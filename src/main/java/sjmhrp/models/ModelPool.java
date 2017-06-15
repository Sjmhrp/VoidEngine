package sjmhrp.models;

import java.util.HashMap;

import sjmhrp.io.IOHandler;
import sjmhrp.render.Loader;

public class ModelPool {
	static HashMap<String,RawModel> pool = new HashMap<String,RawModel>();
	
	public static void init() {
		pool.put("quad",Loader.load(new double[] {-1,1,-1,-1,1,1,1,-1},2));
		pool.put("pointLight",IOHandler.parseOBJ("pointLight"));
	}

	public static RawModel getModel(String name) {
		RawModel m = pool.get(name);
		if(m!=null)return m;
		m = IOHandler.parseOBJ(name);
		pool.put(name,m);
		return m;
	}
}