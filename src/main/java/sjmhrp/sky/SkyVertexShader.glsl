#version 330 core

in vec3 position;
in vec2 texturePos;
in vec3 normal;

out vec3 vPosition;
out vec2 vTexturePos;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform float size;

void main(void) {
	gl_Position = projectionMatrix * viewMatrix * vec4(size*position,1);
	vTexturePos = vec2(-texturePos.y,texturePos.x);
	vPosition = position;
}