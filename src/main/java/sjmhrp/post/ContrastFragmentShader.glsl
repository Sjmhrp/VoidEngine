#version 330 core

in vec2 vTexturePos;

out vec4 outColour;

uniform sampler2D textureSampler;

void main(void){
	outColour = texture(textureSampler,vTexturePos);
	outColour.rgb = (outColour.rgb-0.5) * 1.3 + 0.5;
}