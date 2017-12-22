package sjmhrp.render.gui;

import sjmhrp.render.shader.ShaderProgram;

public class GUIShader extends ShaderProgram {

	private int location_offset;
	private int location_size;
	private int location_rot;
	private int location_opacity;
	
	public GUIShader() {
		super("render/gui/GUI",false);
	}

	@Override
	protected void getAllUniformLocations() {
		location_offset = getUniformLocation("offset");
		location_size = getUniformLocation("size");
		location_rot = getUniformLocation("rot");
		location_opacity = getUniformLocation("opacity");
	}

	@Override
	protected void bind() {
		bindAttribute(0,"position");
	}
	
	public void load(GUIBox g) {
		load2Vector(location_offset,g.getOffset());
		load2Vector(location_size,g.getSize());
		load2Matrix(location_rot,g.getRotation());
		loadFloat(location_opacity,g.getOpacity());
	}
}