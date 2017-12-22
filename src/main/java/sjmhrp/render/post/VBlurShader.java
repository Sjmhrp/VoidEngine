package sjmhrp.render.post;

import org.lwjgl.opengl.Display;

import sjmhrp.render.shader.PostShaderProgram;

public class VBlurShader extends PostShaderProgram {

	private int location_height;

	public VBlurShader() {
		super("render/post/VBlur", "render/post/Blur");
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