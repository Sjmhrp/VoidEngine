package sjmhrp.render.flare;

import sjmhrp.render.shader.PostShaderProgram;
import sjmhrp.utils.linear.Vector3d;

public class DownSampleShader extends PostShaderProgram {
	
	private int location_bias;
	
	public DownSampleShader() {
		super("render/post/Generic","render/flare/DownSample");
	}

	@Override
	protected void getAllUniformLocations() {
		location_bias = getUniformLocation("bias");
	}
	
	public void loadBias(Vector3d bias) {
		load3Vector(location_bias,bias);
	}
}