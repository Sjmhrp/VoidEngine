package sjmhrp.flare;

import sjmhrp.linear.Matrix3d;
import sjmhrp.shaders.MultiTextureShaderProgram;
import sjmhrp.shaders.PostShaderProgram;

public class FlareShader extends PostShaderProgram implements MultiTextureShaderProgram {

	private int location_flareTex;
	private int location_starTex;
	private int location_starMatrix;
	
	public FlareShader() {
		super("post/Generic","flare/Flare");
	}
	
	@Override
	public void getAllUniformLocations() {
		location_flareTex = getUniformLocation("flareTex");
		location_starTex = getUniformLocation("starTex");
		location_starMatrix = getUniformLocation("starMatrix");
	}
	
	public void loadStarMatrix(Matrix3d matrix) {
		load3Matrix(location_starMatrix,matrix);
	}
	
	public void connectTextures() {
		loadInt(location_flareTex,0);
		loadInt(location_starTex,1);
	}
}