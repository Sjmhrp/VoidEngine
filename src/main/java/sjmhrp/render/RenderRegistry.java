package sjmhrp.render;

import java.util.ArrayList;
import java.util.HashMap;

import sjmhrp.entity.Entity;
import sjmhrp.light.Light;
import sjmhrp.models.RawModel;
import sjmhrp.textures.ModelTexture;

public class RenderRegistry {

	static HashMap<RawModel,HashMap<ModelTexture,ArrayList<Entity>>> entities = new HashMap<RawModel,HashMap<ModelTexture,ArrayList<Entity>>>();
	static ArrayList<Light> lights = new ArrayList<Light>();
	
	public static void clear() {
		entities = new HashMap<RawModel,HashMap<ModelTexture,ArrayList<Entity>>>();
		lights = new ArrayList<Light>();
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

	public static HashMap<RawModel, HashMap<ModelTexture, ArrayList<Entity>>> getEntities() {
		return entities;
	}

	public static ArrayList<Light> getLights() {
		return lights;
	}
}