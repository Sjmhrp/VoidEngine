#version 330 core

in vec2 position;

out vec2 vTexturePos;

void main(void){
	
	gl_Position = vec4(position.x,position.y,0.0,1.0);
	vTexturePos = 0.5+0.5*position;
}