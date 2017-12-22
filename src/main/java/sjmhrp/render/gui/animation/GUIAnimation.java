package sjmhrp.render.gui.animation;

import sjmhrp.render.gui.GUIComponent;
import sjmhrp.render.gui.GUIHandler;

public abstract class GUIAnimation {

	double progress;
	double length;
	boolean started;
	GUIComponent component;
	
	public GUIAnimation() {
		this(Double.MAX_VALUE);
	}
	
	public GUIAnimation(double length) {
		this.length = length;
		progress=0;
		started = false;
	}

	public void remove() {
		GUIHandler.removeAnimation(this);
	}
	
	public void setComponent(GUIComponent component) {
		this.component = component;
	}
	
	public void start() {
		started=true;
	}
	
	public void pause() {
		started=false;
	}
	
	public void stop() {
		started=false;
		progress = 0;
	}
	
	public void tick(double dt) {
	if(!started)return;
		progress+=dt;
		if(progress<length)step(dt);
		if(progress>=length) {
			progress = length;
			step(dt);
			stop();
		}
	}
	
	abstract void step(double dt);
}