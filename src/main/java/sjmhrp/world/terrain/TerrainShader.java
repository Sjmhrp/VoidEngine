package sjmhrp.world.terrain;

import sjmhrp.render.shader.MultiTextureShader;
import sjmhrp.render.shader.ShaderProgram;
import sjmhrp.utils.linear.Matrix4d;

public class TerrainShader extends ShaderProgram implements MultiTextureShader {
		
	private int location_viewMatrix;
	private int location_background;
	private int location_rTexture;
	private int location_gTexture;
	private int location_bTexture;
	private int location_blendMap;
	private int location_useBlend;
	
	public TerrainShader() {
		super("world/terrain/Terrain",false);
	}

	@Override
	protected void bind() {
		bindAttribute(0,"position");
		bindAttribute(1,"normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_background = getUniformLocation("background");
		location_rTexture = getUniformLocation("rTexture");
		location_gTexture = getUniformLocation("gTexture");
		location_bTexture = getUniformLocation("bTexture");
		location_blendMap = getUniformLocation("blendMap");
		location_useBlend = getUniformLocation("useBlend");
	}
	
	public void connectTextures() {
		loadInt(location_background,0);
		loadInt(location_rTexture,1);
		loadInt(location_gTexture,2);
		loadInt(location_bTexture,3);
		loadInt(location_blendMap,4);
	}
	
	public void loadUseBlend(boolean b) {
		loadBoolean(location_useBlend,b);
	}
	
	public void loadViewMatrix(Matrix4d matrix) {
		load4Matrix(location_viewMatrix, matrix);
	}
}