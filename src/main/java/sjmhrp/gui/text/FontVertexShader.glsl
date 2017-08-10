#version 330 core

in vec2 position;
in vec2 texturePos;

out vec2 vTexturePos;

uniform vec2 offset;
uniform float size;

void main(void) {
	gl_Position = vec4(position*size+offset,0,1);
	vTexturePos=texturePos;
}