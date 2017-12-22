package sjmhrp.render.shader;

import java.util.HashMap;
import java.util.Map.Entry;

import org.lwjgl.opengl.Display;

import sjmhrp.particle.ParticleShader;
import sjmhrp.render.debug.AABBShader;
import sjmhrp.render.entity.EntityShader;
import sjmhrp.render.flare.DownSampleShader;
import sjmhrp.render.flare.FeatureShader;
import sjmhrp.render.flare.FlareShader;
import sjmhrp.render.gui.GUIShader;
import sjmhrp.render.gui.text.FontShader;
import sjmhrp.render.light.LightShader;
import sjmhrp.render.light.SunLightShader;
import sjmhrp.render.post.EdgeHighlightShader;
import sjmhrp.render.post.GBufferShader;
import sjmhrp.render.post.HBlurShader;
import sjmhrp.render.post.SSAOShader;
import sjmhrp.render.post.TintShader;
import sjmhrp.render.post.VBlurShader;
import sjmhrp.render.view.Frustum;
import sjmhrp.utils.linear.Vector2d;
import sjmhrp.world.sky.CelestialShader;
import sjmhrp.world.sky.SkyShader;
import sjmhrp.world.sky.StarShader;
import sjmhrp.world.terrain.TerrainShader;

public class Shader {
	
	private static HashMap<String,ShaderProgram> shaders;

	public static void init() {
		shaders = new HashMap<String,ShaderProgram>();
		addShader("Entity",new EntityShader());
		addShader("Terrain",new TerrainShader());
		addShader("Sky",new SkyShader());
		addShader("Celestial",new CelestialShader());
		addShader("Star",new StarShader());
		addShader("GBuffer",new GBufferShader());
		addShader("Light",new LightShader());
		addShader("SunLight",new SunLightShader());
		addShader("Particle",new ParticleShader());
		addShader("Contrast",new PostShaderProgram("render/post/Generic","render/post/Contrast"));
		addShader("VBlur",new VBlurShader());
		addShader("HBlur",new HBlurShader());
		addShader("SSAO",new SSAOShader());
		addShader("SSAOBlur",new PostShaderProgram("render/post/Generic","render/post/SSAOBlur"));
		addShader("DownSample",new DownSampleShader());
		addShader("Flare",new FlareShader());
		addShader("Feature",new FeatureShader());
		addShader("Tint",new TintShader());
		addShader("FXAA",new PostShaderProgram("render/post/Generic","render/post/FXAA"){
			@Override
			public void getAllUniformLocations() {
				start();
				load2Vector(getUniformLocation("size"),new Vector2d(1d/Display.getWidth(),1d/Display.getHeight()));
				stop();
			}
		});
		addShader("EdgeHighlight",new EdgeHighlightShader());
		addShader("AABB",new AABBShader());
		addShader("GUI",new GUIShader());
		addShader("Font",new FontShader());
		initProjectionMatrices();
		connectTextures();
	}

	static void addShader(String name, ShaderProgram shader) {
		shaders.put(name,shader);
	}
	
	public static void initProjectionMatrices() {
		for(Entry<String,ShaderProgram> e : shaders.entrySet()) {
			ShaderProgram s = e.getValue();
			s.start();
			s.loadProjectionMatrix(Frustum.getProjectionMatrix());
			s.stop();
		}
	}

	public static void connectTextures() {
		for(Entry<String,ShaderProgram> e : shaders.entrySet()) {
			if(!(e.getValue() instanceof MultiTextureShader))continue;
			ShaderProgram s = e.getValue();
			s.start();
			((MultiTextureShader)s).connectTextures();
			s.stop();
		}
	}

	public static void cleanUp() {
		for(Entry<String,ShaderProgram> e : shaders.entrySet()) {
			e.getValue().cleanUp();
		}
	}

	public static ShaderProgram getShader(String name) {
		return shaders.get(name);
	}
	
	public static EntityShader getEntityShader() {
		return (EntityShader)getShader("Entity");
	}
	
	public static TerrainShader getTerrainShader() {
		return (TerrainShader)getShader("Terrain");
	}
	
	public static SkyShader getSkyShader() {
		return (SkyShader)getShader("Sky");
	}
	
	public static CelestialShader getCelestialShader() {
		return (CelestialShader)getShader("Celestial");
	}

	public static StarShader getStarShader() {
		return (StarShader)getShader("Star");
	}
	
	public static LightShader getLightShader() {
		return (LightShader)getShader("Light");
	}

	public static SunLightShader getSunLightShader() {
		return (SunLightShader)getShader("SunLight");
	}
	
	public static ParticleShader getParticleShader() {
		return (ParticleShader)getShader("Particle");
	}
	
	public static GBufferShader getGBufferShader() {
		return (GBufferShader)getShader("GBuffer");
	}

	public static PostShaderProgram getContrastShader() {
		return (PostShaderProgram)getShader("Contrast");
	}

	public static VBlurShader getVBlurShader() {
		return (VBlurShader)getShader("VBlur");
	}
	
	public static HBlurShader getHBlurShader() {
		return (HBlurShader)getShader("HBlur");
	}
	
	public static SSAOShader getSSAOShader() {
		return (SSAOShader)getShader("SSAO");
	}

	public static PostShaderProgram getSSAOBlurShader() {
		return (PostShaderProgram)getShader("SSAOBlur");
	}

	public static DownSampleShader getDownSampleShader() {
		return (DownSampleShader)getShader("DownSample");
	}
	
	public static FlareShader getFlareShader() {
		return (FlareShader)getShader("Flare");
	}
	
	public static FeatureShader getFeatureShader() {
		return (FeatureShader)getShader("Feature");
	}
	
	public static TintShader getTintShader() {
		return (TintShader)getShader("Tint");
	}
	
	public static PostShaderProgram getFXAAShader() {
		return (PostShaderProgram)getShader("FXAA");
	}
	
	public static EdgeHighlightShader getEdgeHighlightShader() {
		return (EdgeHighlightShader)getShader("EdgeHighlight");
	}
	
	public static AABBShader getAabbShader() {
		return (AABBShader)getShader("AABB");
	}
	
	public static GUIShader getGUIShader() {
		return (GUIShader)getShader("GUI");
	}
	
	public static FontShader getFontShader() {
		return (FontShader)getShader("Font");
	}
}