#version 330 core

in vec2 vTexturePos;

out vec4 outColour;

uniform sampler2D flareTex;
uniform sampler2D starTex;
uniform mat3 starMatrix;

void main(void) {
	vec2 tex = (starMatrix*vec3(vTexturePos,1)).xy;
	outColour=texture(flareTex,vTexturePos)*(texture(starTex,tex)+vec4(1,1,1,0));
}