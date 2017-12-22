#version 330 core

in vec3 position;
in vec3 normal;

out vec3 vPos;
out vec3 vNormal;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main(void){
	
	gl_Position = projectionMatrix * viewMatrix * vec4(position,1.0);
	vPos=position;
	vNormal = normal;
}