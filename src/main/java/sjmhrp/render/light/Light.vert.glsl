#version 330 core

in vec3 position;

out vec2 vTexturePos;

uniform mat4 transformMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main(void){
	gl_Position = projectionMatrix * viewMatrix * transformMatrix * vec4(position,1.0);
	vTexturePos = 0.5+0.5*(gl_Position.xy/gl_Position.w);
}