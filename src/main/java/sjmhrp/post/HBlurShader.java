package sjmhrp.post;

import org.lwjgl.opengl.Display;

import sjmhrp.shaders.PostShaderProgram;

public class HBlurShader extends PostShaderProgram {

	private int location_width;

	public HBlurShader() {
		super("post/HBlur", "post/Blur");
	}
	
	@Override
	public void start() {
		super.start();
		loadFloat(location_width,Display.getWidth());
	}
	
	@Override
	protected void getAllUniformLocations() {
		location_width = getUniformLocation("width");
	}
}