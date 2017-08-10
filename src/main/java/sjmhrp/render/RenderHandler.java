package sjmhrp.render;

import static org.lwjgl.opengl.ARBDepthClamp.GL_DEPTH_CLAMP;
import static org.lwjgl.opengl.GL11.GL_ALWAYS;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_GEQUAL;
import static org.lwjgl.opengl.GL11.GL_KEEP;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_REPLACE;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_TEST;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glColorMask;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glStencilFunc;
import static org.lwjgl.opengl.GL11.glStencilMask;
import static org.lwjgl.opengl.GL11.glStencilOp;
import static org.lwjgl.opengl.GL11.glViewport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;

import sjmhrp.debug.DebugRenderer;
import sjmhrp.entity.Entity;
import sjmhrp.entity.EntityShader;
import sjmhrp.flare.FlareRenderer;
import sjmhrp.gui.BasicGUIComponent;
import sjmhrp.gui.GUIShader;
import sjmhrp.gui.text.FontShader;
import sjmhrp.gui.text.FontType;
import sjmhrp.gui.text.GUIText;
import sjmhrp.io.ConfigHandler;
import sjmhrp.io.Log;
import sjmhrp.light.Light;
import sjmhrp.light.LightShader;
import sjmhrp.light.SunLightShader;
import sjmhrp.linear.Vector3d;
import sjmhrp.models.ModelPool;
import sjmhrp.models.RawModel;
import sjmhrp.models.TexturedModel;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.post.Fbo;
import sjmhrp.post.GBufferShader;
import sjmhrp.post.Post;
import sjmhrp.shaders.Shader;
import sjmhrp.sky.CelestialBody;
import sjmhrp.sky.SkyDome;
import sjmhrp.sky.SkyRenderer;
import sjmhrp.terrain.Terrain;
import sjmhrp.terrain.TerrainShader;
import sjmhrp.textures.ModelTexture;
import sjmhrp.textures.TerrainTexture;
import sjmhrp.utils.MatrixUtils;
import sjmhrp.utils.Profiler;
import sjmhrp.view.Camera;
import sjmhrp.view.Frustum;
import sjmhrp.world.World;

public class RenderHandler {

	static Fbo gBuffer;
	
	public static void init(String title, int width, int height, boolean fullScreen) {
		try {
			Display.setTitle(title);
			Display.setResizable(false);
			Display.setDisplayMode(fullScreen?Display.getDesktopDisplayMode():new DisplayMode(width,height));
			Display.setFullscreen(fullScreen);
			Display.setVSyncEnabled(ConfigHandler.getBoolean("vsync"));
			Display.create(new PixelFormat().withDepthBits(24));
			enableCulling();
			glEnable(GL_DEPTH_TEST);
			glDepthFunc(GL_LEQUAL);
			glViewport(0,0,Display.getWidth(),Display.getHeight());
			Mouse.setGrabbed(true);
			if(GLContext.getCapabilities().GL_ARB_depth_clamp)glEnable(GL_DEPTH_CLAMP);
			gBuffer = new Fbo(Display.getWidth(),Display.getHeight());
			DebugRenderer.init();
			Frustum.init();
			ModelPool.init();
			Post.init();
			SSAORenderer.init();
			FlareRenderer.init();
			SkyRenderer.init();
		} catch(Exception e) {
			Log.printError(e);
		}
	}
	
	public static void enableCulling() {
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
	}

	public static void disableCulling() {
		glDisable(GL_CULL_FACE);
	}
	
