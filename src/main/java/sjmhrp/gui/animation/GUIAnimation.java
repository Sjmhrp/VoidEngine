package sjmhrp.gui.animation;

import sjmhrp.gui.GUIComponent;
import sjmhrp.gui.GUIHandler;

public abstract class GUIAnimation {

	double progress;
	double length;
	boolean started;
	GUIComponent component;
	
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
		if(progress<length)step();
		if(progress>=length) {
			progress = length;
			step();
			stop();
		}
	}
	
	abstract void step();
}