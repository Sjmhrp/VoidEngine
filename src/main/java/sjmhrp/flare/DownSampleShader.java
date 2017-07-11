package sjmhrp.flare;

import sjmhrp.linear.Vector3d;
import sjmhrp.shaders.PostShaderProgram;

public class DownSampleShader extends PostShaderProgram {
	
	private int location_bias;
	
	public DownSampleShader() {
		super("post/Generic","flare/DownSample");
	}

	@Override
	protected void getAllUniformLocations() {
		location_bias = getUniformLocation("bias");
	}
	
	public void loadBias(Vector3d bias) {
		load3Vector(location_bias,bias);
	}
}