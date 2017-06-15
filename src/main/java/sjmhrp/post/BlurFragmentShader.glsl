#version 330 core

in vec2 texturePos[11];

out vec4 outColour;

uniform sampler2D textureSampler;

const float[] weights = {0.084264,0.088139,0.091276,0.093585,0.094998,0.095474,0.094998,0.093585,0.091276,0.088139,0.084264};
const float[] weights1 = {0.066414,0.079465,0.091364,0.100939,0.107159,0.109317,0.107159,0.100939,0.091364,0.079465,0.066414};
const float[] weights2 = {0.0093,0.028002,0.065984,0.121703,0.175713,0.198596,0.175713,0.121703,0.065984,0.028002,0.0093};
const float[] weights3 = {0.000003,0.000229,0.005977,0.060598,0.24173,0.382925,0.24173,0.060598,0.005977,0.000229,0.000003};

void main(void){
	for(int i=0;i<11;i++) {
		outColour+=texture(textureSampler,texturePos[i])*weights[i];
	}
}