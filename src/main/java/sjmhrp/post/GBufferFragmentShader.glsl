#version 330 core

in vec2 vTexturePos;

out vec4 outColour;

uniform sampler2D albedo;
uniform sampler2D light;
uniform sampler2D ssao;

void main(void){
	vec3 lighting = texture(light,vTexturePos).rgb;
	lighting+=max(0.2*texture(ssao,vTexturePos).r,0.1);
	outColour = texture(albedo,vTexturePos);
	outColour.rgb*=lighting;
	outColour.a=1;
}