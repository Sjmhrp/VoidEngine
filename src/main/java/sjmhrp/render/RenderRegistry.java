package sjmhrp.render;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import sjmhrp.entity.Entity;
import sjmhrp.gui.BasicGUIComponent;
import sjmhrp.gui.GUIComponent;
import sjmhrp.gui.text.FontType;
import sjmhrp.gui.text.GUIText;
import sjmhrp.light.Light;
import sjmhrp.models.RawModel;
import sjmhrp.textures.ModelTexture;

public class RenderRegistry {

	static HashMap<RawModel,HashMap<ModelTexture,ArrayList<Entity>>> entities = new HashMap<RawModel,HashMap<ModelTexture,ArrayList<Entity>>>();
	static ArrayList<Light> lights = new ArrayList<Light>();
	static ArrayList<BasicGUIComponent> gui = new ArrayList<BasicGUIComponent>();
	static HashMap<FontType,ArrayList<GUIText>> text = new HashMap<FontType,ArrayList<GUIText>>();
	
	public static void clearEntities() {
		entities = new HashMap<RawModel,HashMap<ModelTexture,ArrayList<Entity>>>();
		lights = new ArrayList<Light>();
	}
	
	public static void clear() {
		clearEntities();
		gui = new ArrayList<BasicGUIComponent>();
		text = new HashMap<FontType,ArrayList<GUIText>>();
	}
	
	public static void registerEntity(Entity e) {
		RawModel m = e.getModel().getRawModel();
		ModelTexture t = e.getModel().getTexture();
		HashMap<ModelTexture,ArrayList<Entity>> ess = entities.get(m);
		if(ess==null) {
			ess = new HashMap<ModelTexture,ArrayList<Entity>>();
			entities.put(m,ess);
		}
		ArrayList<Entity> es = ess.get(t);
		if(es==null) {
			es = new ArrayList<Entity>();
			ess.put(t,es);
		}
		es.add(e);
	}

	public static void registerLight(Light l) {
		lights.add(l);
	}

	public static void registerGUIComponent(BasicGUIComponent g) {
		gui.add(g);
	}
	
	public static void registerText(GUIText g) {
		ArrayList<GUIText> t = text.get(g.getFont());
		if(t==null) {
			t = new ArrayList<GUIText>();
			text.put(g.getFont(),t);
		}
		t.add((GUIText)g);
	}
	
	public static void removeEntity(Entity e) {
		HashMap<ModelTexture,ArrayList<Entity>> e1 = entities.get(e.getModel().getTexture());
		if(e1==null)return;
		ArrayList<Entity> e2 = e1.get(e.getModel().getRawModel());
		if(e2!=null)e2.remove(e);
	}
	
	public static void removeLight(Light l) {
		lights.remove(l);
	}
	
	public static void removeGUIComponent(BasicGUIComponent g) {
		gui.remove(g);
	}
	
	public static void removeGUIText(GUIText t) {
		ArrayList<GUIText> g = text.get(t.getFont());
		if(g!=null)g.remove(t);
	}
	
	public static HashMap<RawModel, HashMap<ModelTexture, ArrayList<Entity>>> getEntities() {
		return entities;
	}

	public static ArrayList<Entity> getAllEntities() {
		ArrayList<Entity> es = new ArrayList<Entity>();
		for(Entry<RawModel,HashMap<ModelTexture,ArrayList<Entity>>> e1 : entities.entrySet()) {
			for(Entry<ModelTexture,ArrayList<Entity>> e2 : e1.getValue().entrySet()) {
				es.addAll(e2.getValue());
			}
		}
		return es;
	}
	
	public static ArrayList<Light> getLights() {
		return lights;
	}
	
	public static ArrayList<BasicGUIComponent> getGUI() {
		return gui;
	}
	
	public static HashMap<FontType,ArrayList<GUIText>> getText() {
		return text;
	}
	
	public static ArrayList<GUIText> getAllText() {
		ArrayList<GUIText> t = new ArrayList<GUIText>();
		for(Entry<FontType,ArrayList<GUIText>> e : text.entrySet()) {
			t.addAll(e.getValue());
		}
		return t;
	}
	
	public static ArrayList<GUIComponent> getAllGUI() {
		ArrayList<GUIComponent> guis = new ArrayList<GUIComponent>(gui);
		guis.addAll(getAllText());
		return guis;
	}	
}