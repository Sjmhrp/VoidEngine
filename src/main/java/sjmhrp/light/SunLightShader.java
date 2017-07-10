package sjmhrp.light;

import sjmhrp.linear.Matrix4d;
import sjmhrp.shaders.ShaderProgram;
import sjmhrp.sky.CelestialBody;

public class SunLightShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/sjmhrp/post/GenericVertexShader.glsl";
	private static final String FRAGMENT_FILE = "/sjmhrp/light/SunLightFragmentShader.glsl";
	
	private int location_viewMatrix;
	private int location_albedo;
	private int location_normal;
	private int location_depth;
	private int location_pos;
	private int location_colour;
	
	public SunLightShader() {
		super(VERTEX_FILE,FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_albedo = getUniformLocation("albedo");
		location_normal = getUniformLocation("normalMap");
		location_depth = getUniformLocation("depth");
		location_pos = getUniformLocation("position");
		location_colour = getUniformLocation("colour");
	}

	@Override
	protected void bind() {
		bindAttribute(0,"position");
	}

	public void loadViewMatrix(Matrix4d matrix) {
		loadMatrix(location_viewMatrix,matrix);
	}
	
	public void load(CelestialBody body) {
		load3Vector(location_pos,body.getPosition());
		load3Vector(location_colour,body.getColour());
	}
	
	public void connectTextures() {
		loadInt(location_albedo,0);
		loadInt(location_normal,1);
		loadInt(location_depth,2);
	}
}