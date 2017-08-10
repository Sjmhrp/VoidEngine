package sjmhrp.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTTextureFilterAnisotropic;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLContext;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import sjmhrp.io.Log;
import sjmhrp.models.MeshData;
import sjmhrp.models.RawModel;

public class Loader {

	private static ArrayList<Integer> vaos = new ArrayList<Integer>();
	private static ArrayList<Integer> vbos = new ArrayList<Integer>();
	private static ArrayList<Integer> textures = new ArrayList<Integer>();

	public static RawModel load(double[] pos, int[] indices, double[] normals, double[] uvs) {
		int id = create();
		int i = bindIndices(indices);
		int v = store(0,3,pos);
		int u = store(1,2,uvs);
		int n = store(2,3,normals);
		unbind();
		MeshData m = new MeshData(pos,v,uvs,u,normals,n,indices,i);
		return new RawModel(id,indices.length,m);
	}

	public static RawModel load(double[] pos, int[] indices, double[] normals, double[] tangents, double[] uv) {
		int id = create();
		int i = bindIndices(indices);
		int v = store(0,3,pos);
		int u = store(1,2,uv);
		int n = store(2,3,normals);
		store(3,3,tangents);
		MeshData m = new MeshData(pos,v,uv,u,normals,n,indices,i);
		return new RawModel(id,indices.length,m);
	}

	public static RawModel load(double[] pos, int dim) {
		int id = create();
		int vbo = store(0,dim,pos);
		unbind();
		MeshData m = new MeshData(pos,vbo);
		return new RawModel(id, pos.length/dim,m);
	}

	public static RawModel load(double[] pos, double[] texPos) {
        int id = create();
        int v = store(0,2,pos);
        int u = store(1,2,texPos);
        unbind();
        MeshData m = new MeshData(pos,v,texPos,u);
        return new RawModel(id,pos.length/2,m);
	}
	
	public static int loadTexture(String file) {
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG",Class.class.getResourceAsStream("/res/textures/"+file+".png"));
			GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, 0);
			if(GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic) {
				float amount = Math.min(4,GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
			} else {
				Log.println("Anisotropic Filtering is not supported");
			}
		} catch (Exception e) {
			Log.println("Tried to load "+file+".png");
			Log.printError(e);
		}
		int id = texture.getTextureID();
		textures.add(id);
		return id;
	}

	public static void cleanUp() {
		for(int i : vaos) {
			GL30.glDeleteVertexArrays(i);
		}
		for(int i : vbos) {
			GL15.glDeleteBuffers(i);
		}
		for(int i : textures) {
			GL11.glDeleteTextures(i);
		}
	}

	private static int create() {
		int id = GL30.glGenVertexArrays();
		vaos.add(id);
		GL30.glBindVertexArray(id);
		return id;
	}
	
	private static int store(int n, int size, double[] data) {
		int id = GL15.glGenBuffers();
		vbos.add(id);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id);
		FloatBuffer f = convertFloats(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, f, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(n, size, GL11.GL_FLOAT,false,0,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return id;
	}
	
	public static void updateVbo(int vbo, int n, int size, double[] data) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		FloatBuffer f = convertFloats(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, f, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(n, size, GL11.GL_FLOAT,false,0,0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public static void updateVbo(int vbo, float[] data, FloatBuffer buffer) {
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity() * Float.BYTES, GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public static int createEmptyVBO(float floatCount) {
		int vbo = GL15.glGenBuffers();
		vbos.add(vbo);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (long) (floatCount * Float.BYTES), GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vbo;
	}

	public static void addInstancedAttrib(int vao, int vbo, int attribute, int dataSize, int instancedDataLength, int offset) {
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL30.glBindVertexArray(vao);
		GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, instancedDataLength * Float.BYTES, offset * Float.BYTES);
		GL33.glVertexAttribDivisor(attribute, 1);
		GL30.glBindVertexArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	public static int createVao() {
		int id = create();
		unbind();
		return id;
	}

	private static void unbind() {
		GL30.glBindVertexArray(0);
	}

	private static int bindIndices(int[] indices) {
		int id = GL15.glGenBuffers();
		vbos.add(id);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, id);
		IntBuffer i = convertInts(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, i, GL15.GL_STATIC_DRAW);
		return id;
	}

	private static IntBuffer convertInts(int[] data) {
		IntBuffer i = BufferUtils.createIntBuffer(data.length);
		i.put(data);
		i.flip();
		return i;
	}

	private static FloatBuffer convertFloats(double[] data) {
		FloatBuffer f = BufferUtils.createFloatBuffer(data.length);
		for(double d : data) {
			f.put((float)d);
		}
		f.flip();
		return f;
	}
}