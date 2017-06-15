package sjmhrp.sky;

import sjmhrp.linear.Matrix4d;
import sjmhrp.shaders.ShaderProgram;

public class SunShader extends ShaderProgram {
	
	private static final String VERTEX_FILE = "/sjmhrp/sky/SunVertexShader.glsl";
	private static final String FRAGMENT_FILE = "/sjmhrp/sky/SunFragmentShader.glsl";
	
	private int location_transformMatrix;
	private int location_viewMatrix;
	
	public SunShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_transformMatrix = getUniformLocation("transformMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
	}

	@Override
	protected void bind() {
		bindAttribute(0,"position");
	}
	
	public void loadTransformMatrix(Matrix4d matrix) {
		loadMatrix(location_transformMatrix, matrix);
	}
	
	public void loadViewMatrix(Matrix4d matrix) {
		loadMatrix(location_viewMatrix, matrix);
	}
}