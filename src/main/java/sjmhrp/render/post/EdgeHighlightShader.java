package sjmhrp.render.post;

import sjmhrp.render.shader.MultiTextureShader;
import sjmhrp.render.shader.PostShaderProgram;

public class EdgeHighlightShader extends PostShaderProgram implements MultiTextureShader{

	private int location_albedo;
	private int location_mask;
	
	public EdgeHighlightShader() {
		super("render/post/Generic","render/post/EdgeHighlight");
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_albedo = getUniformLocation("albedo");
		location_mask = getUniformLocation("mask");
	}
	
	@Override
	public int[] getTextures(int initial) {
		return new int[] {initial,Post.mask.getColourTexture()};
	}
	
	public void connectTextures() {
		loadInt(location_albedo,0);
		loadInt(location_mask,1);
	}
}