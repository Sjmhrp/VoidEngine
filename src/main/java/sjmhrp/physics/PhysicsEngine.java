package sjmhrp.physics;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import sjmhrp.event.KeyHandler;
import sjmhrp.event.KeyListener;
import sjmhrp.event.TickListener;
import sjmhrp.io.ConfigHandler;
import sjmhrp.render.RenderRegistry;
import sjmhrp.render.debug.DebugRenderer;
import sjmhrp.render.entity.Entity;
import sjmhrp.utils.ScalarUtils;
import sjmhrp.world.World;

public class PhysicsEngine {
	
	public static final double TARGET_FPS = 60;
	static final int MAX_SUB_TICKS = 1;
	
	static boolean paused = false;
	public static int tick = 0;
	static double timeStep;
	static double subTimeStep;
	
	static ArrayList<World> worlds = new ArrayList<World>();
	static ArrayList<TickListener> listeners = new ArrayList<TickListener>();
	static ArrayList<Runnable> tasks = new ArrayList<Runnable>();
	
	public static void step(double dt) {
		ArrayList<Runnable> ts = new ArrayList<Runnable>(tasks);
		tasks.clear();
		ts.stream().filter(t->t!=null).forEach(Runnable::run);
		timeStep = dt/1000000000;
		int ticks = (int)(timeStep*TARGET_FPS);
		ticks=ScalarUtils.clamp(ticks,1,MAX_SUB_TICKS);
		subTimeStep=timeStep/ticks;
		for(int i = 0; i < ticks; i++) {
			if(ConfigHandler.getBoolean("debug"))DebugRenderer.clearContacts();
			ArrayList<KeyListener> ls = new ArrayList<KeyListener>();
			for(TickListener e : new ArrayList<TickListener>(listeners)) {
				if(e instanceof KeyListener)ls.add((KeyListener)e);
			}
			KeyHandler.tick(ls);
			for(TickListener l : new ArrayList<TickListener>(listeners)) {
				l.tick();
			}
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
			RenderRegistry.getAllEntities().forEach(Entity::updateTransformMatrix);
		}
	}
	
	public static void addTask(Runnable r) {
		tasks.add(r);
	}
	
	public static void pause(boolean b) {
		tasks.add(()->paused=b);
	}
	
	public static boolean isPaused() {
		return paused;
	}
	
	public static double getTimeStep() {
		return subTimeStep;
	}

	public static double getFPS() {
		return Math.min(Math.round(10/timeStep)/10d,60);
	}
	
	public static ArrayList<World> getWorlds() {
		return worlds;
	}
	
	public static void registerWorld(World world) {
		worlds.add(world);
	}
	
	public static void addTickListener(TickListener e) {
		listeners.add(e);
	}
	
	public static void removeTickListener(TickListener e) {
		listeners.remove(e);
	}
	
	public static ArrayList<TickListener> getListeners() {
		return listeners;
	}
	
	public static void clear() {
		worlds.clear();
		listeners=listeners.stream().filter(TickListener::keepOnLoad).collect(Collectors.toCollection(ArrayList::new));
	}
}