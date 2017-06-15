package sjmhrp.terrain;

import sjmhrp.linear.Matrix4d;
import sjmhrp.shaders.ShaderProgram;

public class TerrainShader extends ShaderProgram{
		
	private static final String VERTEX_FILE = "/sjmhrp/terrain/TerrainVertexShader.glsl";
	private static final String FRAGMENT_FILE = "/sjmhrp/terrain/TerrainFragmentShader.glsl";
	
	private int location_transformMatrix;
	private int location_viewMatrix;
	private int location_background;
	private int location_rTexture;
	private int location_gTexture;
	private int location_bTexture;
	private int location_blendMap;
	private int location_useBlend;
	
	public TerrainShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bind() {
		bindAttribute(0,"position");
		bindAttribute(1,"texturePos");
		bindAttribute(2,"normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformMatrix = getUniformLocation("transformMatrix");
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
	
	public void loadTransformMatrix(Matrix4d matrix) {
		loadMatrix(location_transformMatrix, matrix);
	}
	
	public void loadViewMatrix(Matrix4d matrix) {
		loadMatrix(location_viewMatrix, matrix);
	}
}