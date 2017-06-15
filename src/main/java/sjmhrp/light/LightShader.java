package sjmhrp.light;

import sjmhrp.linear.Matrix4d;
import sjmhrp.shaders.ShaderProgram;

public class LightShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/sjmhrp/light/LightVertexShader.glsl";
	private static final String FRAGMENT_FILE = "/sjmhrp/light/LightFragmentShader.glsl";

	private int location_transformMatrix;
	private int location_viewMatrix;
	private int location_albedo;
	private int location_normal;
	private int location_depth;
	private int location_lightPos;
	private int location_lightColour;
	private int location_attenuation;
	private int location_lightSize;
	
	public LightShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformMatrix = getUniformLocation("transformMatrix");
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_albedo = getUniformLocation("albedo");
		location_normal = getUniformLocation("normalMap");
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
		loadMatrix(location_transformMatrix, matrix);
	}

	public void loadViewMatrix(Matrix4d matrix) {
		loadMatrix(location_viewMatrix, matrix);
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
		loadInt(location_depth,2);
	}
}