package sjmhrp.render.flare;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import org.lwjgl.opengl.Display;

import sjmhrp.render.Loader;
import sjmhrp.render.RenderHandler;
import sjmhrp.render.post.Fbo;
import sjmhrp.render.post.Post;
import sjmhrp.render.shader.Shader;
import sjmhrp.render.view.Camera;
import sjmhrp.utils.linear.Matrix3d;
import sjmhrp.utils.linear.Vector3d;

public class FlareRenderer {

	static final Vector3d BIAS = new Vector3d(0.7);
	static final int SAMPLES = 5;
	static final double DISPERSAL = 0.37;
	static final double HALO_WIDTH = 0.4;
	static final double DISTORTION = 0.1;
	
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
	
	public static void renderFlares(Camera c) {
		lensFlare.bindFrameBuffer();
		Shader.getDownSampleShader().start();
		Shader.getDownSampleShader().loadBias(BIAS);
		RenderHandler.clear();
		RenderHandler.renderQuad(Post.lightSource.getColourTexture());
		Shader.getDownSampleShader().stop();
		temp.bindFrameBuffer();
		Shader.getFeatureShader().start();
		Shader.getFeatureShader().load(SAMPLES,DISPERSAL,HALO_WIDTH,DISTORTION);
		RenderHandler.clear();
		RenderHandler.renderQuad(lensFlare.getColourTexture(),flareColour);
		Shader.getFeatureShader().stop();
		temp.unbindFrameBuffer();
		Post.clear();
		Post.addToPipeline(Shader.getHBlurShader());
		Post.addToPipeline(Shader.getVBlurShader());
		Post.process(temp,lensFlare);
		Post.main.bindFrameBuffer();
		Shader.getFlareShader().start();
		double crot = c.getRotMatrix().m20+c.getRotMatrix().m11;
		Matrix3d rot = new Matrix3d(Math.cos(crot),-Math.sin(crot),0,Math.sin(crot),Math.cos(crot),0,0,0,1);
		Shader.getFlareShader().loadStarMatrix(Matrix3d.mul(scaleBias2,rot.mul(scaleBias1)));
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE,GL_ONE);
		RenderHandler.renderQuad(lensFlare.getColourTexture(),lensStar);
		glDisable(GL_BLEND);
		Shader.getFlareShader().stop();
		Post.main.unbindFrameBuffer();
	}
}