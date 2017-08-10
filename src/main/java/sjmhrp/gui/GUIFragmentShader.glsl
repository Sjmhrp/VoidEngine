#version 330 core

in vec2 vTexturePos;

out vec4 outColour;

uniform sampler2D textureSampler;
uniform float opacity;
uniform vec3 colour;
uniform float useColour;

void main(void) {
	vec4 c = texture(textureSampler,vTexturePos);
	outColour.rgb=useColour*colour+(1-useColour)*c.rgb;
	outColour.a=c.a*opacity;
}