#version 330 core

in vec2 vTexturePos;

out vec4 outColour;

uniform sampler2D textureSampler;
uniform sampler2D flareColour;
uniform int samples;
uniform float dispersal;
uniform float haloWidth;
uniform float distortion;

vec3 textureDistorted(sampler2D sampler, vec2 pos, vec2 dir, vec3 dis) {
	return vec3(texture(sampler,pos+dir*dis.x).x,
				texture(sampler,pos+dir*dis.y).y,
				texture(sampler,pos+dir*dis.z).z);
}

void main(void) {
	vec2 tex = -vTexturePos+vec2(1);
	vec2 ghost = (vec2(0.5)-tex)*dispersal;
	vec2 texelSize = 1/vec2(textureSize(textureSampler,0));
	vec2 dir = normalize(ghost);
	vec3 dis = vec3(-texelSize.x*distortion,0,texelSize.x*distortion);
	for(int i = 0; i < samples; i++) {
		vec2 offset = tex+ghost*i;
		float weight = length(vec2(0.5)-fract(offset))*sqrt(2);
      	weight = pow(1-weight,10);
		outColour.rgb+=textureDistorted(textureSampler,offset,dir,dis)*weight;
	}
	vec2 halo = dir*haloWidth;
	float weight = length(vec2(0.5)-fract(tex+halo))*sqrt(2);
	weight = pow(1-weight,5);
	outColour.rgb+=textureDistorted(textureSampler,tex+halo,dir,dis)*weight;
	outColour*=texture(flareColour,vec2(length(vec2(0.5)-tex)*sqrt(2),1));
}