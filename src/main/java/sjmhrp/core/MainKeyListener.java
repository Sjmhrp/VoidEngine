package sjmhrp.core;

import org.lwjgl.input.Keyboard;

import sjmhrp.event.KeyListener;
import sjmhrp.physics.PhysicsEngine;

public class MainKeyListener implements KeyListener{

	public MainKeyListener() {
		PhysicsEngine.addEventListener(this);
	}

	@Override
	public void tick() {
	}

	@Override
	public void keyPressed(int key) {
	}

	@Override
	public void keyReleased(int key) {
		if(key==Keyboard.KEY_ESCAPE)PhysicsEngine.pause();
		if(key==Keyboard.KEY_R)Main.restart();
		if(!PhysicsEngine.paused) {
			if(key==Keyboard.KEY_L)Globals.debug=!Globals.debug;
		}
	}

	@Override
	public void mousePressed(int key) {
	}

	@Override
	public void mouseReleased(int key) {
	}
	
	@Override
	public boolean canPause() {
		return false;
	}
}