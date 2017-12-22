package sjmhrp.physics.dynamics.controller;

import sjmhrp.event.KeyListener;
import sjmhrp.utils.linear.Vector3d;

public abstract class Controller implements KeyListener {

	public abstract void setOrientation(Vector3d orientation);
	
	public abstract void setPosition(Vector3d position);
	
	public abstract Vector3d getCameraPosition();
	
	public boolean hasCamera() {
		return true;
	}
}