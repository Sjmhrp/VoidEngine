package sjmhrp.post;

import org.lwjgl.opengl.Display;

import sjmhrp.shaders.PostShaderProgram;

public class VBlurShader extends PostShaderProgram {

	private int location_height;

	public VBlurShader() {
		super("post/VBlur", "post/Blur");
	}
	
	@Override
	public void start() {
		super.start();
		loadFloat(location_height,Display.getHeight());
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_height = getUniformLocation("height");
	}
}