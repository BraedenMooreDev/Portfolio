#version 400 core

in vec2 pass_textureCoordinates;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;
in vec4 shadowCoords;

out vec4 out_color;

uniform sampler2D modelTexture;
uniform vec3 lightColor[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;
uniform sampler2D shadowMap;
uniform float shadowMapSize;
uniform int pcfCount;

void main(void) {

	float totalTexels = (pcfCount * 2.0 + 1.0) * (pcfCount * 2.0 + 1.0);

		float texelSize = 1.0 / shadowMapSize;
		float total = 0.0;

		for(int x = -pcfCount; x <= pcfCount; x++) {

			for(int y = -pcfCount; y <= pcfCount; y++) {

				float objectNearestLight = texture(shadowMap, shadowCoords.xy + vec2(x, y) * texelSize).r;

				if(shadowCoords.z > objectNearestLight + 0.02) {

					total += 1.0;
				}
			}
		}

		total /= totalTexels;

		float lightFactor = 1.0 - (total * shadowCoords.w);

		vec3 unitNormal = normalize(surfaceNormal);

		vec3 totalDiffuse = vec3(0.0);

		//Issue has to do with surface normals from animated model

		for(int i = 0; i < 4; i++) {

			vec3 unitLightVector = normalize(toLightVector[i]);
			float brightness = max(dot(unitNormal, unitLightVector), 0.0);

			totalDiffuse += brightness * lightColor[i];
		}

		totalDiffuse = max(totalDiffuse, 0.3);

		vec4 textureColor = texture(modelTexture, pass_textureCoordinates);

		if(textureColor.a < 0.5) {

			discard;
		}

		//Would normally multiply by the 4D totalDiffuse, but it causes vertices from the model to disappear.
		//I will try to fix this issue once I learn more about GLSL programs, I am going to work on things I know I can do.

		out_color = textureColor /* vec4(totalDiffuse, 1.0) */;
}
