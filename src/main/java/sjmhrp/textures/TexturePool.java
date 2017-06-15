package sjmhrp.textures;

import java.util.HashMap;

import sjmhrp.render.Loader;

public class TexturePool {
	
	static HashMap<String,ModelTexture> pool = new HashMap<String,ModelTexture>();

	public static ModelTexture getTexture(String name) {
		ModelTexture t = pool.get(name);
		if(t!=null)return t;
		t = new ModelTexture(Loader.loadTexture(name));
		pool.put(name,t);
		return t;
	}

	public static ModelTexture getTexture(String name, String normalMap) {
		ModelTexture t = pool.get(name);
		if(t==null) {
			t = new ModelTexture(Loader.loadTexture(name));
			pool.put(name,t);
		}
		if(t.getNormalID()==0)t.loadNormalMap(Loader.loadTexture("map/"+normalMap));
		return t;
	}

	public static ModelTexture getTexture(String name, String normalMap, String specularMap) {
		ModelTexture t = pool.get(name);
		if(t==null) {
			t = new ModelTexture(Loader.loadTexture(name));
			pool.put(name,t);
		}
		if(t.getNormalID()==0)t.loadNormalMap(Loader.loadTexture("map/"+normalMap));
		if(t.getSpecularID()==0)t.loadSpecularMap(Loader.loadTexture("map/"+specularMap));;
		return t;
	}

	public static void addNormalMap(String texture, String map) {
		ModelTexture t = pool.get(texture);
		if(t==null)return;
		t.loadNormalMap(Loader.loadTexture("map/"+map));
	}

	public static void addSpecularMap(String texture, String map) {
		ModelTexture t = pool.get(texture);
		if(t==null)return;
		t.loadSpecularMap(Loader.loadTexture("map/"+map));
	}
}