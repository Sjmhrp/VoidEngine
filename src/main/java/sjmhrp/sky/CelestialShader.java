package sjmhrp.sky;

import sjmhrp.linear.Matrix4d;
import sjmhrp.shaders.ShaderProgram;

public class CelestialShader extends ShaderProgram {

	private static final String VERTEX_SHADER = "/sjmhrp/sky/CelestialVertexShader.glsl";
	private static final String FRAGMENT_SHADER = "/sjmhrp/sky/CelestialFragmentShader.glsl";
	
	private int location_transformMatrix;
	private int location_viewMatrix;
	
	public CelestialShader() {
		super(VERTEX_SHADER,FRAGMENT_SHADER);
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
		loadMatrix(location_transformMatrix,matrix);
	}
	
	public void loadViewMatrix(Matrix4d viewMatrix) {
		loadMatrix(location_viewMatrix,viewMatrix);
	}
}