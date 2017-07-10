package sjmhrp.sky;

import sjmhrp.linear.Matrix4d;
import sjmhrp.shaders.ShaderProgram;

public class StarShader extends ShaderProgram {
	
	private static final String VERTEX_SHADER = "/sjmhrp/sky/StarVertexShader.glsl";
	private static final String GEOMETRY_SHADER = "/sjmhrp/sky/StarGeometryShader.glsl";
	private static final String FRAGMENT_SHADER = "/sjmhrp/sky/StarFragmentShader.glsl";
	
	private int location_viewMatrix;
	private int location_domeSize;
	private int location_sunPosition;
	
	public StarShader() {
		super(VERTEX_SHADER,GEOMETRY_SHADER,FRAGMENT_SHADER);
	}
	
	@Override
	public void bind() {
		bindAttribute(0,"position");
		bindAttribute(1,"colour");
		bindAttribute(2,"radius");
	}
	
	@Override
	public void getAllUniformLocations() {
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_domeSize = getUniformLocation("domeSize");
		location_sunPosition = getUniformLocation("sunPosition");
	}
	
	public void loadViewMatrix(Matrix4d viewMatrix) {
		loadMatrix(location_viewMatrix,viewMatrix);
	}
	
	public void load(SkyDome sky) {
		loadFloat(location_domeSize,sky.getSize());
		load3Vector(location_sunPosition,sky.getSun().getPosition());
	}
}