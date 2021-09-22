#version 400 core

const int MAX_WEIGHTS = 3;//max number of joints that can affect a vertex

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec2 pass_textureCoordinates;
out vec3 surfaceNormal;
out vec3 toLightVector[4];
out vec3 toCameraVector;
out float visibility;
out vec4 shadowCoords;

uniform mat4 playerTransformationMatrix;
uniform mat4 posMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[4];
uniform float useFakeLighting;
uniform float atlasSize;
uniform vec2 offset;
uniform vec4 plane;

uniform mat4 jointTransforms[MAX_WEIGHTS];
uniform ivec3 jointIndices;
uniform vec3 jointWeight;

uniform mat4 toShadowMapSpace;
uniform float shadowDistance;

const float fogDensity = 0.0035;
const float fogGradient = 5;
const float transitionDistance = 10.0;

void main(void) {

	vec4 totalLocalPos = vec4(0.0);

	for(int i = 0; i < MAX_WEIGHTS; i++) {

		mat4 jointTransform = jointTransforms[jointIndices[i]];
		vec4 posePosition = jointTransform * (posMatrix * vec4(position, 1.0));
		totalLocalPos += posePosition * jointWeight[i];
	}

	vec4 worldPosition = playerTransformationMatrix * totalLocalPos;
	shadowCoords = toShadowMapSpace * worldPosition;
	
	gl_ClipDistance[0] = dot(worldPosition, plane);
	
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * viewMatrix * worldPosition;	
	pass_textureCoordinates = (textureCoords / atlasSize) + offset;
	
	vec3 actualNormal = normal;
	
	if(useFakeLighting > 0.5) {
		
		actualNormal = vec3(0.0, 1.0, 0.0);
	}
	
	surfaceNormal = (playerTransformationMatrix * vec4(actualNormal, 0.0)).xyz;
	
	for(int i = 0; i < 4; i++) {
	
		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}
	
	toCameraVector = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
	
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance * fogDensity), fogGradient));
	visibility = clamp(visibility, 0.0, 1.0);
}
