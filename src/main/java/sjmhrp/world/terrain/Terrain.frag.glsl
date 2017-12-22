#version 330 core

in vec3 vPos;
in vec3 vNormal;

layout (location=0) out vec4 albedo;
layout (location=1) out vec4 normal;
layout (location=2) out vec4 mask;

uniform sampler2D background;
uniform sampler2D rTexture;
uniform sampler2D gTexture;
uniform sampler2D bTexture;
uniform sampler2D blendMap;
uniform float useBlend;

const float scale = 0.05;

vec4 sample(sampler2D sampler) {
	vec3 blending = abs(vNormal)-0.2;
	blending*=7;
	blending.x=pow(blending.x,3);
	blending.y=pow(blending.y,3);
	blending.z=pow(blending.z,3);
	blending = max(blending,0);
	blending /= dot(blending,vec3(1));
	vec4 xaxis = texture(sampler,vPos.yz*scale);
	vec4 yaxis = texture(sampler,vPos.xz*scale);
	vec4 zaxis = texture(sampler,vPos.xy*scale);
	return xaxis * blending.x + yaxis * blending.y + zaxis * blending.z;
}

void main(void){
	if(useBlend>0.5) {
		vec4 blend = sample(blendMap);
		vec4 back = sample(background)*(1-blend.r-blend.g-blend.b);
		vec4 r = sample(rTexture)*blend.r;
		vec4 g = sample(gTexture)*blend.g;
		vec4 b = sample(bTexture)*blend.b;
		albedo=back+r+g+b;
	} else {
		albedo = sample(background);
	}
	albedo.a=0;
	normal.rgb=0.5+0.5*normalize(vNormal);
	mask=vec4(0);
}