package sjmhrp.core;

import org.lwjgl.input.Keyboard;

import sjmhrp.event.KeyListener;
import sjmhrp.physics.PhysicsEngine;

public class MainKeyListener implements KeyListener {

	{PhysicsEngine.addEventListener(this);}

	@Override
	public void tick() {
	}

	@Override
	public void keyPressed(int key) {
	}

	@Override
	public void keyReleased(int key) {
		if(key==Keyboard.KEY_ESCAPE)PhysicsEngine.pause();
	}

	@Override
	public void mousePressed(int key) {
	}

	@Override
	public void mouseReleased(int key) {
	}
}