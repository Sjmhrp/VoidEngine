#version 330 core

in vec3 vNormal;
in vec2 vTexturePos;

layout (location=0) out vec4 albedo;
layout (location=1) out vec4 normal;
layout (location=2) out vec4 mask;

uniform sampler2D textureSampler;
uniform sampler2D normals;
uniform sampler2D specular;
uniform float hasNormals;
uniform float hasSpecular;
uniform float reflectivity;
uniform float highlight;

void main(void){
	albedo = texture(textureSampler,vTexturePos);
	if(albedo.a<0.5)discard;
	vec4 s = texture(specular,vTexturePos);
	albedo.a=mix(reflectivity,s.r,hasSpecular);
	normal.rgb=mix(0.5+0.5*normalize(vNormal),texture(normals,vTexturePos).rgb,hasNormals);
	mask=vec4(highlight,s.g,0,0);
}