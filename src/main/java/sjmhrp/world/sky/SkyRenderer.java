package sjmhrp.world.sky;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_DST_ALPHA;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_DST_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import sjmhrp.render.Loader;
import sjmhrp.render.RenderHandler;
import sjmhrp.render.models.ModelPool;
import sjmhrp.render.models.RawModel;
import sjmhrp.render.post.Post;
import sjmhrp.render.shader.Shader;
import sjmhrp.render.textures.TexturePool;
import sjmhrp.render.view.Camera;
import sjmhrp.utils.MatrixUtils;
import sjmhrp.utils.Profiler;
import sjmhrp.utils.ScalarUtils;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Quaternion;
import sjmhrp.utils.linear.Vector3d;

public class SkyRenderer {
	
	public static final double MIN_STAR_SIZE = 2.2;
	static final int MAX_INSTANCES = 10000;
	static final int DATA_LENGTH = 7;
	static final FloatBuffer buffer = BufferUtils.createFloatBuffer(DATA_LENGTH*MAX_INSTANCES);
	
	static int vao;
	static int vbo;
	static int glowTexture;
	static int colourTexture;

	public static void init() {
		vao = Loader.createVao();
		vbo = Loader.createEmptyVBO(MAX_INSTANCES*DATA_LENGTH);
		Loader.addInstancedAttrib(vao,vbo,0,4,DATA_LENGTH,0);
		Loader.addInstancedAttrib(vao,vbo,1,4,DATA_LENGTH,3);
		Loader.addInstancedAttrib(vao,vbo,2,4,DATA_LENGTH,6);
		glowTexture = Loader.loadTexture("map/skyGlow");
		colourTexture = Loader.loadTexture("map/skyColour");
	}

	public static void renderSky(SkyDome sky, Camera camera) {
		Post.lightSource.bindFrameBuffer();
		SkyRenderer.renderCelestial(sky,camera,Shader.getCelestialShader());
		Post.main.bindFrameBuffer();
		renderSkyDome(sky,camera,Shader.getSkyShader());
		renderCelestial(sky,camera,Shader.getCelestialShader());
		renderStars(sky,camera,Shader.getStarShader());
	}
	
	static void renderSkyDome(SkyDome sky, Camera c, SkyShader s) {
		s.start();
		glDepthMask(false);
		glDisable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_DST_ALPHA,GL_ONE_MINUS_DST_ALPHA);
		RawModel m = ModelPool.getModel("SkyDome");
		GL30.glBindVertexArray(m.getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D,glowTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D,colourTexture);
		s.loadViewMatrix(c.getRotMatrix());
		s.load(sky);
		glDrawElements(GL_TRIANGLES,m.getVertexCount(),GL_UNSIGNED_INT, 0);
		Profiler.drawCalls++;
		glDisable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
		glDepthMask(true);
		s.stop();
	}
	
	public static void renderCelestial(SkyDome sky, Camera c, CelestialShader s) {
		s.start();
		s.loadViewMatrix(c.getRotMatrix());
		glDepthMask(false);
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE_MINUS_DST_ALPHA,GL_ONE);
		GL30.glBindVertexArray(ModelPool.getModel("quad").getVaoId());
		GL20.glEnableVertexAttribArray(0);
		for(CelestialBody body : sky.getBodies()) {
			Vector3d pos = body.getPosition().getUnit();
			Quaternion orientation = new Quaternion().rotate(new Vector3d(0.5*Math.PI,0,0),1).rotate(pos.z==0&&pos.x==0?new Vector3d():new Vector3d(-pos.z,0,pos.x).normalize(),-Math.acos(pos.y));
			Matrix4d matrix = MatrixUtils.createTransform(pos.scale(sky.getSize()),orientation,body.getSize());
			s.loadTransformMatrix(matrix);
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D,TexturePool.getTexture(body.getTexture()).getAlbedoID());
			glDrawArrays(GL_TRIANGLE_STRIP,0,4);
			Profiler.drawCalls++;
		}
		RenderHandler.unbind();
		glDisable(GL_BLEND);
		glDepthMask(true);
		s.stop();
	}
	
	static void renderStars(SkyDome sky, Camera c, StarShader s) {
		s.start();
		s.loadViewMatrix(c.getRotMatrix());
		s.load(sky);
		glDepthMask(false);
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE_MINUS_DST_ALPHA,GL_ONE);
		GL30.glBindVertexArray(vao);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		int pointer = 0;
		int count = 0;
		float[] data = new float[MAX_INSTANCES*DATA_LENGTH];
		double time = 1-ScalarUtils.clamp(sky.getSun()==null?0:sky.getSun().getPosition().y*5,0,1);
		time*=time;
		for(Star star : sky.getStars()) {
			double radius = time*star.getRadius()*(1+Math.random()/5);
			if(radius<MIN_STAR_SIZE)continue;
			Vector3d pos = star.getPosition();
			Vector3d colour = star.getColour();
			data[pointer++]=(float)pos.x;
			data[pointer++]=(float)pos.y;
			data[pointer++]=(float)pos.z;
			data[pointer++]=(float)colour.x;
			data[pointer++]=(float)colour.y;
			data[pointer++]=(float)colour.z;
			data[pointer++]=(float)radius;
			count++;
		}
		if(count!=0) {
			Loader.updateVbo(vbo,data,buffer);
			GL31.glDrawArraysInstanced(GL11.GL_POINTS,0,1,count);
			Profiler.drawCalls++;
		}
		RenderHandler.unbind();
		glDisable(GL_BLEND);
		glDepthMask(true);
		s.stop();
	}
}