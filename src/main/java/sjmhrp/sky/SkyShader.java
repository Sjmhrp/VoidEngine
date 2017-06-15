package sjmhrp.sky;

import sjmhrp.linear.Matrix4d;
import sjmhrp.shaders.ShaderProgram;

public class SkyShader extends ShaderProgram {

	private static final String VERTEX_SHADER = "/sjmhrp/sky/SkyVertexShader.glsl";
	private static final String FRAGMENT_SHADER = "/sjmhrp/sky/SkyFragmentShader.glsl";

	private int location_viewMatrix;
	private int location_size;

	public SkyShader() {
		super(VERTEX_SHADER,FRAGMENT_SHADER);
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
		location_size = getUniformLocation("size");
	}

	public void loadSize(double size) {
		loadFloat(location_size,size);
	}
	
	public void loadViewMatrix(Matrix4d matrix) {
		loadMatrix(location_viewMatrix, matrix);
	}
}