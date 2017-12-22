package sjmhrp.world.sky;

import sjmhrp.io.ConfigHandler;
import sjmhrp.render.shader.MultiTextureShader;
import sjmhrp.render.shader.ShaderProgram;
import sjmhrp.utils.linear.Matrix4d;

public class SkyShader extends ShaderProgram implements MultiTextureShader {

	private int location_viewMatrix;
	private int location_domeSize;
	private int location_glow;
	private int location_colour;
	private int location_hasClouds;
	private int location_sunPosition;
	
	public SkyShader() {
		super("world/sky/Sky",false);
	}

	@Override
	protected void bind() {
		bindAttribute(0,"position");
		bindAttribute(1,"texturePos");
		bindAttribute(2,"normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_domeSize = getUniformLocation("domeSize");
		location_glow = getUniformLocation("glow");
		location_colour = getUniformLocation("colour");
		location_hasClouds = getUniformLocation("hasClouds");
		location_sunPosition = getUniformLocation("sunPosition");
	}

	public void loadViewMatrix(Matrix4d matrix) {
		load4Matrix(location_viewMatrix, matrix);
	}
	
	public void load(SkyDome sky) {
		loadFloat(location_domeSize,sky.getSize());
		loadBoolean(location_hasClouds,ConfigHandler.getBoolean("clouds"));
		load3Vector(location_sunPosition,sky.getSunPosition());
	}
	
	public void connectTextures() {
		loadInt(location_glow,0);
		loadInt(location_colour,1);
	}
}