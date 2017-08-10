package sjmhrp.gui.animation;

import sjmhrp.linear.Vector2d;
import sjmhrp.utils.VectorUtils;

public class SlideAnimation extends GUIAnimation {

	Vector2d startPos;
	Vector2d endPos;
	
	public SlideAnimation(Vector2d startPos, Vector2d endPos, double length) {
		super(length);
		this.startPos = startPos;
		this.endPos = endPos;
	}

	@Override
	void step() {
		component.setOffset(VectorUtils.lerp(startPos,endPos,progress/length));
	}
}