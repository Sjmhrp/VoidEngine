#version 330 core

in vec3 position;
in vec2 texturePos;
in vec3 normal;

out vec3 vPosition;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform float domeSize;

void main(void) {
	vec3 pos = normalize(position);
	gl_Position = projectionMatrix * viewMatrix * vec4(domeSize*pos,1);
	vPosition = pos;
}