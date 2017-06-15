#version 330 core

in vec2 vTexturePos;

out vec4 outColour;

const vec2 center = vec2(0,0);
const float radius = 1;

void main(void){
	float d = distance(center,vTexturePos);
	outColour = vec4(1.0);
	outColour.a=pow(1-4/3*(d-0.25),4);
}