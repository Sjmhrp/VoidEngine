#version 330 core

in vec2 vTexturePos;
in vec3 vNormal;

layout (location=0) out vec4 albedo;
layout (location=1) out vec4 normal;

uniform sampler2D background;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;
uniform float useBlend;

void main(void){
	vec4 blend = texture(blendMap,vTexturePos);
	blend*=useBlend;
	vec2 texturePos = vTexturePos*40;
	vec4 back = texture(background,texturePos)*(1-blend.r-blend.g-blend.b);
	vec4 r = texture(rTexture,texturePos)*blend.r;
	vec4 g = texture(gTexture,texturePos)*blend.g;
	vec4 b = texture(bTexture,texturePos)*blend.b;
	albedo=back+r+g+b;
	albedo.a=0;
	normal.rgb=0.5+0.5*normalize(vNormal);
}