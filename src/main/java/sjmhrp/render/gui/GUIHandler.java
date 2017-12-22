package sjmhrp.render.gui;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import sjmhrp.io.ConfigHandler;
import sjmhrp.io.SaveHandler;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.render.RenderHandler;
import sjmhrp.render.RenderRegistry;
import sjmhrp.render.gui.animation.GUIAnimation;
import sjmhrp.render.gui.animation.SlideAnimation;
import sjmhrp.render.gui.animation.SpinAnimation;
import sjmhrp.render.gui.text.GUIText;
import sjmhrp.render.gui.text.TextButton;
import sjmhrp.render.textures.TexturePool;
import sjmhrp.utils.linear.Vector2d;

public class GUIHandler {

	static final int OPTION_SCREENS = 2;
	
	static ArrayList<GUIAnimation> animations = new ArrayList<GUIAnimation>();
	static boolean paused = false;
	static String prevScreen = "";
	static String nextScreen = "";
	static GUIText fps;
	
	public static void addAnimation(GUIAnimation g) {
		animations.add(g);
	}
	
	public static void removeAnimation(GUIAnimation g) {
		animations.remove(g);
	}
	
	public static void tick(double dt) {
		for(GUIComponent g : RenderRegistry.getAllGUI()) {
			g.setActive(g.hasAttribute(nextScreen)||g.hasAttribute("all"));
		}
		for(GUIAnimation g : animations) {
			g.tick(dt);
		}
		fps.setActive(ConfigHandler.getBoolean("debug"));
		if(ConfigHandler.getBoolean("debug"))fps.set(String.valueOf(PhysicsEngine.getFPS()));
	}
	
	public static void pause() {
		switchToScreen(paused?"unpause":"pause");
	}
	
	public static boolean isPaused() {
		return paused;
	}
	
	static void nextOptions() {
		if(!nextScreen.startsWith("options"))return;
		switchToScreen("options"+(int)(Integer.valueOf(nextScreen.substring(7))+1)%OPTION_SCREENS);
	}
	
	static void prevOptions() {
		if(!nextScreen.startsWith("options"))return;
		switchToScreen("options"+(int)(Integer.valueOf(nextScreen.substring(7))+OPTION_SCREENS-1)%OPTION_SCREENS);
	}
	
	public static void switchToScreen(String screen) {
		if(!RenderHandler.isRenderer()) {
			RenderHandler.addTask(()->switchToScreen(screen));
			return;
		}
		if(screen.equals(nextScreen))return;
		switch(screen) {
			case "loading":
				PhysicsEngine.pause(true);
				paused=true;
				Mouse.setGrabbed(false);
				break;
			case "unpause":
				switchToScreen(prevScreen);
				return;
			case "main":
				Mouse.setGrabbed(true);
				PhysicsEngine.pause(false);
				paused=false;
				break;
			case "pause":
				if(nextScreen.equals("main")||nextScreen.equals("edit"))prevScreen=nextScreen;
				if(Mouse.isGrabbed()) {
					Mouse.setGrabbed(false);
					Mouse.setCursorPosition(Display.getWidth()/2,Display.getHeight()/2);
				}
				PhysicsEngine.pause(true);
				paused=true;
				break;
			case "edit":
				Mouse.setGrabbed(true);
				PhysicsEngine.pause(false);
				paused=false;
				break;
		}
		nextScreen=screen;
	}
	
	public static String getPrevScreen() {
		return prevScreen;
	}
	
	public static String getScreen() {
		return nextScreen;
	}
	
