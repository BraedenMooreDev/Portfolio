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
	vec3 unitCameraVector = normalize(toCameraVector);
	
	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	
	for(int i = 0; i < 4; i++) {
	
		float distance = length(toLightVector[i]);
		float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
	
		vec3 unitLightVector = normalize(toLightVector[i]);
		float nDot1 = dot(unitNormal, unitLightVector);
		float brightness = max(nDot1, 0.0);	
		vec3 lightDir = -unitLightVector;
		vec3 reflectedLightDir = reflect(lightDir, unitNormal);
		float nDot2 = dot(reflectedLightDir, unitCameraVector);
		nDot2 = max(nDot2, 0.0);
		float dampedFactor = pow(nDot2, shineDamper);
		totalDiffuse =  totalDiffuse + (brightness * lightColor[i]) / attFactor;
		totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColor[i]) / attFactor;
	}

	totalDiffuse = max(totalDiffuse * lightFactor, 0.3);

	vec4 textureColor = texture(modelTexture, pass_textureCoordinates);
	
	if(textureColor.a < 0.5) {
		
		discard;
	}
	
	out_color = vec4(totalDiffuse, 1.0) * texture(modelTexture, pass_textureCoordinates) + vec4(totalSpecular, 1.0);
	out_color = mix(vec4(skyColor, 1.0), out_color, visibility);
}