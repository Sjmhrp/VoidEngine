package sjmhrp.render.light;

import static org.lwjgl.opengl.GL11.GL_ALWAYS;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_GEQUAL;
import static org.lwjgl.opengl.GL11.GL_KEEP;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_REPLACE;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColorMask;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glStencilFunc;
import static org.lwjgl.opengl.GL11.glStencilMask;
import static org.lwjgl.opengl.GL11.glStencilOp;

import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import sjmhrp.physics.PhysicsEngine;
import sjmhrp.render.RenderHandler;
import sjmhrp.render.RenderRegistry;
import sjmhrp.render.models.ModelPool;
import sjmhrp.render.post.Post;
import sjmhrp.render.shader.Shader;
import sjmhrp.render.view.Camera;
import sjmhrp.utils.MatrixUtils;
import sjmhrp.utils.Profiler;
import sjmhrp.world.World;
import sjmhrp.world.sky.CelestialBody;
import sjmhrp.world.sky.SkyDome;

public class LightRenderer {
	
	public static void renderLights(Camera c) {
		RenderHandler.gBuffer.resolveDepth(Post.lighting);
		Post.lighting.bindFrameBuffer();
		glClear(GL_COLOR_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE,GL_ONE);
		glDepthMask(false);
		PhysicsEngine.getWorlds().forEach(world->renderLights(world,c));
		glDepthMask(true);
		glDisable(GL_BLEND);
		Post.lighting.unbindFrameBuffer();
		Post.lighting.resolve(0,Post.light);
		Post.lighting.resolve(1,Post.lightSource);
		Post.lighting.resolve(2,Post.bloom);
	}
	
	static void renderLights(World w, Camera c) {
		if(w.hasSky()&&w.getSky().getBodies().size()>0)renderSunLight(w.getSky(),Shader.getSunLightShader(),c);
		renderLights(Shader.getLightShader(),c);
	}

	static void renderSunLight(SkyDome sky, SunLightShader s, Camera c) {
		s.start();
		s.loadViewMatrix(c.getRotMatrix());
		for(CelestialBody b : sky.getBodies()) {
			s.load(sky,b);
			RenderHandler.renderQuad(Post.albedo.getColourTexture(),Post.normal.getColourTexture(),Post.mask.getColourTexture(),Post.albedo.getDepthTexture());
		}
		s.stop();
	}
	
	static void renderLights(LightShader s, Camera c) {
		s.start();
		s.loadViewMatrix(c.getViewMatrix());
		GL30.glBindVertexArray(ModelPool.getModel("pointLight").getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, Post.albedo.getColourTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, Post.normal.getColourTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		glBindTexture(GL_TEXTURE_2D, Post.mask.getColourTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		glBindTexture(GL_TEXTURE_2D, Post.albedo.getDepthTexture());
		glEnable(GL_STENCIL_TEST);
		for(Light l : RenderRegistry.getLights()) {
			s.loadTransformMatrix(MatrixUtils.createTransform(l.getPos(),l.getSize()));
			s.loadLight(l);
			renderLightFirstPass(s,l);
			renderLightSecondPass(s,l);
		}
		glDisable(GL_STENCIL_TEST);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		s.stop();
	}

	static void renderLightFirstPass(LightShader s, Light l) {
		glColorMask(false,false,false,false);
		glStencilFunc(GL_ALWAYS,1,0xFF);
		glStencilOp(GL_KEEP,GL_REPLACE,GL_KEEP);
		glStencilMask(0xFF);
		glClear(GL_STENCIL_BUFFER_BIT);
		renderLight(s,l);
		glColorMask(true,true,true,false);
	}

	static void renderLightSecondPass(LightShader s, Light l) {
		glCullFace(GL_FRONT);
		glDepthFunc(GL_GEQUAL);
		glStencilFunc(GL_EQUAL,0,0xFF);
		glStencilMask(0x00);
		renderLight(s,l);
		glDepthFunc(GL_LEQUAL);
		glCullFace(GL_BACK);
		glColorMask(true,true,true,true);
	}

	static void renderLight(LightShader s, Light l) {
		glDrawElements(GL_TRIANGLES,ModelPool.getModel("pointLight").getVertexCount(),GL_UNSIGNED_INT, 0);
		Profiler.drawCalls++;
	}
}