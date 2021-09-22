#version 400

in vec3 textureCoords;

out vec4 out_color;

uniform samplerCube cubeMapDay;
uniform samplerCube cubeMapNight;
uniform float blendFactor;
uniform vec3 fogColor;

const float lowerLimit = 0.0;
const float upperLimit = 50.0;

void main(void) {

	vec4 texture1 = texture(cubeMapDay, textureCoords);
	vec4 texture2 = texture(cubeMapNight, textureCoords);

	vec4 finalColor = mix(texture1, texture2, blendFactor);
	
	float factor = (textureCoords.y - lowerLimit)/ (upperLimit - lowerLimit);
	factor = clamp(factor, 0.0, 1.0);
	out_color = mix(vec4(fogColor, 1.0), finalColor, factor);
}