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
uniform vec3 lightPos;
uniform vec3 attenuation;
uniform vec3 lightColour;
uniform float lightSize;

const float dampen = 10.0;

void main(void) {
	mat4 projMatrix=inverse(projectionMatrix);
	vec4 c = texture(albedo,vTexturePos);
	vec3 normal = (2*texture(normalMap,vTexturePos)-1).rgb;
	float z = texture(depth,vTexturePos).r*2-1;
	vec4 projPos = vec4(vTexturePos*2-1,z,1.0);
	projPos = projMatrix*projPos;
	vec3 pos = projPos.xyz/projPos.w;
	vec4 worldPos = inverse(viewMatrix)*vec4(pos,1.0);
	vec3 light = lightPos-worldPos.xyz;
	vec3 toCamera = (inverse(viewMatrix) * vec4(0.0,0.0,0.0,1.0)).xyz - worldPos.xyz;
	vec3 lightDir = normalize(light);
	vec3 lightSpec = reflect(-lightDir,normal);

	float d = length(light);
	float att = attenuation.x+d*attenuation.y+d*d*attenuation.z;
	
	float totalSpecular = pow(max(dot(normalize(toCamera),lightSpec),0.0),dampen) * c.a/att;
	float totalDiffuse = max(dot(normal,lightDir),0.0);
	vec3 diffuse = lightColour * max(totalDiffuse,0.0)/att;
	vec3 specularColour = totalSpecular * lightColour;

	lightOutColour=vec4(diffuse,1.0);
	lightOutColour.rgb+=specularColour;
	vec4 viewSpace = viewMatrix*vec4(lightPos,1);
	vec4 a = projectionMatrix*viewSpace;
	a/=a.w;
	a=0.5+0.5*a;
	if(a.z>0&&a.z<(0.5+0.5*z))lightSourceColour.rgb=min(pow(max(1-4/3*(distance(vTexturePos,a.xy)*16-0.25),0),4),1)*lightColour;
	lightSourceColour.rgb+=specularColour;
	
	bloom=c+vec4(specularColour,1);
	bloom*=texture(mask,vTexturePos).g;
}