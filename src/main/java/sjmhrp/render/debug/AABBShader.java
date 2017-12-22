package sjmhrp.render.debug;

import sjmhrp.render.shader.ShaderProgram;
import sjmhrp.utils.linear.Matrix4d;

public class AABBShader extends ShaderProgram{

	private int location_viewMatrix;

	public AABBShader() {
		super("render/debug/AABB",true);
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