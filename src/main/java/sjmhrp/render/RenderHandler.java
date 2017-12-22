package sjmhrp.render;

import static org.lwjgl.opengl.ARBDepthClamp.GL_DEPTH_CLAMP;
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL11.glViewport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;

import sjmhrp.audio.AudioHandler;
import sjmhrp.event.KeyHandler;
import sjmhrp.io.ConfigHandler;
import sjmhrp.io.Log;
import sjmhrp.particle.ParticleRenderer;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.render.animation.AnimatedModel;
import sjmhrp.render.debug.DebugRenderer;
import sjmhrp.render.entity.Entity;
import sjmhrp.render.entity.EntityShader;
import sjmhrp.render.flare.FlareRenderer;
import sjmhrp.render.gui.GUIBox;
import sjmhrp.render.gui.GUIHandler;
import sjmhrp.render.gui.GUIShader;
import sjmhrp.render.gui.text.FontShader;
import sjmhrp.render.gui.text.FontType;
import sjmhrp.render.gui.text.GUIText;
import sjmhrp.render.light.LightRenderer;
import sjmhrp.render.models.ModelPool;
import sjmhrp.render.models.RawModel;
import sjmhrp.render.post.Fbo;
import sjmhrp.render.post.GBufferShader;
import sjmhrp.render.post.Post;
import sjmhrp.render.shader.Shader;
import sjmhrp.render.textures.ModelTexture;
import sjmhrp.render.textures.TerrainTexture;
import sjmhrp.render.view.Camera;
import sjmhrp.render.view.Frustum;
import sjmhrp.utils.Profiler;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.world.World;
import sjmhrp.world.sky.SkyRenderer;
import sjmhrp.world.terrain.ChunkTree.ChunkNode;
import sjmhrp.world.terrain.TerrainShader;

public class RenderHandler {

	public static Fbo gBuffer;
	
	static Thread render;
	static Camera camera;
	
	static ConcurrentHashMap<String,Runnable> uniqueTasks = new ConcurrentHashMap<String,Runnable>();
	static List<Runnable> tasks = Collections.synchronizedList(new ArrayList<Runnable>());
	static double timeStep;
	
	public static void start(String title, int width, int height, boolean fullScreen) {
		render = new Thread() {
			@Override
			public void run() {
				init(title,width,height,fullScreen);
				loop();
			}
		};
		render.setName("renderer");
		render.setDaemon(true);
		render.start();
	}
	
