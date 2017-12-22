package sjmhrp.world.sky;

import sjmhrp.render.shader.ShaderProgram;
import sjmhrp.utils.linear.Matrix4d;

public class CelestialShader extends ShaderProgram {

	private int location_transformMatrix;
	private int location_viewMatrix;
	
	public CelestialShader() {
		super("world/sky/Celestial",false);
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