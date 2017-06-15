#version 330 core

in vec3 position;
in vec2 texturePos;
in vec3 normal;

out vec2 vTexturePos;
out vec3 vNormal;

uniform mat4 transformMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main(void){
	
	gl_Position = projectionMatrix * viewMatrix * transformMatrix * vec4(position,1.0);
	vTexturePos = texturePos;
	vNormal = normal;
}