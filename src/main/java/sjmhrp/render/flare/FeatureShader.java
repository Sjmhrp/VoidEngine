package sjmhrp.render.flare;

import sjmhrp.render.shader.MultiTextureShader;
import sjmhrp.render.shader.PostShaderProgram;

public class FeatureShader extends PostShaderProgram implements MultiTextureShader {

	private int location_texture;
	private int location_flareColour;
	private int location_samples;
	private int location_dispersal;
	private int location_haloWidth;
	private int location_distortion;
	
	public FeatureShader() {
		super("render/post/Generic","render/flare/Feature");
	}
	
	@Override
	public void getAllUniformLocations() {
		location_texture = getUniformLocation("textureSampler");
		location_flareColour = getUniformLocation("flareColour");
		location_samples = getUniformLocation("samples");
		location_dispersal = getUniformLocation("dispersal");
		location_haloWidth = getUniformLocation("haloWidth");
		location_distortion = getUniformLocation("distortion");
	}
	
	public void load(int samples, double dispersal, double haloWidth, double distortion) {
		loadInt(location_samples,samples);
		loadFloat(location_dispersal,dispersal);
		loadFloat(location_haloWidth,haloWidth);
		loadFloat(location_distortion,distortion);
	}
	
	public void connectTextures() {
		loadInt(location_texture,0);
		loadInt(location_flareColour,1);
	}
}