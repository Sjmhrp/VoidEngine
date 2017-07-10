package sjmhrp.sky;

import sjmhrp.linear.Matrix4d;
import sjmhrp.shaders.ShaderProgram;

public class SkyShader extends ShaderProgram {

	private static final String VERTEX_SHADER = "/sjmhrp/sky/SkyVertexShader.glsl";
	private static final String FRAGMENT_SHADER = "/sjmhrp/sky/SkyFragmentShader.glsl";

	private int location_viewMatrix;
	private int location_domeSize;
	private int location_glow;
	private int location_colour;
	private int location_sunPosition;
	
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
		location_domeSize = getUniformLocation("domeSize");
		location_glow = getUniformLocation("glow");
		location_colour = getUniformLocation("colour");
		location_sunPosition = getUniformLocation("sunPosition");
	}

	public void loadViewMatrix(Matrix4d matrix) {
		loadMatrix(location_viewMatrix, matrix);
	}
	
	public void load(SkyDome sky) {
		loadFloat(location_domeSize,sky.getSize());
		load3Vector(location_sunPosition,sky.getSun().getPosition());
	}
	
	public void connectTextures() {
		loadInt(location_glow,0);
		loadInt(location_colour,1);
	}
}