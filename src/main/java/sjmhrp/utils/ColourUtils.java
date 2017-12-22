package sjmhrp.utils;

import java.util.HashMap;

import sjmhrp.utils.linear.Vector3d;

public class ColourUtils {

	static HashMap<String,Vector3d> names = new HashMap<String,Vector3d>();
	
	static {names.put("black",new Vector3d());
			names.put("white",new Vector3d(1));
			names.put("red",new Vector3d(1,0,0));
			names.put("green",new Vector3d(0,1,0));
			names.put("blue",new Vector3d(0,0,1));
			names.put("cyan",new Vector3d(0,0.635,0.91));
			names.put("lime",new Vector3d(0.133,0.694,0.298));
	};
	
	public static Vector3d getColour(String name) {
		Vector3d colour = names.get(name);
		return colour==null?new Vector3d():colour;
	}
}