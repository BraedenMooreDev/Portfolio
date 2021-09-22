#version 400 core

const int MAX_JOINTS = 50;
const int MAX_WEIGHTS = 3;

in vec3 in_position;
in vec2 in_textureCoords;
in ivec3 in_jointIndices;
in vec3 in_weights;

out vec2 textureCoords;

uniform mat4 mvpMatrix;
uniform float isAnimModel;
uniform mat4 jointTransforms[MAX_JOINTS];

void main(void){

	vec4 totalLocalPos = vec4(in_position, 1.0);

	if(isAnimModel > 0.5) {

		totalLocalPos = vec4(0.0);

		for(int i = 0; i < MAX_WEIGHTS; i++) {

			mat4 jointTransform = jointTransforms[in_jointIndices[i]];
			vec4 posePosition = jointTransform * vec4(in_position, 1.0);
			totalLocalPos += posePosition * in_weights[i];
		}
	}

	gl_Position = mvpMatrix * totalLocalPos;
	
	textureCoords = in_textureCoords;
}
