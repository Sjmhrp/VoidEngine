#version 330 core

in vec2 position;

out vec2 vTexturePos;

uniform vec2 offset;
uniform vec2 size;
uniform mat2 rot;

void main(void) {
	gl_Position=vec4(rot*position*size+offset,0,1);
	vTexturePos=0.5+0.5*position;
}