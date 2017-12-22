package sjmhrp.render.post;

import sjmhrp.render.shader.MultiTextureShader;
import sjmhrp.render.shader.ShaderProgram;

public class GBufferShader extends ShaderProgram implements MultiTextureShader {

	private int location_albedo;
	private int location_light;
	private int location_ssao;
	private int location_bloom;
	private int location_depth;
	
	public GBufferShader() {
		super("render/post/Generic","render/post/GBuffer");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_albedo = getUniformLocation("albedo");
		location_light = getUniformLocation("light");
		location_ssao = getUniformLocation("ssao");
		location_bloom = getUniformLocation("bloom");
		location_depth = getUniformLocation("depth");
	}

	@Override
	protected void bind() {
		bindAttribute(0,"position");
	}

	public void connectTextures() {
		loadInt(location_albedo,0);
		loadInt(location_light,1);
		loadInt(location_ssao,2);
		loadInt(location_bloom,3);
		loadInt(location_depth,4);
	}
}