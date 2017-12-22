#version 330 core

in vec2 position;
in mat4 modelViewMatrix;
in vec4 textureOffsets;
in float blend;
in float age;

out vec2 vPos1;
out vec2 vPos2;
out float vDepth;
out float vBlend;
out float vAge;

uniform mat4 projectionMatrix;
uniform float rows;

void main(void){
	vec4 viewSpace = modelViewMatrix * vec4(position.x,position.y,0.0,1.0);
	gl_Position = projectionMatrix * viewSpace;
	vec2 tex = 0.5+0.5*position;
	tex/=rows;
	vPos1=tex+textureOffsets.xy;
	vPos2=tex+textureOffsets.zw;
	vDepth = viewSpace.z;
	vBlend = blend;
	vAge = age;
}