package sjmhrp.render.post;

import sjmhrp.render.shader.PostShaderProgram;
import sjmhrp.utils.linear.Vector3d;

public class TintShader extends PostShaderProgram {

	private int location_tintColour;
	private int location_opacity;
	
	public TintShader() {
		super("render/post/Generic","render/post/Tint");
	}

	@Override
	public void getAllUniformLocations() {
		location_tintColour = getUniformLocation("tintColour");
		location_opacity = getUniformLocation("opacity");
	}
	
	public void loadTint(Vector3d colour, double opacity) {
		load3Vector(location_tintColour,colour);
		loadFloat(location_opacity,opacity);
	}
}
