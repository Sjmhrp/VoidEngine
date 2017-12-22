package sjmhrp.world.sky;

import sjmhrp.render.shader.ShaderProgram;
import sjmhrp.utils.linear.Matrix4d;

public class StarShader extends ShaderProgram {
	
	private int location_viewMatrix;
	private int location_domeSize;
	private int location_sunPosition;
	
	public StarShader() {
		super("world/sky/Star",true);
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
		load3Vector(location_sunPosition,sky.getSunPosition());
	}
}