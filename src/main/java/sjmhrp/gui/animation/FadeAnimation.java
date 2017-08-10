package sjmhrp.gui.animation;

import sjmhrp.utils.ScalarUtils;

public class FadeAnimation  extends GUIAnimation {

	double startOpacity;
	double endOpacity;
	
	public FadeAnimation(double length) {
		this(0,1,length);
	}
	
	public FadeAnimation(double startOpacity, double endOpacity, double length) {
		super(length);
		this.startOpacity = startOpacity;
		this.endOpacity = endOpacity;
	}

	@Override
	void step() {
		component.setOpacity(ScalarUtils.lerp(startOpacity,endOpacity,progress/length));
	}
}