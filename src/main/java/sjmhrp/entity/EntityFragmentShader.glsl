#version 330 core

in vec3 vNormal;
in vec2 vTexturePos;

layout (location=0) out vec4 albedo;
layout (location=1) out vec4 normal;

uniform sampler2D textureSampler;
uniform sampler2D normals;
uniform sampler2D specular;
uniform float hasNormals;
uniform float hasSpecular;
uniform float reflectivity;

void main(void){
	albedo = texture(textureSampler,vTexturePos);
	if(albedo.a<0.5)discard;
	albedo.a=reflectivity*(1-hasSpecular);
	albedo.a+=texture(specular,vTexturePos).r*hasSpecular;
	normal.rgb=(0.5+0.5*normalize(vNormal))*(1-hasNormals);
	normal.rgb+=texture(normals,vTexturePos).rgb*hasNormals;
}