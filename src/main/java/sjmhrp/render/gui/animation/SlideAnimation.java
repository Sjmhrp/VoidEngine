package sjmhrp.render.gui.animation;

import sjmhrp.utils.VectorUtils;
import sjmhrp.utils.linear.Vector2d;

public class SlideAnimation extends GUIAnimation {

	Vector2d startPos;
	Vector2d endPos;
	
	public SlideAnimation(Vector2d startPos, Vector2d endPos, double length) {
		super(length);
		this.startPos = startPos;
		this.endPos = endPos;
	}

	@Override
	void step(double dt) {
		component.setOffset(VectorUtils.lerp(startPos,endPos,progress/length));
	}
}