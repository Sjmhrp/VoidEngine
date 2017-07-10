package sjmhrp.post;

import sjmhrp.linear.Vector3d;
import sjmhrp.shaders.PostShaderProgram;

public class TintShader extends PostShaderProgram {

	private int location_tintColour;
	private int location_opacity;
	
	public TintShader() {
		super("Generic","Tint");
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
