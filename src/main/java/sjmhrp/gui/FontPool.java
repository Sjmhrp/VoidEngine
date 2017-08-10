package sjmhrp.gui;

import java.util.HashMap;

import sjmhrp.gui.text.FontType;

public class FontPool {

	static HashMap<String,FontType> pool = new HashMap<String,FontType>();

	public static FontType getFont(String name) {
		FontType f = pool.get(name);
		if(f==null){
			f = new FontType(name);
			pool.put(name,f);
		}
		return f;
	}
}