package sjmhrp.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import sjmhrp.entity.EntityBuilder;
import sjmhrp.light.Light;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.render.RenderRegistry;
import sjmhrp.terrain.Terrain;
import sjmhrp.world.World;

public class SaveHandler {

	static final String RES_LOC = "saves/";
	
	public static void saveFile() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
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
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
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
			for(World w : PhysicsEngine.getWorlds()) {
				stream.writeObject(w);
			}
			for(EntityBuilder e : EntityBuilder.getEntityBuilders()) {
				stream.writeObject(e);
			}
			for(Light l : RenderRegistry.getLights()) {
				stream.writeObject(l);
			}
			stream.close();
		} catch(Exception e) {
			Log.printError(e);
		}
	}
	
	public static boolean loadFile(String filePath) {
		return loadFile(new File(RES_LOC+filePath+".dat"));
	}
	
	public static boolean loadFile(File file) {
		try {
			FileInputStream in = new FileInputStream(file);
			ObjectInputStream stream = new ObjectInputStream(in);
			RenderRegistry.clearEntities();
			PhysicsEngine.clear();
			Object o;
			while(true) {
				try{
					o = stream.readObject();
				} catch(Exception e) {
					break;
				}
				if(o instanceof World) {
					World w = (World)o;
					w.reload();
					for(Terrain t : w.getTerrain()) {
						t.reload();
					}
					PhysicsEngine.registerWorld(w);
				}
				if(o instanceof EntityBuilder) {
					EntityBuilder.entityBuilders.add((EntityBuilder)o);
					((EntityBuilder)o).build();
				}
				if(o instanceof Light) {
					RenderRegistry.registerLight((Light)o);
				}
			}
			stream.close();
			return true;
		} catch(Exception e) {
			Log.printError(e);
			return false;
		}
	}
}