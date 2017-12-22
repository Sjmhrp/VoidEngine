#version 330 core

in vec2 vTexturePos;

out vec4 outColour;

uniform sampler2D depth;
uniform sampler2D normalMap;
uniform sampler2D noise;

uniform vec3 samples[64];
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec2 resolution;

vec2 noiseScale = vec2(resolution.x/4.0,resolution.y/4.0);

const int kernelSize = 64;
const float radius = 0.5;
const float bias = 0.025;

void main(void){

	mat4 inverseProjMatrix = inverse(projectionMatrix);

	float z = texture(depth,vTexturePos).r*2-1;
	vec4 pos = vec4(vTexturePos*2-1,z,1.0);
	pos = inverseProjMatrix*pos;
	
	vec3 position = pos.xyz/pos.w;
	vec3 normal = 2*texture(normalMap,vTexturePos).xyz-1;
	vec3 randomVec = texture(noise,vTexturePos*noiseScale).xyz;
	
	vec3 tangent = normalize(randomVec - normal * dot(normal,randomVec));
	vec3 bitangent = cross(normal,tangent);
	mat3 TBN = mat3(tangent,bitangent,normal);
	TBN = mat3(inverse(transpose(viewMatrix))*mat4(TBN));
	float occlusion = 0.0;
	for(int i = 0; i < kernelSize; i++) {
		vec3 s = TBN * samples[i];
		s = position + s * radius;
		vec4 offset = vec4(s,1.0);
		offset = projectionMatrix * offset;
		offset.xyz/=offset.w;
		offset.xyz = 0.5+0.5*offset.xyz;
		vec4 projPos = vec4(offset.x*2-1,offset.y*2-1,texture(depth,offset.xy).r*2-1,1.0);
		projPos = inverseProjMatrix * projPos;
		projPos/=projPos.w;
		float sampleDepth = projPos.z;
		float rangeCheck = smoothstep(0.0, 1.0, radius / abs(position.z - sampleDepth));
		occlusion += (sampleDepth >= s.z + bias ? 1.0*rangeCheck : 0.0);  
	}
	occlusion=1-occlusion/kernelSize;
	outColour=vec4(occlusion);
}