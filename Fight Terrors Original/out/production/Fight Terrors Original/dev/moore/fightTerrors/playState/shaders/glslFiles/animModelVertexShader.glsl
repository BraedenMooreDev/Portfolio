#version 400 core

const int MAX_JOINTS = 50;//max joints allowed in a skeleton
const int MAX_WEIGHTS = 3;//max number of joints that can affect a vertex

in vec3 position;
in vec2 textureCoords;
in vec3 normal;
in ivec3 jointIndices;
in vec3 weights;

out vec2 pass_textureCoordinates;
out vec3 surfaceNormal;
out vec3 toLightVector[4];
out vec3 toCameraVector;
out float visibility;
out vec4 shadowCoords;

uniform mat4 jointTransforms[MAX_JOINTS];
uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPosition[4];
uniform float useFakeLighting;
uniform float atlasSize;
uniform vec2 offset;
uniform vec4 plane;

uniform mat4 toShadowMapSpace;
uniform float shadowDistance;

const float fogDensity = 0.0035;
const float fogGradient = 5;
const float transitionDistance = 10.0;

void main(void) {
	
	vec4 totalLocalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);
	
	for(int i = 0; i < MAX_WEIGHTS; i++) {

		mat4 jointTransform = jointTransforms[jointIndices[i]];
		vec4 posePosition = jointTransform * vec4(position, 1.0);
		totalLocalPos += posePosition * weights[i];
		
		vec4 worldNormal = jointTransform * vec4(normal, 0.0);
		totalNormal += worldNormal * weights[i];
	}
	
	vec4 worldPosition = transformationMatrix * totalLocalPos;
	shadowCoords = toShadowMapSpace * worldPosition;

	gl_ClipDistance[0] = dot(worldPosition, plane);

	gl_Position = projectionMatrix * viewMatrix * worldPosition;
	pass_textureCoordinates = (textureCoords / atlasSize) + offset;

	if(useFakeLighting > 0.5) {

		totalNormal = vec4(0.0, 1.0, 0.0, 0.0);
	}

	surfaceNormal = (transformationMatrix * totalNormal).xyz;

	for(int i = 0; i < 4; i++) {

		toLightVector[i] = lightPosition[i] - worldPosition.xyz;
	}

	vec4 positionRelativeToCam = viewMatrix * worldPosition;

	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow((distance * fogDensity), fogGradient));
	visibility = clamp(visibility, 0.0, 1.0);
}
