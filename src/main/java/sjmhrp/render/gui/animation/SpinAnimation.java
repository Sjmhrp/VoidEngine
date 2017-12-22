package sjmhrp.render.gui.animation;

public class SpinAnimation extends GUIAnimation {

	double speed;
	
	public SpinAnimation(double speed) {
		this.speed=speed;
	}

	@Override
	void step(double dt) {
		component.addAngle(speed*dt);
	}
}