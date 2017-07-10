#version 330 core

in vec2 vTexturePos;

out vec4 outColour;

uniform sampler2D textureSampler;

void main(void){
	vec4 c = texture(textureSampler,vTexturePos);
	outColour = c.a*c;
}