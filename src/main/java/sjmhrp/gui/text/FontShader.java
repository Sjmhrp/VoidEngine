package sjmhrp.gui.text;

import sjmhrp.shaders.ShaderProgram;

public class FontShader extends ShaderProgram {

	private int location_offset;
	private int location_size;
	private int location_colour;
	private int location_opacity;
	private int location_outlineWidth;
	private int location_outlineColour;
	private int location_outlineOffset;
	
	public FontShader() {
		super("gui/text/Font","gui/text/Font");
	}

	@Override
	protected void getAllUniformLocations() {
		location_offset = getUniformLocation("offset");
		location_size = getUniformLocation("size");
		location_colour = getUniformLocation("colour");
		location_opacity = getUniformLocation("opacity");
		location_outlineWidth = getUniformLocation("outlineWidth");
		location_outlineColour = getUniformLocation("outlineColour");
		location_outlineOffset = getUniformLocation("outlineOffset");
	}

	@Override
	protected void bind() {
		bindAttribute(0,"position");
		bindAttribute(1,"texturePos");
	}
	
	public void load(GUIText g) {
		load2Vector(location_offset,g.getOffset());
		loadFloat(location_size,g.getFontSize());
		load3Vector(location_colour,g.getColour());
		loadFloat(location_opacity,g.getOpacity());
		loadFloat(location_outlineWidth,g.getOutlineWidth());
		load3Vector(location_outlineColour,g.getOutlineColour());
		load2Vector(location_outlineOffset,g.getDropShadow());
	}
}