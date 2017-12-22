#version 330 core

in vec3 position;
in vec2 texturePos;
in vec3 normal;
in ivec3 jointIndices;
in vec3 weights;

out vec2 vTexturePos;
out vec3 vNormal;

uniform vec2 offset;
uniform float rows;

uniform mat4 transformMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 jointTransforms[50];
uniform float fakeLighting;
uniform float isAnimated;
uniform float isWireFrame;

void main(void){
	vec4 totalPos = vec4(0.0);
	vec4 totalNormal = vec4(0.0);
	for(int i = 0; i < 3; i++) {
		vec4 localPos = jointTransforms[jointIndices[i]] * vec4(position,1.0);
		totalPos += localPos * weights[i];
		
		vec4 worldNormal = jointTransforms[jointIndices[i]] * vec4(normal,0.0);
		totalNormal += worldNormal * weights[i];
	}
	gl_Position = projectionMatrix * viewMatrix * transformMatrix * mix(vec4(position,1),totalPos,isAnimated);
	vTexturePos = (texturePos/rows)+offset;
	vNormal=mix((inverse(transpose(transformMatrix))*mix(vec4(normal,1),totalNormal,isAnimated)).rgb,vec3(0,1,0),fakeLighting);
}