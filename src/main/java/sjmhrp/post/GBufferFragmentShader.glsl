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
	outColour.rgb*=clamp(lighting,0,1+outColour.a);
	float A = projectionMatrix[2][2];
    float B = projectionMatrix[3][2];
    float z = texture(depth,vTexturePos).r;
    float d=B/(z*2-1+A);
	outColour.a=1-clamp(exp(-pow((d*density),gradient)),0,1);
}