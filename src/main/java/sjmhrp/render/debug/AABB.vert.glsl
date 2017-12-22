#version 330 core

in vec3 position;
in vec3 radius;

out vec3 vRadius;

void main(void){

	gl_Position = vec4(position,1.0);
	vRadius = radius;
}