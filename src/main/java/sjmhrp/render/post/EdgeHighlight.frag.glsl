#version 330 core

in vec2 vTexturePos;

out vec4 outColour;

uniform sampler2D albedo; 
uniform sampler2D mask;

const float threshold = 0.5;
const vec3 outlineColour = vec3(1,1,0);

void main(void) {
    vec3 c = texture(albedo,vTexturePos).rgb;
    float m = texture(mask,vTexturePos).r;
    if(m<0.5) {
		outColour = vec4(c,1);
		return;
    }
	float NW = textureOffset(mask,vTexturePos,ivec2(-1,1)).r;
	float NE = textureOffset(mask,vTexturePos,ivec2(1,1)).r;
	float SW = textureOffset(mask,vTexturePos,ivec2(-1,-1)).r;
	float SE = textureOffset(mask,vTexturePos,ivec2(1,-1)).r;
	
	float Min = min(m,min(min(NW, NE),min(SW, SE)));
	float Max = max(m,max(max(NW, NE),max(SW, SE)));

	if (Max - Min < Max * threshold) {
		outColour = vec4(c,1);
		return;
	}
	outColour=vec4(outlineColour,1);
}