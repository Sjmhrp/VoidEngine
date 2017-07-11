package sjmhrp.sky;

import sjmhrp.linear.Matrix4d;
import sjmhrp.shaders.ShaderProgram;

public class CelestialShader extends ShaderProgram {

	private int location_transformMatrix;
	private int location_viewMatrix;
	
	public CelestialShader() {
		super("sky/Celestial","sky/Celestial");
	}

	@Override
	protected void bind() {
		bindAttribute(0,"position");
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_transformMatrix = getUniformLocation("transformMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_projectionMatrix = getUniformLocation("projectionMatrix");
	}

	public void loadTransformMatrix(Matrix4d matrix) {
		load4Matrix(location_transformMatrix,matrix);
	}
	
	public void loadViewMatrix(Matrix4d viewMatrix) {
		load4Matrix(location_viewMatrix,viewMatrix);
	}
}