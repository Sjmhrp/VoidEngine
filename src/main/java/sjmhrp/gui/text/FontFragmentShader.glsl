#version 330 core

in vec2 vTexturePos;

out vec4 outColour;

uniform sampler2D fontAtlas;
uniform vec3 colour;
uniform float opacity;
uniform float outlineWidth;
uniform vec3 outlineColour;
uniform vec2 outlineOffset;

const float width = 0.5;
const float edge = 0.1;

const float outlineEdge = 0.1;

void main(void) {
	float alpha1 = 1-smoothstep(width,width+edge,1-texture(fontAtlas,vTexturePos).a); 
	float alpha2 = 1-smoothstep(outlineWidth,outlineEdge+outlineWidth,1-texture(fontAtlas,vTexturePos+outlineOffset).a);
	outColour.a=alpha1+(1-alpha1)*alpha2;
	outColour.rgb=mix(outlineColour,colour,alpha1/outColour.a);
	outColour.a*=opacity;
}