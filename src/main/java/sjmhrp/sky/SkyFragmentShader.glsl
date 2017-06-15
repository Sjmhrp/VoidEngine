#version 330 core

in vec3 vPosition;
in vec2 vTexturePos;

out vec4 outColour;

uniform sampler2D noise;

const float cloudCover = 128;
const float cloudSharpness = 0.95;
const vec4 C = vec4(0.211324865405187, 0.366025403784439, -0.577350269189626, 0.024390243902439);

vec3 permute(vec3 x) { return mod(((x*34.0)+1.0)*x, 289.0); }

float sNoise(vec2 v){
vec2 i  = floor(v + dot(v, C.yy) );
vec2 x0 = v - i + dot(i, C.xx);
vec2 i1;
  i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
  vec4 x12 = x0.xyxy + C.xxzz;
  x12.xy -= i1;
  i = mod(i, 289.0);
  vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
  + i.x + vec3(0.0, i1.x, 1.0 ));
  vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy),
    dot(x12.zw,x12.zw)), 0.0);
  m = m*m ;
  m = m*m ;
  vec3 x = 2.0 * fract(p * C.www) - 1.0;
  vec3 h = abs(x) - 0.5;
  vec3 ox = floor(x + 0.5);
  vec3 a0 = x - ox;
  m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );
  vec3 g;
  g.x  = a0.x  * x0.x  + h.x  * x0.y;
  g.yz = a0.yz * x12.xz + h.yz * x12.yw;
  return 130.0 * dot(m, g);
}

float cloudExp(float d) {
	float c = d-cloudCover;
	if(c<=0)c=0;
	return 255-pow(cloudSharpness,c)*255;
}

void main(void) {
	vec3 pn = normalize(vPosition);
	float u = 0.5 + atan(pn.z,pn.x)/(2*3.14159265);
	float v = - 0.5 + asin(-pn.y)/3.14159265;
	vec4 cloudColour = vec4(cloudExp(sNoise(vec2(u,v))*255)/255);
	outColour=texture(noise,vTexturePos);
	outColour=vec4(0.141,0.706,1,1);
}