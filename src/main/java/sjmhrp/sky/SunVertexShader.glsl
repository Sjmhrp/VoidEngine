#version 330 core

in vec2 position;

out vec2 vTexturePos;

uniform mat4 transformMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main(void){
	
	gl_Position = projectionMatrix * viewMatrix * transformMatrix * vec4(position.x,position.y,0.0,1.0);
	vTexturePos = position;
}