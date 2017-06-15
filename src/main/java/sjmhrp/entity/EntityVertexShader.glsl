#version 330 core

in vec3 position;
in vec2 texturePos;
in vec3 normal;

out vec2 vTexturePos;
out vec3 vNormal;

uniform vec2 offset;
uniform float rows;

uniform mat4 transformMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform float fakeLighting;

void main(void){
	gl_Position = projectionMatrix * viewMatrix * transformMatrix * vec4(position,1.0);
	vTexturePos = (texturePos/rows)+offset;
	vNormal=(1-fakeLighting)*(inverse(transpose(transformMatrix))*vec4(normal,1.0)).rgb;
	vNormal+=vec3(0.0,1.0,0.0) * fakeLighting;
}