	public static void renderWorld(World world, Camera camera, Shader shader) {
		if(world==null) {
			Display.sync(60);
			Display.update();
			return;
		}
		Profiler.drawCalls=0;
		if(!PhysicsEngine.paused) {
			gBuffer.bindFrameBuffer();
			clear();
			glDisable(GL_BLEND);
			renderEntities(shader.getEntityShader(),camera);
			if(world.getTerrain()!=null&&world.getTerrain().size()>0)renderTerrain(shader.getTerrainShader(),world.getTerrain(),camera);
			gBuffer.resolve(0,Post.albedo);
			gBuffer.resolve(1,Post.normal);
			doLighting(world,camera,shader);
			SSAORenderer.renderSSAO(shader,camera);
			Post.main.bindFrameBuffer();
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);
			gBuffer.resolveDepth(Post.main);
			Post.main.bindFrameBuffer();
			renderScene(shader.getGBufferShader());
			if(world.hasSky())SkyRenderer.renderSky(world.getSky(),camera,shader);
			if(ConfigHandler.getBoolean("debug"))DebugRenderer.render(shader,world,camera);
			Post.main.unbindFrameBuffer();
			FlareRenderer.renderFlares(shader,camera);
			Post.clear();
			if(ConfigHandler.getBoolean("fxaa"))Post.addToPipeline(shader.getFXAAShader());
			Post.addToPipeline(shader.getContrastShader());
		} else {
			Post.clear();
			shader.getTintShader().start();
			shader.getTintShader().loadTint(new Vector3d(),0.7);
			shader.getTintShader().stop();
			Post.addToPipeline(shader.getHBlurShader());
			Post.addToPipeline(shader.getVBlurShader());
			Post.addToPipeline(shader.getTintShader());
		}
		Post.display(Post.main);
		renderGUI(shader);
		Display.sync(60);
		Display.update();
	}
	
	public static void renderQuad(int... textures) {
		GL30.glBindVertexArray(ModelPool.getModel("quad").getVaoId());
		GL20.glEnableVertexAttribArray(0);
		for(int i = 0; i < textures.length; i++) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0+i);
			glBindTexture(GL_TEXTURE_2D,textures[i]);
		}
		glDrawArrays(GL_TRIANGLE_STRIP,0,4);
		Profiler.drawCalls++;
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}

	static void renderGUI(Shader s) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_DEPTH_TEST);
		s.getGUIShader().start();
		for(BasicGUIComponent g : RenderRegistry.getGUI()) {
			if(g.isActive())renderBasicGUIComponent(s.getGUIShader(),g);
		}
		s.getGUIShader().stop();
		renderGUIText(s.getFontShader());
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_BLEND);
	}
	
	static void renderBasicGUIComponent(GUIShader s, BasicGUIComponent g) {
		s.load(g);
		GL30.glBindVertexArray(g.getModel().getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D,g.getTexture());
		glDrawArrays(GL_TRIANGLE_STRIP,0,g.getModel().getVertexCount());
		Profiler.drawCalls++;
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
	}
	
	static void renderGUIText(FontShader s) {
		s.start();
		for(Entry<FontType,ArrayList<GUIText>> e : RenderRegistry.getText().entrySet()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D,e.getKey().getTextureAtlas());
			for(GUIText g : e.getValue()) {
				if(!g.isActive())continue;
				s.load(g);
				GL30.glBindVertexArray(g.getModel().getVaoId());
				GL20.glEnableVertexAttribArray(0);
				GL20.glEnableVertexAttribArray(1);
				glDrawArrays(GL_TRIANGLES,0,g.getVertexCount());
				Profiler.drawCalls++;
				GL20.glDisableVertexAttribArray(0);
				GL20.glDisableVertexAttribArray(1);
				GL30.glBindVertexArray(0);
			}
		}
		s.stop();
	}
	
	static void renderScene(GBufferShader s) {
		s.start();
		glDepthMask(false);
		glDisable(GL_DEPTH_TEST);
		renderQuad(Post.albedo.getColourTexture(),Post.light.getColourTexture(),SSAORenderer.SSAO2.getColourTexture(),Post.albedo.getDepthTexture());
		glEnable(GL_DEPTH_TEST);
		glDepthMask(true);
		s.stop();
	}
	
	static void doLighting(World w, Camera c, Shader s) {
		gBuffer.resolveDepth(Post.light);
		Post.light.bindFrameBuffer();
		glClear(GL_COLOR_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE,GL_ONE);
		glDepthMask(false);
		if(w.hasSky()&&w.getSky().getBodies().size()>0)renderSunLight(w.getSky(),s.getSunLightShader(),c);
		renderLights(s.getLightShader(),c);
		glDepthMask(true);
		glDisable(GL_BLEND);
		Post.light.unbindFrameBuffer();
	}

	static void renderSunLight(SkyDome sky, SunLightShader s, Camera c) {
		s.start();
		s.loadViewMatrix(c.getRotMatrix());
		for(CelestialBody b : sky.getBodies()) {
			s.load(b);
			renderQuad(Post.albedo.getColourTexture(),Post.normal.getColourTexture(),Post.albedo.getDepthTexture());
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

	static void renderTerrain(TerrainShader s, ArrayList<Terrain> terrain, Camera c) {
		s.start();
		for(Terrain te : terrain) {
			RawModel m = te.getModel();
			GL30.glBindVertexArray(m.getVaoId());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			TerrainTexture t = te.getTexture();
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D,t.getBackground().getAlbedoID());
			if(t.useBlend()) {
				GL13.glActiveTexture(GL13.GL_TEXTURE1);
				glBindTexture(GL_TEXTURE_2D,t.getRTexture().getAlbedoID());
				GL13.glActiveTexture(GL13.GL_TEXTURE2);
				glBindTexture(GL_TEXTURE_2D,t.getGTexture().getAlbedoID());
				GL13.glActiveTexture(GL13.GL_TEXTURE3);
				glBindTexture(GL_TEXTURE_2D,t.getBTexture().getAlbedoID());
				GL13.glActiveTexture(GL13.GL_TEXTURE4);
				glBindTexture(GL_TEXTURE_2D,t.getBlendMap().getAlbedoID());
			}
			s.loadUseBlend(t.useBlend());
			s.loadTransformMatrix(MatrixUtils.createTransform(new Vector3d(te.getX(),0,te.getZ())));
			s.loadViewMatrix(c.getViewMatrix());
			glDrawElements(GL_TRIANGLES, m.getVertexCount(), GL_UNSIGNED_INT, 0);
			Profiler.drawCalls++;
			unbind();
		} 
		s.stop();
	}

	static void renderEntities(EntityShader s, Camera c) {
		s.start();
		for(Entry<RawModel,HashMap<ModelTexture,ArrayList<Entity>>> e : RenderRegistry.getEntities().entrySet()) {
			RawModel m = e.getKey();
			GL30.glBindVertexArray(m.getVaoId());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			HashMap<ModelTexture,ArrayList<Entity>> map = e.getValue();
			for(Entry<ModelTexture,ArrayList<Entity>> e2 : map.entrySet()) {
				ModelTexture t = e2.getKey();
				if(t.getTransparency()) {
					disableCulling();
				} else {
					enableCulling();
				}
				s.loadRows(t.getNumberOfRows());
				s.loadFakeLighting(t.getFakeLighting());
				s.loadNormals(t.getNormalID()!=0);
				s.loadSpecular(t.getSpecularID()!=0);
				glDisable(GL_BLEND);
				GL13.glActiveTexture(GL13.GL_TEXTURE0);
				glBindTexture(GL_TEXTURE_2D, t.getAlbedoID());
				GL13.glActiveTexture(GL13.GL_TEXTURE1);
				glBindTexture(GL_TEXTURE_2D, t.getNormalID());
				GL13.glActiveTexture(GL13.GL_TEXTURE2);
				glBindTexture(GL_TEXTURE_2D, t.getSpecularID());
				ArrayList<Entity> es = e2.getValue();
				for(Entity e3 : es) {
					s.loadReflect(e3.getReflectivity());
					renderEntity(e3,s,c);
				}
			}
			unbind();
		}
		s.stop();
	}
	
	public static void renderEntity(Entity e, EntityShader shader, Camera c) {
		TexturedModel tm = e.getModel();
		RawModel m = tm.getRawModel();
		shader.loadOffset(e.getTextureXOffset(),e.getTextureYOffset());
		shader.loadTransformMatrix(e.getTransformMatrix());
		shader.loadViewMatrix(c.getViewMatrix());
		glDrawElements(GL_TRIANGLES, m.getVertexCount(), GL_UNSIGNED_INT, 0);
		Profiler.drawCalls++;
	}
	
	public static void unbind() {
		enableCulling();
		glDisable(GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(4);
		GL30.glBindVertexArray(0);
	}

	public static void clear() {
		glEnable(GL_DEPTH_TEST);
		glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);
	}

	public static void cleanUp() {
		gBuffer.cleanUp();
	}
}