package sjmhrp.core;

import org.lwjgl.LWJGLException;

import sjmhrp.io.ConfigHandler;
import sjmhrp.io.SaveHandler;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.render.RenderHandler;
import sjmhrp.render.debug.DebugRenderer;
import sjmhrp.render.gui.GUIHandler;
import sjmhrp.render.view.Camera;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.world.level.DemoLevel;

public class Main {

	public static final String TITLE = "Void Engine";
	public static final String VERSION = "1.0.5";
	public static final int[] SIZE = {720,480};	

	static Camera camera = new Camera(new Vector3d(0,33,0));
	
	public static void main(String[] args) throws LWJGLException {
		ConfigHandler.loadConfigFiles();
		start();
	}

	static void start() {
		RenderHandler.start(TITLE,SIZE[0],SIZE[1],false);
		RenderHandler.setCamera(camera);
		loop();
	}
	
	static void loop() {
		if(!SaveHandler.loadFile("DemoLevel"))new DemoLevel().build();
		new MainKeyListener(camera);
		GUIHandler.switchToScreen("main");
		long time = System.nanoTime();
		while(RenderHandler.isRendering()) {
			double dt = System.nanoTime()-time;
			time = System.nanoTime();
			PhysicsEngine.step(dt);
			if(ConfigHandler.getBoolean("debug"))DebugRenderer.raycast(camera);
			double r = 1/PhysicsEngine.TARGET_FPS-dt;
			if(r>0)try{Thread.sleep((long)r);}catch(Exception e){}
		}
	}
}