	static void init(String title, int width, int height, boolean fullScreen) {
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
			if(GLContext.getCapabilities().GL_ARB_depth_clamp)glEnable(GL_DEPTH_CLAMP);
			gBuffer = new Fbo(Display.getWidth(),Display.getHeight());
			DebugRenderer.init();
			Frustum.init();
			ModelPool.init();
			Post.init();
			ParticleRenderer.init();
			SSAORenderer.init();
			FlareRenderer.init();
			SkyRenderer.init();
			Shader.init();
			AudioHandler.init();
			KeyHandler.init();
			GUIHandler.createGUI();
		} catch(Exception e) {
			Log.printError(e);
		}
	}
	
	public static void setCamera(Camera camera) {
		RenderHandler.camera=camera;
	}
	
	public static void enableCulling() {
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
	}

	public static void disableCulling() {
		glDisable(GL_CULL_FACE);
	}
	
	public static void addUniqueTask(String name, Runnable task) {
		uniqueTasks.put(name,task);
	}
	
	public static void addTask(Runnable task) {
		tasks.add(task);
	}
	
	public static double getTimeStep() {
		return timeStep;
	}
	
	static void loop() {
		GUIHandler.switchToScreen("loading");
		long time = System.nanoTime();
		while(!Display.isCloseRequested()) {
			double dt = System.nanoTime()-time;
			time = System.nanoTime();
			timeStep=dt/1000000000;
			GUIHandler.tick(timeStep);
			if(camera!=null)RenderHandler.render(camera);
		}
		exit();
	}
	
	static void render(Camera camera) {
		List<Runnable> ts = new ArrayList<Runnable>(tasks);
		tasks.clear();
		ts.addAll(uniqueTasks.values());
		uniqueTasks.clear();
		ts.stream().filter(t->t!=null).forEach(Runnable::run);
		Profiler.drawCalls=0;
		if(!GUIHandler.isPaused()) {
			camera.tick();
			gBuffer.bindFrameBuffer();
			clear();
			glDisable(GL_BLEND);
			if(ConfigHandler.getBoolean("wireframe"))glPolygonMode(GL_FRONT_AND_BACK,GL_LINE);
			renderEntities(Shader.getEntityShader(),camera);
			PhysicsEngine.getWorlds().forEach(world->{if(world!=null&&world.hasTerrain()&&world.getTerrain().size()>0)renderTerrain(Shader.getTerrainShader(),world.getTerrain(),world.getTerrainTexture(),camera);});
			if(ConfigHandler.getBoolean("wireframe"))glPolygonMode(GL_FRONT_AND_BACK,GL_FILL);
			gBuffer.resolve(0,Post.albedo);
			gBuffer.resolve(1,Post.normal);
			gBuffer.resolve(2,Post.mask);
			if(ConfigHandler.getBoolean("ssao"))SSAORenderer.renderSSAO(camera);
			LightRenderer.renderLights(camera);
			if(ConfigHandler.getBoolean("bloom")) {
				Post.clear();
				Post.addToPipeline(Shader.getEdgeHighlightShader());
				Post.process(0,Post.bloom,true);
				Post.clear();
				Post.addToPipeline(Shader.getHBlurShader());
				Post.addToPipeline(Shader.getVBlurShader());
				Post.process(Post.bloom,Post.bloom);
			}
			Post.main.bindFrameBuffer();
			glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT|GL_STENCIL_BUFFER_BIT);
			gBuffer.resolveDepth(Post.main);
			Post.main.bindFrameBuffer();
			renderScene(Shader.getGBufferShader());
			PhysicsEngine.getWorlds().forEach(world->renderWorld(world,camera));
			renderDecals(Shader.getEntityShader(),camera);
			ParticleRenderer.renderParticles(camera,Shader.getParticleShader());
			Post.main.unbindFrameBuffer();
			if(ConfigHandler.getBoolean("lensflare"))FlareRenderer.renderFlares(camera);
			Post.clear();
			if(!ConfigHandler.getBoolean("bloom"))Post.addToPipeline(Shader.getEdgeHighlightShader());
			if(ConfigHandler.getBoolean("fxaa"))Post.addToPipeline(Shader.getFXAAShader());
			Post.addToPipeline(Shader.getContrastShader());
		} else {
			Post.clear();
			Shader.getTintShader().start();
			Shader.getTintShader().loadTint(new Vector3d(),0.7);
			Shader.getTintShader().stop();
			Post.addToPipeline(Shader.getHBlurShader(),1);
			Post.addToPipeline(Shader.getVBlurShader(),1);
			Post.addToPipeline(Shader.getTintShader());
		}
		Post.display(Post.main);
		renderGUI();
		Display.sync(60);
		Display.update();
	}
	
	static void renderWorld(World world, Camera camera) {
		if(world.hasSky())SkyRenderer.renderSky(world.getSky(),camera);
		if(ConfigHandler.getBoolean("debug"))DebugRenderer.render(world,camera);
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

	static void renderDecals(EntityShader s, Camera c) {
		s.start();
		s.loadViewMatrix(c.getViewMatrix());
		RawModel m = ModelPool.getModel("3quad");
		s.loadIsAnimated(false);
		GL30.glBindVertexArray(m.getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		for(Entry<ModelTexture,ArrayList<Entity>> e : RenderRegistry.getDecals().entrySet()) {
			ModelTexture t = e.getKey();
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
			for(Entity entity : e.getValue()) {
				s.loadReflect(entity.getReflectivity());
				renderEntity(entity,s,c);
			}
		}
		unbind();
		s.stop();
	}
	
	static void renderGUI() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_DEPTH_TEST);
		Shader.getGUIShader().start();
		for(GUIBox g : RenderRegistry.getGUI()) {
			if(g.isActive())renderBasicGUIComponent(Shader.getGUIShader(),g);
		}
		Shader.getGUIShader().stop();
		renderGUIText(Shader.getFontShader());
		glEnable(GL_DEPTH_TEST);
		glDisable(GL_BLEND);
	}
	
	static void renderBasicGUIComponent(GUIShader s, GUIBox g) {
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
		renderQuad(Post.albedo.getColourTexture(),Post.light.getColourTexture(),SSAORenderer.SSAO2.getColourTexture(),ConfigHandler.getBoolean("bloom")?Post.bloom.getColourTexture():0,Post.albedo.getDepthTexture());
		glEnable(GL_DEPTH_TEST);
		glDepthMask(true);
		s.stop();
	}
	
	public static void renderTerrain(TerrainShader s, ArrayList<ChunkNode> terrain, TerrainTexture tex, Camera c) {
		s.start();
		for(ChunkNode te : terrain) {
			if(!Frustum.isVisible(te.getBounds()))continue;
			renderTerrain(te,s,te.getModel(),tex,c);
			if(te.hasSeam())renderTerrain(te,s,te.getSeamModel(),tex,c);
		} 
		s.stop();
	}
	
	static void renderTerrain(ChunkNode te, TerrainShader s, RawModel m, TerrainTexture tex, Camera c) {
		if(m==null)return;
		GL30.glBindVertexArray(m.getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D,tex.getBackground().getAlbedoID());
		if(tex.useBlend()) {
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			glBindTexture(GL_TEXTURE_2D,tex.getRTexture().getAlbedoID());
			GL13.glActiveTexture(GL13.GL_TEXTURE2);
			glBindTexture(GL_TEXTURE_2D,tex.getGTexture().getAlbedoID());
			GL13.glActiveTexture(GL13.GL_TEXTURE3);
			glBindTexture(GL_TEXTURE_2D,tex.getBTexture().getAlbedoID());
			GL13.glActiveTexture(GL13.GL_TEXTURE4);
			glBindTexture(GL_TEXTURE_2D,tex.getBlendMap().getAlbedoID());
		}
		s.loadUseBlend(tex.useBlend());
		s.loadViewMatrix(c.getViewMatrix());
		glDrawElements(GL_TRIANGLES, m.getVertexCount(), GL_UNSIGNED_INT, 0);
		Profiler.drawCalls++;
		unbind();
	}
	
	static void renderEntities(EntityShader s, Camera c) {
		s.start();
		s.loadViewMatrix(c.getViewMatrix());
		for(Entry<RawModel,ConcurrentHashMap<ModelTexture,List<Entity>>> e : RenderRegistry.getEntities().entrySet()) {
			RawModel m = e.getKey();
			boolean animated = m instanceof AnimatedModel;
			if(animated) {
				s.loadJointTransforms(((AnimatedModel)m).getJointTransforms());
				s.loadIsAnimated(true);
			} else {
				s.loadIsAnimated(false);
			}
			GL30.glBindVertexArray(m.getVaoId());
			GL20.glEnableVertexAttribArray(0);
			GL20.glEnableVertexAttribArray(1);
			GL20.glEnableVertexAttribArray(2);
			if(animated) {
				GL20.glEnableVertexAttribArray(3);
				GL20.glEnableVertexAttribArray(4);
			}
			ConcurrentHashMap<ModelTexture,List<Entity>> map = e.getValue();
			for(Entry<ModelTexture,List<Entity>> e2 : map.entrySet()) {
				ModelTexture t = e2.getKey();
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
				List<Entity> es = e2.getValue();
				for(Entity e3 : es) {
					s.loadReflect(e3.getReflectivity());
					renderEntity(e3,s,c);
				}
			}
			unbind();
		}
		s.stop();
	}
	
	public static void renderEntity(Entity e, EntityShader s, Camera c) {
		RawModel m = e.getModel();
		if(e.getTexture().getTransparency()||e.isWireFrame()||ConfigHandler.getBoolean("wireframe")) {
			disableCulling();
		} else {
			enableCulling();
		}
		s.loadOffset(e.getTextureXOffset(),e.getTextureYOffset());
		s.loadTransformMatrix(e.getTransformMatrix());
		s.loadHighlight(e.isHighlighted());
		if(!ConfigHandler.getBoolean("wireframe")&&e.isWireFrame())glPolygonMode(GL_FRONT_AND_BACK,GL_LINE);
		glDrawElements(GL_TRIANGLES, m.getVertexCount(), GL_UNSIGNED_INT, 0);
		if(!ConfigHandler.getBoolean("wireframe")&&e.isWireFrame())glPolygonMode(GL_FRONT_AND_BACK,GL_FILL);
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

	public static void clearMain() {
		if(!isRenderer()) {
			addTask(RenderHandler::clearMain);
			return;
		}
		Post.main.bindFrameBuffer();
		clear();
		Post.main.unbindFrameBuffer();
	}
	
	public static void exit() {
		if(!isRenderer()) {
			addTask(RenderHandler::exit);
			return;
		}
		Loader.cleanUp();
		Shader.cleanUp();
		RenderHandler.cleanUp();
		Post.cleanUp();
		SSAORenderer.cleanUp();
		FlareRenderer.cleanUp();
		AudioHandler.cleanUp();
		System.exit(0);
	}
	
	public static boolean isRenderer() {
		return Thread.currentThread().getName().equals("renderer");
	}
	
	public static boolean isRendering() {
		return render.isAlive();
	}
	
	static void cleanUp() {
		gBuffer.cleanUp();
	}
}