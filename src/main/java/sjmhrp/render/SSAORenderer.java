package sjmhrp.render;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL30;

import sjmhrp.linear.Vector3d;
import sjmhrp.post.Fbo;
import sjmhrp.post.Post;
import sjmhrp.shaders.Shader;
import sjmhrp.utils.ScalarUtils;
import sjmhrp.view.Camera;

public class SSAORenderer {

	static int ssaoNoise;
	static Vector3d[] samples = new Vector3d[64];
	static Fbo SSAO1 = new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);
	public static Fbo SSAO2 = new Fbo(Display.getWidth(),Display.getHeight(),Fbo.DEPTH_TEXTURE);

	public static void init() {
		genNoise();
		genSamples();
	}

	static void genNoise() {
		int bufferSize = 48*Float.SIZE;
		FloatBuffer noise = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
		Random r = new Random();
		for(int i = 0; i < 16; i++) {
			noise.put(r.nextFloat()*2-1).put(r.nextFloat()*2-1).put(0);
		}
		noise.flip();
		ssaoNoise = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, ssaoNoise);
        glTexImage2D(GL_TEXTURE_2D, 0, GL30.GL_RGB16F, 4, 4, 0, GL_RGB, GL_FLOAT, noise);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
	}

	static void genSamples() {
		Random r = new Random();
		for(int i = 0; i < 64; i++) {
			Vector3d sample = new Vector3d(r.nextFloat()*2-1,r.nextFloat()*2-1,r.nextFloat());
			sample.normalize();
			sample.scale(r.nextFloat());
			double scale = i/64;
			scale = ScalarUtils.lerp(0.1f, 1, scale * scale);
			sample.scale(scale);
			samples[i] = sample;
		}
	}

	public static void cleanUp() {
		SSAO1.cleanUp();
		SSAO2.cleanUp();
	}
	
	static void renderSSAO(Shader s, Camera c) {
		SSAO1.bindFrameBuffer();
		RenderHandler.clear();
		s.getSSAOShader().start();
		s.getSSAOShader().loadSamples(samples);
		s.getSSAOShader().loadViewMatrix(c.getViewMatrix());
		RenderHandler.renderQuad(Post.albedo.getDepthTexture(),Post.normal.getColourTexture(),ssaoNoise);
		s.getSSAOShader().stop();
		SSAO2.bindFrameBuffer();
		RenderHandler.clear();
		s.getSSAOBlurShader().start();
		RenderHandler.renderQuad(SSAO1.getColourTexture());
		s.getSSAOBlurShader().stop();
		SSAO2.unbindFrameBuffer();
	}
}