package sjmhrp.gui;

import java.util.ArrayList;

import sjmhrp.core.Main;
import sjmhrp.gui.animation.GUIAnimation;
import sjmhrp.gui.animation.SlideAnimation;
import sjmhrp.gui.text.GUIText;
import sjmhrp.gui.text.TextButton;
import sjmhrp.io.ConfigHandler;
import sjmhrp.io.SaveHandler;
import sjmhrp.linear.Vector2d;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.render.RenderRegistry;

public class GUIHandler {

	static ArrayList<GUIAnimation> animations = new ArrayList<GUIAnimation>();
	static String nextScreen;
	
	public static void addAnimation(GUIAnimation g) {
		animations.add(g);
	}
	
	public static void removeAnimation(GUIAnimation g) {
		animations.remove(g);
	}
	
	public static void tick() {
		for(GUIComponent g : RenderRegistry.getAllGUI()) {
			g.setActive(g.hasAttribute(nextScreen)||g.hasAttribute("all"));
		}
		for(GUIAnimation g : animations) {
			g.tick(PhysicsEngine.getTimeStep());
		}
	}
	
	public static void switchToScreen(String screen) {
		nextScreen=screen;
	}
	
	public static void createPauseMenu() {
		new GUIText("VoidEngine").setDropShadow(new Vector2d(0.003,0.003)).setFontSize(4).setColour(1,1,1).addAttribute("pause").addAttribute("options")
		.addAnimation(new SlideAnimation(new Vector2d(1.1,0.7),new Vector2d(0,0.7),0.3));
		new TextButton("Resume",(key->{if(key==0)PhysicsEngine.pause();})).setFontSize(2).addAttribute("pause")
		.addAnimation(new SlideAnimation(new Vector2d(1.1,0.2),new Vector2d(0,0.2),0.3));
		new TextButton("Save",(key->{if(key==0)SaveHandler.saveFile();})).setFontSize(2).addAttribute("pause")
		.addAnimation(new SlideAnimation(new Vector2d(1.1,0.05),new Vector2d(0,0.05),0.3));
		new TextButton("Load",(key->{if(key==0)Main.load();})).setFontSize(2).addAttribute("pause")
		.addAnimation(new SlideAnimation(new Vector2d(1.1,-0.1),new Vector2d(0,-0.1),0.3));
		new TextButton("Options",(key->{if(key==0)switchToScreen("options");})).setFontSize(2).addAttribute("pause")
		.addAnimation(new SlideAnimation(new Vector2d(1.1,-0.25),new Vector2d(0,-0.25),0.3));
		new TextButton("Quit",(key->{if(key==0)Main.exit();})).setFontSize(2).addAttribute("pause")
		.addAnimation(new SlideAnimation(new Vector2d(1.1,-0.4),new Vector2d(0,-0.4),0.3));
		addBooleanOption("Clouds","clouds",new Vector2d(0,0.25));
		addBooleanOption("FXAA","fxaa",new Vector2d(0,0.1));
		addBooleanOption("Debug Mode","debug",new Vector2d(0,-0.05));
		addBooleanOption("VSync*","vsync",new Vector2d(0,-0.2));
		new TextButton("Back",(key->{if(key==0)switchToScreen("pause");})).setFontSize(2).addAttribute("options")
		.addAnimation(new SlideAnimation(new Vector2d(1.1,-0.35),new Vector2d(0,-0.35),0.3));
		new GUIText("* - Requires restart").setFontSize(2).setColour(1,1,1).addAttribute("options")
		.addAnimation(new SlideAnimation(new Vector2d(1.1,-0.65),new Vector2d(0,-0.65),0.3));
	}
	
	static void addBooleanOption(String name, String k, Vector2d offset) {
		TextButton t = new TextButton(name+": "+(ConfigHandler.getBoolean(k)?"On":"Off"));
		t.setOnClick((key->{if(key==0){
			ConfigHandler.setProperty(k,!ConfigHandler.getBoolean(k));
			ConfigHandler.updateEngineConfig();
			t.set(name+": "+(ConfigHandler.getBoolean(k)?"On":"Off"));
		}}));
		t.setFontSize(2).addAttribute("options").setOffset(offset.x,offset.y)
		.addAnimation(new SlideAnimation(new Vector2d(1.1,0).add(offset),offset,0.3));
	}	
}