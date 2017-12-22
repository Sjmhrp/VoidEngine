package sjmhrp.render.post;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.ArrayList;

import org.lwjgl.opengl.Display;

import sjmhrp.render.RenderHandler;
import sjmhrp.render.shader.PostShaderProgram;

public class Post {

	static ArrayList<PostShaderProgram> post = new ArrayList<PostShaderProgram>();
	static ArrayList<Fbo> fboCache0 = new ArrayList<Fbo>();
	static ArrayList<Fbo> fboCache1 = new ArrayList<Fbo>();
	static ArrayList<Fbo> fboCache2 = new ArrayList<Fbo>();
	
	static ArrayList<Fbo> pipeline = new ArrayList<Fbo>();

	public static Fbo main   = new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_STENCIL_BUFFER);
	public static Fbo albedo = new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
	public static Fbo normal = new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
	// r channel - highlight mask
	// g channel - glow mask
	// b channel - currently unused
	public static Fbo mask = new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
	public static Fbo bloom = new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
	public static Fbo lighting = new Fbo(Display.getWidth(),Display.getHeight());
	public static Fbo light = new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
	public static Fbo lightSource = new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);

	public static void init() {
		for(int i = 0; i < 10; i++) {
			fboCache0.add(new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE));
			fboCache1.add(new Fbo(Display.getWidth()/2,Display.getHeight()/2,Fbo.DEPTH_TEXTURE));
			fboCache2.add(new Fbo(Display.getWidth()/4,Display.getHeight()/4,Fbo.DEPTH_TEXTURE));
		}
	}

	public static void clear() {
		post.clear();
		pipeline.clear();
	}

	public static void addToPipeline(PostShaderProgram s) {
		addToPipeline(s,0);
	}
	
	public static void addToPipeline(PostShaderProgram s, int size) {
		pipeline.add(fbo(size).get(post.size()));
		post.add(s);
	}

	public static void process(Fbo input, Fbo output) {
		process(input,output,false);
	}
	
	public static void process(Fbo input, Fbo output, boolean add) {
		process(input.getColourTexture(),output,add);
	}
	
	public static void process(int t, Fbo output, boolean add) {
		int texture = t;
		for(int i = 0; i < post.size()-1; i++) {;
			PostShaderProgram s = post.get(i);
			pipeline.get(i).bindFrameBuffer();
			RenderHandler.clear();
			s.start();
			RenderHandler.renderQuad(s.getTextures(texture));
			s.stop();
			pipeline.get(i).unbindFrameBuffer();
			texture = pipeline.get(i).getColourTexture();
		}
		output.bindFrameBuffer();
		if(add) {
			glEnable(GL_BLEND);
			glBlendFunc(GL_ONE,GL_ONE);
		} else {
			RenderHandler.clear();
		}
		post.get(post.size()-1).start();
		RenderHandler.renderQuad(post.get(post.size()-1).getTextures(texture));
		post.get(post.size()-1).stop();
		if(add)glDisable(GL_BLEND);
		output.unbindFrameBuffer();
	}

	public static void display(Fbo f) {
		int texture = f.getColourTexture();
		for(int i = 0; i < post.size()-1; i++) {;
			PostShaderProgram s = post.get(i);
			pipeline.get(i).bindFrameBuffer();
			RenderHandler.clear();
			s.start();
			RenderHandler.clear();
			RenderHandler.renderQuad(s.getTextures(texture));
			s.stop();
			pipeline.get(i).unbindFrameBuffer();
			texture = pipeline.get(i).getColourTexture();
		}
		RenderHandler.clear();
		post.get(post.size()-1).start();
		RenderHandler.renderQuad(texture);
		post.get(post.size()-1).stop();
	}

	static ArrayList<Fbo> fbo(int size) {
		return size==0?fboCache0:size==1?fboCache1:fboCache2;
	}
	
	public static void cleanUp() {
		main.cleanUp();
		albedo.cleanUp();
		normal.cleanUp();
		mask.cleanUp();
		bloom.cleanUp();
		lighting.cleanUp();
		light.cleanUp();
		lightSource.cleanUp();
		fboCache0.forEach(Fbo::cleanUp);
		fboCache1.forEach(Fbo::cleanUp);
		fboCache2.forEach(Fbo::cleanUp);
		pipeline.forEach(Fbo::cleanUp);
	}
}
