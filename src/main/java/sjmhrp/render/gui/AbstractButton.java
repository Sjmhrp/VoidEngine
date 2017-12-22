package sjmhrp.render.gui;

import sjmhrp.event.KeyListener;
import sjmhrp.physics.PhysicsEngine;

public abstract class AbstractButton extends GUIBox implements KeyListener {

	{PhysicsEngine.addTickListener(this);}
	
	public AbstractButton(int texture) {
		super(texture);
		addAttribute("button");
	}

	@Override
	public void tick() {
		if(!active)return;
		if(isSelected()) {
			onHover();
		} else {
			stopHover();
		}
	}

	@Override
	public void keyPressed(int key) {
	}

	@Override
	public void keyReleased(int key) {
	}

	@Override
	public void mousePressed(int key) {
	}

	@Override
	public void mouseReleased(int key) {
		if(active&&isSelected())onClick(key);
	}
	
	public abstract boolean isSelected();
	
	public abstract void onClick(int key);
	
	public abstract void onHover();
	
	public abstract void stopHover();
	
	@Override
	public boolean keepOnLoad() {
		return true;
	}
}
