package sjmhrp.core;

import org.lwjgl.opengl.Display;

import sjmhrp.flare.FlareRenderer;
import sjmhrp.gui.GUIHandler;
import sjmhrp.gui.text.GUIText;
import sjmhrp.io.ConfigHandler;
import sjmhrp.io.SaveHandler;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.post.Post;
import sjmhrp.render.Loader;
import sjmhrp.render.RenderHandler;
import sjmhrp.render.RenderRegistry;
import sjmhrp.render.SSAORenderer;
import sjmhrp.shaders.Shader;
import sjmhrp.textures.TerrainTexture;
import sjmhrp.view.Camera;
import sjmhrp.world.World;

public class Main {

	public static final String TITLE = "Void Engine";
	public static final String VERSION = "1.0.3";
	public static final int[] SIZE = {720,480};	

	static Camera camera = new Camera(new Vector3d(375,33,461));
	static World world;
	static Shader shader;
	static GUIText fps;

	public static void main(String[] args) {
		ConfigHandler.loadConfigFiles();
		RenderHandler.init(TITLE+" "+VERSION,SIZE[0],SIZE[1],false);
		new MainKeyListener();
		shader = new Shader();
		if(!load("DemoLevel"))createWorld();
		GUIHandler.createPauseMenu();
		fps = new GUIText("");
		loop();
	}

	public static void restart() {
		PhysicsEngine.clear();
		RenderRegistry.clearEntities();
		if(!load("DemoLevel"))createWorld();
		loop();
	}
	
	static void createWorld() {
		world = new World();
		world.generateSky();
		world.addSun();
		world.generateStars();
		world.generateFlatTerrain(0,new TerrainTexture("background"));
		world.setGravity(new Vector3d(0,-ConfigHandler.getDouble("gravity"),0));
	}
	
	public static boolean load() {
		boolean b = SaveHandler.loadFile();
		if(PhysicsEngine.getWorlds().size()>0)world = PhysicsEngine.getWorlds().get(0);
		return b;
	}
	
	public static boolean load(String file) {
		boolean b = SaveHandler.loadFile(file);
		if(PhysicsEngine.getWorlds().size()>0)world = PhysicsEngine.getWorlds().get(0);
		return b;
	}
	
	static void loop() {
		long time = System.nanoTime();
		GUIHandler.switchToScreen("main");
		while(!Display.isCloseRequested()) {
			double dt = System.nanoTime()-time;
			time = System.nanoTime();
			PhysicsEngine.step(dt);
			fps.remove();
			fps = new GUIText(String.valueOf(PhysicsEngine.getFPS()));
			fps.setFontSize(3).addAttribute("all").setOffset(0.88,0.9);
			RenderHandler.renderWorld(world,camera,shader);
		}
		exit();
	}

	public static void exit() {
		Loader.cleanUp();
		shader.cleanUp();
		RenderHandler.cleanUp();
		Post.cleanUp();
		SSAORenderer.cleanUp();
		FlareRenderer.cleanUp();
		Display.destroy();
		System.exit(0);
	}
}