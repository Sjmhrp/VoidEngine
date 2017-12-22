#version 330 core

in vec2 vTexturePos;

out vec4 outColour;

uniform sampler2D textureSampler; 
uniform vec2 size;

const float lumaThreshold = 0.5;
const float mulReduce = 0.125;
const float minReduce = 0.0078125;
const float maxSpan = 8;

void main(void) {
    vec3 rgbM = texture(textureSampler,vTexturePos).rgb;
	vec3 rgbNW = textureOffset(textureSampler,vTexturePos,ivec2(-1,1)).rgb;
    vec3 rgbNE = textureOffset(textureSampler,vTexturePos,ivec2(1,1)).rgb;
    vec3 rgbSW = textureOffset(textureSampler,vTexturePos,ivec2(-1,-1)).rgb;
    vec3 rgbSE = textureOffset(textureSampler,vTexturePos,ivec2(1,-1)).rgb;
	
	const vec3 toLuma = vec3(0.299, 0.587, 0.114);
	float lumaNW = dot(rgbNW, toLuma);
	float lumaNE = dot(rgbNE, toLuma);
	float lumaSW = dot(rgbSW, toLuma);
	float lumaSE = dot(rgbSE, toLuma);
	float lumaM = dot(rgbM, toLuma);
	
	float lumaMin = min(lumaM, min(min(lumaNW, lumaNE), min(lumaSW, lumaSE)));
	float lumaMax = max(lumaM, max(max(lumaNW, lumaNE), max(lumaSW, lumaSE)));

	if (lumaMax - lumaMin < lumaMax * lumaThreshold) {
		outColour = vec4(rgbM,1.0);
		return;
	}

	vec2 samplingDirection;	
	samplingDirection.x = -((lumaNW + lumaNE) - (lumaSW + lumaSE));
    samplingDirection.y =  ((lumaNW + lumaSW) - (lumaNE + lumaSE));
    
    float samplingDirectionReduce = max((lumaNW + lumaNE + lumaSW + lumaSE) * 0.25 * mulReduce, minReduce);
	float minSamplingDirectionFactor = 1.0 / (min(abs(samplingDirection.x), abs(samplingDirection.y)) + samplingDirectionReduce);
	samplingDirection = clamp(samplingDirection * minSamplingDirectionFactor, vec2(-maxSpan, -maxSpan), vec2(maxSpan, maxSpan)) * size;
	
	vec3 rgbSampleNeg = texture(textureSampler,vTexturePos+samplingDirection*(1.0/3.0-0.5)).rgb;
	vec3 rgbSamplePos = texture(textureSampler,vTexturePos+samplingDirection*(2.0/3.0-0.5)).rgb;
	vec3 rgbTwoTab = (rgbSamplePos+rgbSampleNeg)*0.5;

	vec3 rgbSampleNegOuter = texture(textureSampler,vTexturePos+samplingDirection*(0.0/3.0-0.5)).rgb;
	vec3 rgbSamplePosOuter = texture(textureSampler,vTexturePos+samplingDirection*(3.0/3.0-0.5)).rgb;
	vec3 rgbFourTab = (rgbSamplePosOuter+rgbSampleNegOuter)*0.25+rgbTwoTab*0.5;   
	
	float lumaFourTab = dot(rgbFourTab, toLuma);
	
	if(lumaFourTab < lumaMin || lumaFourTab > lumaMax) {
		outColour = vec4(rgbTwoTab,1.0);
	} else {
		outColour = vec4(rgbFourTab,1.0);
	}
}