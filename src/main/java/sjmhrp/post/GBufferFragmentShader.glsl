#version 330 core

in vec2 vTexturePos;

out vec4 outColour;

uniform sampler2D albedo;
uniform sampler2D light;
uniform sampler2D ssao;
uniform sampler2D depth;
uniform mat4 projectionMatrix;

const float density = 0.0035;
const float gradient = 5;

void main(void){
	vec3 lighting = texture(light,vTexturePos).rgb;
	lighting+=max(0.2*texture(ssao,vTexturePos).r,0.1);
	outColour = texture(albedo,vTexturePos);
	outColour.rgb*=lighting;
	float z = texture(depth,vTexturePos).r*2-1;
	vec4 projPos = inverse(projectionMatrix)*vec4(vTexturePos*2-1,z,1.0);
	vec3 pos = projPos.xyz/projPos.w;
	outColour.a=1-clamp(exp(-pow((pos.z*density),gradient)),0,1);
}