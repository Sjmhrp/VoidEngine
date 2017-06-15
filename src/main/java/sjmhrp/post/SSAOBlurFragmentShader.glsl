#version 330 core

in vec2 vTexturePos;

out vec4 outColour;

uniform sampler2D textureSampler;

void main(void){
	
	vec2 texelSize = 1.0/vec2(textureSize(textureSampler,0));
	float result = 0.0;
	for(int x = -2; x < 2; x++) {
		for(int y = -2; y < 2; y++) {
			vec2 offset = vec2(float(x),float(y))*texelSize;
			result+=texture(textureSampler,vTexturePos+offset).r;
		}
	}
	result/=16.0;
	outColour=vec4(result);
	outColour.a=1.0;
}