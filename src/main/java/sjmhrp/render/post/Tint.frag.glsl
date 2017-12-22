#version 330 core

in vec2 vTexturePos;

out vec4 outColour;

uniform sampler2D textureSampler;
uniform vec3 tintColour;
uniform float opacity;

void main(void) {
	outColour=texture(textureSampler,vTexturePos);
	outColour.rgb=mix(clamp(outColour.rgb,0,1),tintColour,opacity);
}