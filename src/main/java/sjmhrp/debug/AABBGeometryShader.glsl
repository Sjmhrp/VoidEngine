#version 330 core

layout (points) in;
layout (triangle_strip, max_vertices=24) out;

in vec3 vRadius[];

out vec2 gBarycentric;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

int count = 0;

void createVertex(vec3 offset) {
	vec4 o = vec4(offset,0.0);
	gl_Position = projectionMatrix * viewMatrix * (o+gl_in[0].gl_Position);
	gBarycentric=vec2(count%2,int(count/2));
	count++;
	count=count%4;
	EmitVertex();
}

void main(void){
	createVertex(vec3(-vRadius[0].x, vRadius[0].y, vRadius[0].z));
	createVertex(vec3(-vRadius[0].x, -vRadius[0].y, vRadius[0].z));
	createVertex(vec3(vRadius[0].x, vRadius[0].y, vRadius[0].z));
	createVertex(vec3(vRadius[0].x, -vRadius[0].y, vRadius[0].z));
	EndPrimitive();
	createVertex(vec3(vRadius[0].x, vRadius[0].y, vRadius[0].z));
	createVertex(vec3(vRadius[0].x, -vRadius[0].y, vRadius[0].z));
	createVertex(vec3(vRadius[0].x, vRadius[0].y, -vRadius[0].z));
	createVertex(vec3(vRadius[0].x, -vRadius[0].y, -vRadius[0].z));
	EndPrimitive();
	createVertex(vec3(vRadius[0].x, vRadius[0].y, -vRadius[0].z));
	createVertex(vec3(vRadius[0].x, -vRadius[0].y, -vRadius[0].z));
	createVertex(vec3(-vRadius[0].x, vRadius[0].y, -vRadius[0].z));
	createVertex(vec3(-vRadius[0].x, -vRadius[0].y, -vRadius[0].z));
	EndPrimitive();
	createVertex(vec3(-vRadius[0].x, vRadius[0].y, -vRadius[0].z));
	createVertex(vec3(-vRadius[0].x, -vRadius[0].y, -vRadius[0].z));
	createVertex(vec3(-vRadius[0].x, vRadius[0].y, vRadius[0].z));
	createVertex(vec3(-vRadius[0].x, -vRadius[0].y, vRadius[0].z));
	EndPrimitive();
	createVertex(vec3(vRadius[0].x, vRadius[0].y, vRadius[0].z));
	createVertex(vec3(vRadius[0].x, vRadius[0].y, -vRadius[0].z));
	createVertex(vec3(-vRadius[0].x, vRadius[0].y, vRadius[0].z));
	createVertex(vec3(-vRadius[0].x, vRadius[0].y, -vRadius[0].z));
	EndPrimitive();
	createVertex(vec3(-vRadius[0].x, -vRadius[0].y, vRadius[0].z));
	createVertex(vec3(-vRadius[0].x, -vRadius[0].y, -vRadius[0].z));
	createVertex(vec3(vRadius[0].x, -vRadius[0].y, vRadius[0].z));
	createVertex(vec3(vRadius[0].x, -vRadius[0].y, -vRadius[0].z));
	EndPrimitive();
}