#version 330 core

in vec2 gTexturePos;
in vec3 gColour;

out vec4 outColour;

uniform vec3 sunPosition;

void main(void) {
	outColour.rgb=gColour;
	outColour.a=min(pow(max(1-4/3*(length(gTexturePos)-0.25),0),4),1);
	outColour.rgb*=outColour.a;
}