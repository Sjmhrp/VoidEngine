package sjmhrp.debug;

import sjmhrp.linear.Matrix4d;
import sjmhrp.shaders.ShaderProgram;

public class AABBShader extends ShaderProgram{

	private static final String VERTEX_FILE = "/sjmhrp/debug/AABBVertexShader.glsl";
	private static final String GEOMETRY_FILE = "/sjmhrp/debug/AABBGeometryShader.glsl";
	private static final String FRAGMENT_FILE = "/sjmhrp/debug/AABBFragmentShader.glsl";

	private int location_viewMatrix;

	public AABBShader() {
		super(VERTEX_FILE,GEOMETRY_FILE,FRAGMENT_FILE);
	}

	@Override
	protected void bind() {
		bindAttribute(0, "position");
		bindAttribute(1, "radius");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
	}

	public void loadViewMatrix(Matrix4d matrix) {
		loadMatrix(location_viewMatrix, matrix);
	}
}