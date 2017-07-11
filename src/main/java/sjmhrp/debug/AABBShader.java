package sjmhrp.debug;

import sjmhrp.linear.Matrix4d;
import sjmhrp.shaders.ShaderProgram;

public class AABBShader extends ShaderProgram{

	private int location_viewMatrix;

	public AABBShader() {
		super("debug/AABB","debug/AABB","debug/AABB");
	}

	@Override
	protected void bind() {
		bindAttribute(0,"position");
		bindAttribute(1,"radius");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
	}

	public void loadViewMatrix(Matrix4d matrix) {
		load4Matrix(location_viewMatrix, matrix);
	}
}