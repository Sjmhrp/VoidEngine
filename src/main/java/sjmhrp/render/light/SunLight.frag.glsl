#version 330 core

in vec2 vTexturePos;

layout (location=0) out vec4 lightOutColour;
layout (location=1) out vec4 lightSourceColour;
layout (location=2) out vec4 bloom;

uniform sampler2D albedo;
uniform sampler2D normalMap;
uniform sampler2D mask;
uniform sampler2D depth;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 position;
uniform vec3 colour;

const float dampen = 10.0;

void main(void){
	mat4 projMatrix=inverse(projectionMatrix);
	vec4 c = texture(albedo,vTexturePos);
	vec3 normal = (2*texture(normalMap,vTexturePos)-1).rgb;
	float specular = c.a;
	float z = texture(depth,vTexturePos).r*2-1;
	vec4 projPos = vec4(vTexturePos*2-1,z,1.0);
	projPos = projMatrix*projPos;
	vec3 pos = projPos.xyz/projPos.w;
	vec4 worldPos = inverse(viewMatrix) * vec4(pos,1.0);
	//vec3 toCamera = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPos.xyz;
	vec3 toCamera = -pos;
	vec3 light = normalize(position);
	vec3 lightDir = reflect(-light,normal);
	
	float totalSpecular = pow(max(dot(normalize(toCamera),lightDir),0.0),dampen) * specular;
	vec3 diffuse = colour * max(dot(normal,light),0);
	vec3 specularColour = totalSpecular * colour;
	
	lightOutColour.rgb=diffuse;
	lightOutColour.rgb+=specularColour;
	lightOutColour.a=1.0;
	lightSourceColour.rgb=specularColour;
	
	bloom=c+vec4(specularColour,1);
	bloom*=texture(mask,vTexturePos).g;
}