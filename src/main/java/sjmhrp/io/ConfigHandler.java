package sjmhrp.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class ConfigHandler {

	static final String PATH = "config/Engine.cfg";
	static HashMap<String,ArrayList<Integer>> keyBindings = new HashMap<String,ArrayList<Integer>>();
	static Properties config = new Properties();
	
	public static void loadConfigFiles() {
		readEngineConfig();
		readKeyBindings();
	}
	
	static void readEngineConfig() {
		try {
			try {
				config.load(new FileInputStream(PATH));
			} catch(Exception e) {
				config.load(Class.class.getResourceAsStream("/"+PATH));	
			}
		} catch (Exception e) {
			Log.printError(e);
		}
	}

	public static void updateEngineConfig() {
		try {
			File configFile = new File(PATH);
			if(!configFile.exists()) {
				configFile.getParentFile().mkdirs();
				configFile.createNewFile();
			}
			config.store(new FileOutputStream(PATH),null);
		} catch(Exception e) {
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
			return Double.parseDouble(config.getProperty(key));
		}
		return 0;
	}
	
	public static int getInt(String key) {
		if(config.containsKey(key)) {
			return Integer.parseInt(config.getProperty(key));
		}
		return 0;
	}	
	
	public static boolean getBoolean(String key) {
		if(config.containsKey(key)) {
			return Boolean.valueOf(config.getProperty(key));
		}
		return false;
	}
	
	public static String get(String key) {
		return config.getProperty(key);
	}
	
	public static void setProperty(String key, Object value) {
		setProperty(key,value,true);
	}
	
	public static void setProperty(String key, Object value, boolean update) {
		config.setProperty(key,value.toString());
		if(update)updateEngineConfig();
	}
	
	public static ArrayList<Integer> getKeyBinding(String action) {
		return keyBindings.get(action);
	}
}