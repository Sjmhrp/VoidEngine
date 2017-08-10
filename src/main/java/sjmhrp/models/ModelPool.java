package sjmhrp.models;

import java.util.HashMap;

import sjmhrp.io.OBJHandler;
import sjmhrp.render.Loader;

public class ModelPool {
	static HashMap<String,RawModel> pool = new HashMap<String,RawModel>();
	
	public static void init() {
		pool.put("quad",Loader.load(new double[] {-1,1,-1,-1,1,1,1,-1},2));
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
}