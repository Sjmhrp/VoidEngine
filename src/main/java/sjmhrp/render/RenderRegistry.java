package sjmhrp.render;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import sjmhrp.particle.Particle;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.render.entity.Entity;
import sjmhrp.render.gui.GUIBox;
import sjmhrp.render.gui.GUIComponent;
import sjmhrp.render.gui.text.FontType;
import sjmhrp.render.gui.text.GUIText;
import sjmhrp.render.light.Light;
import sjmhrp.render.models.RawModel;
import sjmhrp.render.textures.ModelTexture;
import sjmhrp.render.textures.ParticleTexture;

public class RenderRegistry {

	static ConcurrentHashMap<RawModel,ConcurrentHashMap<ModelTexture,List<Entity>>> entities = new ConcurrentHashMap<RawModel,ConcurrentHashMap<ModelTexture,List<Entity>>>();
	static ArrayList<Light> lights = new ArrayList<Light>();
	static HashMap<ModelTexture,ArrayList<Entity>> decals = new HashMap<ModelTexture,ArrayList<Entity>>();
	static HashMap<ParticleTexture,ArrayList<Particle>> particles = new HashMap<ParticleTexture,ArrayList<Particle>>();
	static ArrayList<GUIBox> gui = new ArrayList<GUIBox>();
	static HashMap<FontType,ArrayList<GUIText>> text = new HashMap<FontType,ArrayList<GUIText>>();
	
	public static void clearEntities() {
		entities = new ConcurrentHashMap<RawModel,ConcurrentHashMap<ModelTexture,List<Entity>>>();
		lights = new ArrayList<Light>();
		decals = new HashMap<ModelTexture,ArrayList<Entity>>();
		particles = new HashMap<ParticleTexture,ArrayList<Particle>>();
	}
	
	public static void clear() {
		clearEntities();
		decals.clear();
		particles.clear();
		gui = new ArrayList<GUIBox>();
		text = new HashMap<FontType,ArrayList<GUIText>>();
	}
	
	public static void registerEntity(Entity e) {
		RawModel m = e.getModel();
		ModelTexture t = e.getTexture();
		ConcurrentHashMap<ModelTexture,List<Entity>> ess = entities.get(m);
		if(ess==null) {
			ess = new ConcurrentHashMap<ModelTexture,List<Entity>>();
			entities.put(m,ess);
		}
		List<Entity> es = ess.get(t);
		if(es==null) {
			es = Collections.synchronizedList(new ArrayList<Entity>());
			ess.put(t,es);
		}
		es.add(e);
	}

	public static void registerDecal(Entity e) {
		ModelTexture t = e.getTexture();
		ArrayList<Entity> es = decals.get(t);
		if(es==null) {
			es = new ArrayList<Entity>();
			decals.put(t,es);
		}
		es.add(e);
	}
	
	public static void registerLight(Light l) {
		lights.add(l);
	}
	
	public static void registerParticle(Particle p) {
		ArrayList<Particle> ps = particles.get(p.getTexture());
		if(ps==null) {
			ps = new ArrayList<Particle>();
			particles.put(p.getTexture(),ps);
		}
		ps.add(p);
	}
	
	public static void registerGUIComponent(GUIBox g) {
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
		ConcurrentHashMap<ModelTexture,List<Entity>> e1 = entities.get(e.getModel());
		if(e1==null)return;
		List<Entity> e2 = e1.get(e.getTexture());
		if(e2!=null)e2.remove(e);
	}
	
	public static void removeDecal(Entity e) {
		ArrayList<Entity> es = decals.get(e.getTexture());
		if(es==null)return;
		es.remove(e);
	}
	
	public static void removeLight(Light l) {
		lights.remove(l);
	}
	
	public static void removeParticle(Particle p) {
		for(Entry<ParticleTexture,ArrayList<Particle>> e : particles.entrySet()) {
			if(e.getKey()==p.getTexture()) {
				e.getValue().remove(p);
				return;
			}
		}
	}
	
	public static void removeGUIComponent(GUIBox g) {
		gui.remove(g);
	}
	
	public static void removeGUIText(GUIText t) {
		ArrayList<GUIText> g = text.get(t.getFont());
		if(g!=null)g.remove(t);
	}
	
	public static ConcurrentHashMap<RawModel,ConcurrentHashMap<ModelTexture,List<Entity>>> getEntities() {
		return entities;
	}

	public static ArrayList<Entity> getEntities(CollisionBody r) {
		return getAllEntities().stream().filter(e->e.getPosition().equals(r.getPosition())).collect(Collectors.toCollection(ArrayList::new));
	}
	
	public static ArrayList<Entity> getAllEntities() {
		ArrayList<Entity> es = new ArrayList<Entity>();
		entities.values().forEach(e->e.values().forEach(es::addAll));
		return es;
	}
	
	public static HashMap<ModelTexture,ArrayList<Entity>> getDecals() {
		return decals;
	}
	
	public static ArrayList<Light> getLights() {
		return lights;
	}
	
	public static HashMap<ParticleTexture,ArrayList<Particle>> getParticles() {
		return particles;
	}
	
	public static ArrayList<Particle> getAllParticles() {
		ArrayList<Particle> ps = new ArrayList<Particle>();
		particles.values().forEach(ps::addAll);
		return ps;
	}
	
	public static boolean containsParticle(Particle p) {
		return particles.values().stream().anyMatch(ps->ps.contains(p));
	}
	
	public static ArrayList<GUIBox> getGUI() {
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