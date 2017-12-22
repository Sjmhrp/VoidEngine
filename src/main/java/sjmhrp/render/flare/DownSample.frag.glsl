#version 330 core

in vec2 vTexturePos;

out vec4 outColour;

uniform sampler2D textureSampler;
uniform vec3 bias;

void main(void) {
	outColour=max(vec4(0),texture(textureSampler,vTexturePos)-vec4(bias,0));
}