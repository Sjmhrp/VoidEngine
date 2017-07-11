package sjmhrp.sky;

import sjmhrp.linear.Matrix4d;
import sjmhrp.shaders.ShaderProgram;

public class StarShader extends ShaderProgram {
	
	private int location_viewMatrix;
	private int location_domeSize;
	private int location_sunPosition;
	
	public StarShader() {
		super("sky/Star","sky/Star","sky/Star");
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
		load4Matrix(location_viewMatrix,viewMatrix);
	}
	
	public void load(SkyDome sky) {
		loadFloat(location_domeSize,sky.getSize());
		load3Vector(location_sunPosition,sky.getSun().getPosition());
	}
}