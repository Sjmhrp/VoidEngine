package sjmhrp.flare;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import org.lwjgl.opengl.Display;

import sjmhrp.linear.Matrix3d;
import sjmhrp.linear.Vector3d;
import sjmhrp.post.Fbo;
import sjmhrp.post.Post;
import sjmhrp.render.Loader;
import sjmhrp.render.RenderHandler;
import sjmhrp.shaders.Shader;
import sjmhrp.view.Camera;

public class FlareRenderer {

	public static Fbo lensFlare = new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
	public static Fbo temp = new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
	
	static int flareColour;
	static int lensStar;
	
	static Matrix3d scaleBias1 = new Matrix3d(2,0,-1,0,2,-1,0,0,1);
	static Matrix3d scaleBias2 = new Matrix3d(0.5,0,0.5,0,0.5,0.5,0,0,1);
	
	public static void init() {
		flareColour = Loader.loadTexture("map/flareColour");
		lensStar = Loader.loadTexture("map/lensStar");
	}
	
	public static void cleanUp() {
		lensFlare.cleanUp();
	}
	
	public static void renderFlares(Shader s, Camera c) {
		lensFlare.bindFrameBuffer();
		s.getDownSampleShader().start();
		s.getDownSampleShader().loadBias(new Vector3d(1.2));
		RenderHandler.clear();
		RenderHandler.renderQuad(Post.main.getColourTexture());
		s.getDownSampleShader().stop();
		temp.bindFrameBuffer();
		s.getFeatureShader().start();
		s.getFeatureShader().load(5,0.37,0.4,0.1);
		RenderHandler.clear();
		RenderHandler.renderQuad(lensFlare.getColourTexture(),flareColour);
		s.getFeatureShader().stop();
		temp.unbindFrameBuffer();
		Post.clear();
		Post.addToPipeline(s.getHBlurShader());
		Post.addToPipeline(s.getVBlurShader());
		Post.process(temp,lensFlare);
		Post.main.bindFrameBuffer();
		s.getFlareShader().start();
		double crot = c.getRotMatrix().m20+c.getRotMatrix().m11;
		Matrix3d rot = new Matrix3d(Math.cos(crot),-Math.sin(crot),0,Math.sin(crot),Math.cos(crot),0,0,0,0);
		s.getFlareShader().loadStarMatrix(Matrix3d.mul(scaleBias2,rot.mul(scaleBias1)));
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE,GL_ONE);
		RenderHandler.renderQuad(lensFlare.getColourTexture(),lensStar);
		glDisable(GL_BLEND);
		s.getFlareShader().stop();
		Post.main.unbindFrameBuffer();
	}
}