package sjmhrp.post;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;

import sjmhrp.render.RenderHandler;
import sjmhrp.shaders.PostShaderProgram;
import sjmhrp.shaders.ShaderProgram;

public class Post {

	static ArrayList<PostShaderProgram> post = new ArrayList<PostShaderProgram>();
	static Fbo[] fboCache = new Fbo[10];

	public static Fbo main   = new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_STENCIL_BUFFER);
	public static Fbo albedo = new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
	public static Fbo normal = new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
	public static Fbo light  = new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);

	public static void init() {
		for(int i = 0; i < fboCache.length; i++) {
			fboCache[i] = new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
		}
	}

	public static void clear() {
		post.clear();
	}

	public static void addToPipeline(PostShaderProgram s) {
		post.add(s);
	}

	public static void process(Fbo input, Fbo output) {
		int texture = input.getColourTexture();
		for(int i = 0; i < post.size()-1; i++) {;
			ShaderProgram s = post.get(i);
			fboCache[i].bindFrameBuffer();
			RenderHandler.clear();
			s.start();
			RenderHandler.clear();
			RenderHandler.renderQuad(texture);
			s.stop();
			fboCache[i].unbindFrameBuffer();
			texture = fboCache[i].getColourTexture();
		}
		output.bindFrameBuffer();
		RenderHandler.clear();
		post.get(post.size()-1).start();
		RenderHandler.renderQuad(texture);
		post.get(post.size()-1).stop();
		output.unbindFrameBuffer();
	}

	public static void display(Fbo f) {
		int texture = f.getColourTexture();
		for(int i = 0; i < post.size()-1; i++) {;
			ShaderProgram s = post.get(i);
			fboCache[i].bindFrameBuffer();
			RenderHandler.clear();
			s.start();
			RenderHandler.clear();
			RenderHandler.renderQuad(texture);
			s.stop();
			fboCache[i].unbindFrameBuffer();
			texture = fboCache[i].getColourTexture();
		}
		RenderHandler.clear();
		post.get(post.size()-1).start();
		RenderHandler.renderQuad(texture);
		post.get(post.size()-1).stop();
	}

	public static void cleanUp() {
		main.cleanUp();
		albedo.cleanUp();
		normal.cleanUp();
		light.cleanUp();
		for(Fbo f : fboCache) {
			f.cleanUp();
		}
	}
}
