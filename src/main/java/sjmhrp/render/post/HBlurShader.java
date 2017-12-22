package sjmhrp.render.post;

import org.lwjgl.opengl.Display;

import sjmhrp.render.shader.PostShaderProgram;

public class HBlurShader extends PostShaderProgram {

	private int location_width;

	public HBlurShader() {
		super("render/post/HBlur", "render/post/Blur");
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