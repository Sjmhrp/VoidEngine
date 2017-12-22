package sjmhrp.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import sjmhrp.factory.Factory;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.render.RenderHandler;
import sjmhrp.render.RenderRegistry;
import sjmhrp.render.gui.GUIHandler;
import sjmhrp.render.light.Light;
import sjmhrp.world.World;

public class SaveHandler {

	static final String RES_LOC = "saves/";
	
	public static void saveFile() {
		JFileChooser fileChooser = new JFileChooser();
		try{
			File f = new File(SaveHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()+RES_LOC);
			while(!f.exists())f=f.getParentFile();
			fileChooser.setCurrentDirectory(f);
		} catch(Exception e) {
			e.printStackTrace();
		}
		fileChooser.setFileFilter(new FileNameExtensionFilter(".dat","dat"));
		if(fileChooser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			if(!selectedFile.toString().endsWith(".dat")) {
				selectedFile = new File(selectedFile+".dat");
			}
			saveFile(selectedFile);
		}
	}
	
	public static boolean loadFile() {
		JFileChooser fileChooser = new JFileChooser();
		try{
			File f = new File(SaveHandler.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()+RES_LOC);
			while(!f.exists())f=f.getParentFile();
			fileChooser.setCurrentDirectory(f);
		} catch(Exception e) {
			e.printStackTrace();
		}
		fileChooser.setFileFilter(new FileNameExtensionFilter(".dat","dat"));
		if(fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			return loadFile(selectedFile);
		}
		return false;
	}

	public static void saveFile(String filePath) {
		saveFile(new File(RES_LOC+filePath+".dat"));
	}
	
	public static void saveFile(File file) {
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			ObjectOutputStream stream = new ObjectOutputStream(out);
			for(World w : PhysicsEngine.getWorlds())stream.writeObject(w);
			for(Factory<?> f : Factory.getFactories())stream.writeObject(f);
			for(Light l : RenderRegistry.getLights())stream.writeObject(l);
			stream.close();
		} catch(Exception e) {
			Log.printError(e);
		}
	}
	
	public static boolean loadFile(String filePath) {
		return loadFile(new File(RES_LOC+filePath+".dat"));
	}
	
	public static boolean loadFile(File file) {
		List<World> worlds = new ArrayList<World>();
		List<Factory<?>> factories = new ArrayList<Factory<?>>();
		List<Light> lights = new ArrayList<Light>();
		GUIHandler.switchToScreen("loading");
		try {
			FileInputStream in = new FileInputStream(file);
			ObjectInputStream stream = new ObjectInputStream(in);
			Object o;
			while(true) {
				try{
					o = stream.readObject();
				} catch(Exception e) {
					break;
				}
				if(o instanceof World)worlds.add((World)o);
				if(o instanceof Factory)factories.add((Factory<?>)o);
				if(o instanceof Light)lights.add((Light)o);
			}
			stream.close();
		} catch(Exception e) {
			Log.printError(e);
			GUIHandler.switchToScreen("main");
			return false;
		}
		RenderHandler.addTask(()->{
			RenderRegistry.clearEntities();
			RenderHandler.clearMain();
			Factory.rebuild(factories);});
		PhysicsEngine.clear();
		for(World w : worlds) {
			w.reload();
			PhysicsEngine.registerWorld(w);
		}
		lights.forEach(RenderRegistry::registerLight);
		GUIHandler.switchToScreen("main");
		return true;
	}
}