package sjmhrp.render.textures;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import sjmhrp.render.Loader;
import sjmhrp.render.RenderHandler;
import sjmhrp.render.gui.text.FontShader;
import sjmhrp.render.gui.text.GUIText;
import sjmhrp.render.post.Fbo;
import sjmhrp.render.shader.Shader;
import sjmhrp.utils.ColourUtils;
import sjmhrp.utils.Profiler;
import sjmhrp.utils.linear.Vector3d;

public class TexturePool {
	
	static HashMap<String,ModelTexture> pool = new HashMap<String,ModelTexture>();
	static HashMap<String,ParticleTexture> particleTextures = new HashMap<String,ParticleTexture>();
	
	public static ModelTexture getTexture(String name) {
		ModelTexture t = pool.get(name);
		if(t==null){
			t = new ModelTexture(Loader.loadTexture(name),name);
			pool.put(name,t);
		}
		return t;
	}

	public static ModelTexture getTexture(String name, String normalMap) {
		ModelTexture t = pool.get(name);
		if(t==null) {
			t = new ModelTexture(Loader.loadTexture(name),name);
			pool.put(name,t);
		}
		if(t.getNormalID()==0)t.loadNormalMap(Loader.loadTexture("map/"+normalMap));
		return t;
	}

	public static ModelTexture getTexture(String name, String normalMap, String specularMap) {
		ModelTexture t = pool.get(name);
		if(t==null) {
			t = new ModelTexture(Loader.loadTexture(name),name);
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
	        t = new ModelTexture(texture,colour.x+";"+colour.y+";"+colour.z);
			pool.put(c,t);
		}
		return t;
	}

	public static ModelTexture getColour(String colour) {
		Vector3d c = ColourUtils.getColour(colour);
		if(c==null)return null;
		return getColour(c);
	}
	
	public static ModelTexture createText(String text, String font, Vector3d colour) {
		return createText(text,font,colour,128);
	}
	
	public static ModelTexture createText(String text, String font, Vector3d colour, int resolution) {
		ModelTexture t = pool.get(text+";"+font+";"+colour);
		if(t==null) {
			GUIText g = new GUIText(text,font);
			g.setColour(colour);
			Fbo fbo = new Fbo(resolution,resolution,Fbo.DEPTH_TEXTURE);
			fbo.bindFrameBuffer();
			RenderHandler.clear();
			FontShader s = Shader.getFontShader();
			s.start();
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D,g.getFont().getTextureAtlas());
			s.load(g);
			GL30.glBindVertexArray(g.getModel().getVaoId());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			glDrawArrays(GL_TRIANGLES,0,g.getVertexCount());
			Profiler.drawCalls++;
			GL20.glDisableVertexAttribArray(0);
			GL20.glDisableVertexAttribArray(1);
			GL30.glBindVertexArray(0);
			s.stop();
			fbo.unbindFrameBuffer();
			t = new ModelTexture(fbo.getColourTexture(),text+";"+font+";"+colour);
			pool.put(text+";"+font+";"+colour,t);
		}
		return t;
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
	
	public static ParticleTexture getParticleTexture(String name) {
		return getParticleTexture(name,1);
	}
	
	public static ParticleTexture getParticleTexture(String name, int rows) {
		ParticleTexture t = particleTextures.get(name);
		if(t==null){
			t = new ParticleTexture(Loader.loadTexture("particle/"+name),rows);
			particleTextures.put(name,t);
		}
		return t;
	}
}