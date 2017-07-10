#version 330 core

layout (points) in;
layout (triangle_strip,max_vertices=6) out;

in vec3 vColour[];
in float vRadius[];

out vec2 gTexturePos;
out vec3 gColour;

uniform mat4 projectionMatrix;
uniform vec3 sunPosition;

float rand(vec2 co) {
	return fract(sin(dot(co,vec2(12.9898,78.233)))*43758.5453);
}

void createVertex(vec2 offset, vec2 texturePos) {
	gl_Position = projectionMatrix * (vec4(offset,0,0)+gl_in[0].gl_Position);
	gTexturePos = texturePos;
	gColour = vColour[0];
	EmitVertex();
}

void main(void) {
	createVertex(vec2(-vRadius[0],-vRadius[0]),vec2(-1,-1));
	createVertex(vec2(vRadius[0],-vRadius[0]),vec2(1,-1));
	createVertex(vec2(vRadius[0],vRadius[0]),vec2(1,1));
	EndPrimitive();
	createVertex(vec2(-vRadius[0],-vRadius[0]),vec2(-1,-1));
	createVertex(vec2(vRadius[0],vRadius[0]),vec2(1,1));
	createVertex(vec2(-vRadius[0],vRadius[0]),vec2(-1,1));
	EndPrimitive();
}