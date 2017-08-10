package sjmhrp.gui;

import sjmhrp.shaders.ShaderProgram;

public class GUIShader extends ShaderProgram {

	private int location_offset;
	private int location_size;
	private int location_opacity;
	
	public GUIShader() {
		super("gui/GUI","gui/GUI");
	}

	@Override
	protected void getAllUniformLocations() {
		location_offset = getUniformLocation("offset");
		location_size = getUniformLocation("size");
		location_opacity = getUniformLocation("opacity");
	}

	@Override
	protected void bind() {
		bindAttribute(0,"position");
	}
	
	public void load(BasicGUIComponent g) {
		load2Vector(location_offset,g.getOffset());
		load2Vector(location_size,g.getSize());
		loadFloat(location_opacity,g.getOpacity());
	}
}