	public static void createGUI() {
		fps = new GUIText("").setFontSize(3);
		fps.addAttribute("all").setOffset(0.88,0.9);
		
		new GUIText("VoidEngine").setDropShadow(new Vector2d(0.003,0.003)).setFontSize(4).setColour(1,1,1).addAttribute("pause").addAttribute("options")
		.addAnimation(new SlideAnimation(new Vector2d(1.1,0.7),new Vector2d(0,0.7),0.3));
		new TextButton("Resume",(key->{if(key==0)pause();})).setFontSize(2).addAttribute("pause")
		.addAnimation(new SlideAnimation(new Vector2d(1.1,0.2),new Vector2d(0,0.2),0.3));
		new TextButton("Save",(key->{if(key==0)SaveHandler.saveFile();})).setFontSize(2).addAttribute("pause")
		.addAnimation(new SlideAnimation(new Vector2d(1.1,0.05),new Vector2d(0,0.05),0.3));
		new TextButton("Load",(key->{if(key==0)SaveHandler.loadFile();})).setFontSize(2).addAttribute("pause")
		.addAnimation(new SlideAnimation(new Vector2d(1.1,-0.1),new Vector2d(0,-0.1),0.3));
		new TextButton("Options",(key->{if(key==0)switchToScreen("options0");})).setFontSize(2).addAttribute("pause")
		.addAnimation(new SlideAnimation(new Vector2d(1.1,-0.25),new Vector2d(0,-0.25),0.3));
		new TextButton("Quit",(key->{if(key==0)RenderHandler.exit();})).setFontSize(2).addAttribute("pause")
		.addAnimation(new SlideAnimation(new Vector2d(1.1,-0.4),new Vector2d(0,-0.4),0.3));
		GUIComponent n = new TextButton("Next",(key->{if(key==0)nextOptions();})).setFontSize(2)
		.addAnimation(new SlideAnimation(new Vector2d(1.1,-0.4),new Vector2d(0.2,-0.3),0.3));
		GUIComponent p = new TextButton("Prev",(key->{if(key==0)prevOptions();})).setFontSize(2)
		.addAnimation(new SlideAnimation(new Vector2d(1.1,-0.4),new Vector2d(-0.2,-0.3),0.3));
		GUIComponent b = new TextButton("Back",(key->{if(key==0)switchToScreen("pause");})).setFontSize(2)
		.addAnimation(new SlideAnimation(new Vector2d(1.1,-0.5),new Vector2d(0,-0.5),0.3));
		for(int i = 0; i < OPTION_SCREENS; i++) {
			n.addAttribute("options"+i);
			p.addAttribute("options"+i);
			b.addAttribute("options"+i);
		}
		addBooleanOption("Clouds","clouds",new Vector2d(0,0.35),0);
		addBooleanOption("Bloom","bloom",new Vector2d(0,0.2),0);
		addBooleanOption("Lens Flare","lensflare",new Vector2d(0,0.05),0);
		addBooleanOption("Wireframe","wireframe",new Vector2d(0,-0.1),0);
		addBooleanOption("SSAO","ssao",new Vector2d(0,0.35),1);
		addBooleanOption("FXAA","fxaa",new Vector2d(0,0.2),1);
		addBooleanOption("Debug Mode","debug",new Vector2d(0,0.05),1);
		addBooleanOption("VSync*","vsync",new Vector2d(0,-0.1),1);
		new GUIText("* - Requires restart").setFontSize(2).setColour(1,1,1).addAttribute("options1")
		.addAnimation(new SlideAnimation(new Vector2d(1.1,-0.8),new Vector2d(0,-0.8),0.3));	
		new GUIBox(TexturePool.getTexture("loading").getAlbedoID()).setSize(0.2/Display.getWidth()*Display.getHeight(),0.2).addAttribute("loading").addAnimation(new SpinAnimation(2));
	}
	
	static void addBooleanOption(String name, String k, Vector2d offset, int page) {
		TextButton t = new TextButton(name+": "+(ConfigHandler.getBoolean(k)?"On":"Off"));
		t.setOnClick((key->{if(key==0){
			ConfigHandler.setProperty(k,!ConfigHandler.getBoolean(k),true);
			t.set(name+": "+(ConfigHandler.getBoolean(k)?"On":"Off"));
		}}));
		t.setFontSize(2).addAttribute("options"+page).setOffset(offset.x,offset.y)
		.addAnimation(new SlideAnimation(new Vector2d(1.1,0).add(offset),offset,0.3));
	}	
}