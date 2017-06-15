#version 330 core

in vec2 position;

out vec2 texturePos[11];

uniform float height;

void main(void){
	
	gl_Position = vec4(position.x,position.y,0.0,1.0);
	vec2 center = 0.5+0.5*position;
	for(int i = -5; i <= 5; i++) {
		texturePos[i+5] = vec2(center.x,center.y+(i/height)); 
	}
}