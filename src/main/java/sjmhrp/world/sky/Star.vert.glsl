#version 330 core

in vec3 position;
in vec3 colour;
in float radius;

out vec3 vColour;
out float vRadius;

uniform mat4 viewMatrix;
uniform float domeSize;

void main(void) {
	
	gl_Position = viewMatrix * vec4(normalize(position)*domeSize,1);
	vColour = colour;
	vRadius = radius;
}