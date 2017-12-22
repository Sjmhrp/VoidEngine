package sjmhrp.render.light;

import sjmhrp.render.shader.MultiTextureShader;
import sjmhrp.render.shader.ShaderProgram;
import sjmhrp.utils.linear.Matrix4d;

public class LightShader extends ShaderProgram implements MultiTextureShader {

	private int location_transformMatrix;
	private int location_viewMatrix;
	private int location_albedo;
	private int location_normal;
	private int location_mask;
	private int location_depth;
	private int location_lightPos;
	private int location_lightColour;
	private int location_attenuation;
	private int location_lightSize;
	
	public LightShader() {
		super("render/light/Light",false);
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformMatrix = getUniformLocation("transformMatrix");
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_albedo = getUniformLocation("albedo");
		location_normal = getUniformLocation("normalMap");
		location_mask = getUniformLocation("mask");
		location_depth = getUniformLocation("depth");
		location_lightColour = getUniformLocation("lightColour");
		location_lightPos = getUniformLocation("lightPos");
		location_attenuation = getUniformLocation("attenuation");
		location_lightSize = getUniformLocation("lightSize");
	}

	@Override
	protected void bind() {
		bindAttribute(0,"position");
	}

	public void loadTransformMatrix(Matrix4d matrix) {
		load4Matrix(location_transformMatrix, matrix);
	}

	public void loadViewMatrix(Matrix4d matrix) {
		load4Matrix(location_viewMatrix, matrix);
	}

	public void loadLight(Light l) {
		load3Vector(location_lightPos,l.getPos());
		load3Vector(location_lightColour,l.getColour());
		load3Vector(location_attenuation,l.getAttenuation());
		loadFloat(location_lightSize,l.getSize());
	}

	public void connectTextures() {
		loadInt(location_albedo,0);
		loadInt(location_normal,1);
		loadInt(location_mask,2);
		loadInt(location_depth,3);
	}
}