package sjmhrp.particle;

import sjmhrp.render.shader.ShaderProgram;

public class ParticleShader extends ShaderProgram {

	private int location_rows;
	
	public ParticleShader() {
		super("particle/Particle",false);
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_rows = getUniformLocation("rows");
	}

	@Override
	protected void bind() {
		bindAttribute(0,"position");
		bindAttribute(1,"modelViewMatrix");
		bindAttribute(5,"textureOffsets");
		bindAttribute(6,"blend");
		bindAttribute(7,"age");
	}
	
	public void loadRows(int rows) {
		loadFloat(location_rows,rows);
	}
}