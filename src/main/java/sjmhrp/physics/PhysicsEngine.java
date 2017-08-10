package sjmhrp.physics;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import sjmhrp.event.EventListener;
import sjmhrp.event.KeyHandler;
import sjmhrp.event.KeyListener;
import sjmhrp.gui.GUIHandler;
import sjmhrp.world.World;

public class PhysicsEngine {
	
	public static boolean paused = false;
	public static int tick = 0;
	static double timeStep;
	
	static ArrayList<World> worlds = new ArrayList<World>();
	static ArrayList<EventListener> listeners = new ArrayList<EventListener>();
	
	public static void step(double dt) {
		timeStep=dt/1000000000;
		ArrayList<KeyListener> ls = new ArrayList<KeyListener>();
		for(EventListener e : listeners) {
			if(e instanceof KeyListener)ls.add((KeyListener)e);
		}
		KeyHandler.tick(ls);
		for(EventListener e : listeners) {
			e.tick();
		}
		GUIHandler.tick();
		if(!paused) {
			for(World world : worlds) {
				if(!Keyboard.isKeyDown(Keyboard.KEY_Q)) {
					world.stepForward();
				} else {
					world.stepBackward();
				}
			}
		}
		tick++;
	}
	
	public static double getTimeStep() {
		return timeStep;
	}

	public static double getFPS() {
		return Math.min(Math.round(10/timeStep)/10d,60);
	}
	
	public static ArrayList<World> getWorlds() {
		return worlds;
	}
	
	public static void pause() {
		Mouse.setGrabbed(paused);
		paused=!paused;
		if(paused)Mouse.setCursorPosition(Display.getWidth()/2,Display.getHeight()/2);
		GUIHandler.switchToScreen(paused?"pause":"main");
	}
	
	public static void registerWorld(World world) {
		worlds.add(world);
	}
	
	public static void addEventListener(EventListener e) {
		listeners.add(e);
	}
	
	public static void clear() {
		worlds.clear();
	}
}