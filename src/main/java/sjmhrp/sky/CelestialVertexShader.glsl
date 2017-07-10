#version 330 core

in vec2 position;

out vec2 vTexturePos;

uniform mat4 transformMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main(void){

	gl_Position = projectionMatrix * viewMatrix * transformMatrix * vec4(position,0,1);
	vTexturePos = 0.5*position+0.5;
}