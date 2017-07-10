package sjmhrp.textures;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import sjmhrp.linear.Vector3d;
import sjmhrp.render.Loader;

public class TexturePool {
	
	static HashMap<String,ModelTexture> pool = new HashMap<String,ModelTexture>();
	static HashMap<String,Vector3d> colourNames = new HashMap<String,Vector3d>();

	static {addColourName("red",new Vector3d(1,0,0));
			addColourName("green",new Vector3d(0,1,0));
			addColourName("blue",new Vector3d(0,0,1));
			addColourName("cyan",new Vector3d(0,0.635,0.91));
			addColourName("lime",new Vector3d(0.133,0.694,0.298));
	};
	
	public static void addColourName(String name, Vector3d colour) {
		colourNames.put(name,colour);
	}
	
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

	public static ModelTexture getColour(Vector3d colour) {
		String c = colour.x+";"+colour.y+";"+colour.z;
		ModelTexture t = pool.get(c);
		if(t==null) {
			FloatBuffer buffer = ByteBuffer.allocateDirect(4*Float.SIZE).order(ByteOrder.nativeOrder()).asFloatBuffer();
			buffer.put((float)colour.x).put((float)colour.y).put((float)colour.z).put(1);
			buffer.flip();
			int texture = GL11.glGenTextures();
			glBindTexture(GL_TEXTURE_2D, texture);
	        GL11.glTexImage2D(GL11.GL_TEXTURE_2D,0,GL30.GL_RGBA32F,1,1,0,GL11.GL_RGBA,GL11.GL_FLOAT,buffer);
	        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
	        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
	        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_REPEAT);
	        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_REPEAT);
	        t = new ModelTexture(texture);
			pool.put(c,t);
		}
		return t;
	}

	public static ModelTexture getColour(String colour) {
		Vector3d c = colourNames.get(colour);
		if(c==null)return null;
		return getColour(c);
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