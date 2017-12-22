#version 330 core

in vec2 vPos1;
in vec2 vPos2;
in float vDepth;
in float vBlend;
in float vAge;
in mat4 vModelViewMatrix;

out vec4 outColour;

uniform sampler2D albedo;

void main(void){
	outColour = mix(texture(albedo,vPos1),texture(albedo,vPos2),vBlend);
	outColour.a*=1-vAge;
}