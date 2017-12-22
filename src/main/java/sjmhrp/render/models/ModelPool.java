package sjmhrp.render.models;

import java.util.HashMap;

import sjmhrp.io.OBJHandler;
import sjmhrp.render.Loader;
import sjmhrp.render.animation.AnimatedModel;

public class ModelPool {

	static final String RES_LOC = "/res/models/";
	static HashMap<String,RawModel> pool = new HashMap<String,RawModel>();
	
	public static void init() {
		pool.put("quad",Loader.load(new double[] {-1,1,-1,-1,1,1,1,-1},2));
		pool.put("3quad",Loader.load(new double[]{1,1,0,1,-1,0,-1,-1,0,-1,1,0},new int[]{2,0,3,1,0,2},new double[]{0,0,1,0,0,1,0,0,1,0,0,1},new double[]{1,0,1,1,0,1,0,0}));
		pool.put("pointLight",OBJHandler.parseOBJ("pointLight"));
	}

	public static RawModel getModel(String name) {
		RawModel m = pool.get(name);
		if(m==null) {
			m = OBJHandler.parseOBJ(name);
			pool.put(name,m);
		}
		return m;
	}
	
	public static AnimatedModel getAnimatedModel(String name) {
		RawModel m = pool.get(name);
		if(m==null||!(m instanceof AnimatedModel)) {
			m = Loader.loadAnimatedModel(RES_LOC+name+".dae");
			pool.put(name,m);
		}
		return (AnimatedModel)m;
	}
}