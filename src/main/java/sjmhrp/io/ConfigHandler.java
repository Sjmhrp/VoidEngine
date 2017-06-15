package sjmhrp.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigHandler {

	static HashMap<String,String> config = new HashMap<String,String>();
	static HashMap<String,ArrayList<Integer>> keyBindings = new HashMap<String,ArrayList<Integer>>();
	
	public static void loadConfigFiles() {
		readEngineConfig();
		readKeyBindings();
	}
	
	static void readEngineConfig() {
		try {
			InputStreamReader in = new InputStreamReader(Class.class.getResourceAsStream("/config/Engine.cfg"));
			BufferedReader reader = new BufferedReader(in);
			String line = "";
			while(line!=null) {
				if(line.contains("=")&&line.split("=").length==2){
					String[] s = line.split("=");
					config.put(s[0],s[1]);
				}
				line = reader.readLine();
			}
			in.close();
		} catch (Exception e) {
			Log.printError(e);
		}
	}

	static void readKeyBindings() {
		try {
			InputStreamReader in = new InputStreamReader(Class.class.getResourceAsStream("/config/KeyBindings.cfg"));
			BufferedReader reader = new BufferedReader(in);
			String line = "";
			while(line!=null) {
				if(line.contains("=")&&line.split("=").length==2){
					String[] s = line.split("=");
					Integer i = null;
					try{i = Integer.valueOf(s[0]);}catch(Exception e){};
					if(i!=null) {
						if(!keyBindings.containsKey(s[1])) {
							keyBindings.put(s[1],new ArrayList<Integer>());
						}
						keyBindings.get(s[1]).add(i);
					}
				}
				line = reader.readLine();
			}
			in.close();
		} catch (Exception e) {
			Log.printError(e);
		}
	}
	
	public static double getDouble(String key) {
		if(config.containsKey(key)) {
			return Double.parseDouble(config.get(key));
		}
		return 0;
	}
	
	public static int getInt(String key) {
		if(config.containsKey(key)) {
			return Integer.parseInt(config.get(key));
		}
		return 0;
	}	
	
	public static String get(String key) {
		return config.get(key);
	}
	
	public static ArrayList<Integer> getKeyBinding(String action) {
		return keyBindings.get(action);
	}
}