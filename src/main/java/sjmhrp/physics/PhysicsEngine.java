package sjmhrp.physics;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import sjmhrp.event.EventListener;
import sjmhrp.event.KeyHandler;
import sjmhrp.event.KeyListener;
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
			if(e instanceof KeyListener&&!(paused&&e.canPause()))ls.add((KeyListener)e);
		}
		KeyHandler.tick(ls);
		for(EventListener e : listeners) {
			if(!paused||!e.canPause())e.tick();
		}
		if(!paused&&!Keyboard.isKeyDown(Keyboard.KEY_P)) {
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
	
	public static void pause() {
		paused=!paused;
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