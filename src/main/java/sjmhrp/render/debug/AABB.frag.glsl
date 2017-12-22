#version 330 core

layout (location = 0) out vec4 colour;
layout (location = 1) out vec4 normal;

in vec2 gBarycentric;

void main(void){
	if(gBarycentric.x>0.02&&gBarycentric.x<0.98&&gBarycentric.y>0.02&&gBarycentric.y<0.98)discard;
	colour=vec4(1);
	colour.a=0;
	normal=vec4(0,1,0,1);
}