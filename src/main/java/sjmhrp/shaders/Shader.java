package sjmhrp.shaders;

import java.util.HashMap;
import java.util.Map.Entry;

import org.lwjgl.opengl.Display;

import sjmhrp.debug.AABBShader;
import sjmhrp.entity.EntityShader;
import sjmhrp.flare.DownSampleShader;
import sjmhrp.flare.FeatureShader;
import sjmhrp.flare.FlareShader;
import sjmhrp.gui.GUIShader;
import sjmhrp.gui.text.FontShader;
import sjmhrp.light.LightShader;
import sjmhrp.light.SunLightShader;
import sjmhrp.linear.Vector2d;
import sjmhrp.post.GBufferShader;
import sjmhrp.post.HBlurShader;
import sjmhrp.post.SSAOShader;
import sjmhrp.post.TintShader;
import sjmhrp.post.VBlurShader;
import sjmhrp.sky.CelestialShader;
import sjmhrp.sky.SkyShader;
import sjmhrp.sky.StarShader;
import sjmhrp.terrain.TerrainShader;
import sjmhrp.view.Frustum;

public class Shader {
	
	private HashMap<String,ShaderProgram> shaders;

	public Shader() {
		shaders = new HashMap<String,ShaderProgram>();
		addShader("Entity",new EntityShader());
		addShader("Terrain",new TerrainShader());
		addShader("Sky",new SkyShader());
		addShader("Celestial",new CelestialShader());
		addShader("Star",new StarShader());
		addShader("GBuffer",new GBufferShader());
		addShader("Light",new LightShader());
		addShader("SunLight",new SunLightShader());
		addShader("Contrast",new PostShaderProgram("post/Generic","post/Contrast"));
		addShader("VBlur",new VBlurShader());
		addShader("HBlur",new HBlurShader());
		addShader("SSAO",new SSAOShader());
		addShader("SSAOBlur",new PostShaderProgram("post/Generic","post/SSAOBlur"));
		addShader("DownSample",new DownSampleShader());
		addShader("Flare",new FlareShader());
		addShader("Feature",new FeatureShader());
		addShader("Tint",new TintShader());
		addShader("FXAA",new PostShaderProgram("post/Generic","post/FXAA"){
			@Override
			public void getAllUniformLocations() {
				start();
				load2Vector(getUniformLocation("size"),new Vector2d(1d/Display.getWidth(),1d/Display.getHeight()));
				stop();
			}
		});
		addShader("AABB",new AABBShader());
		addShader("GUI",new GUIShader());
		addShader("Font",new FontShader());
		initProjectionMatrices();
		connectTextures();
	}

	void addShader(String name, ShaderProgram shader) {
		shaders.put(name,shader);
	}
	
	public void initProjectionMatrices() {
		for(Entry<String,ShaderProgram> e : shaders.entrySet()) {
			ShaderProgram s = e.getValue();
			s.start();
			s.loadProjectionMatrix(Frustum.getProjectionMatrix());
			s.stop();
		}
	}

	public void connectTextures() {
		for(Entry<String,ShaderProgram> e : shaders.entrySet()) {
			if(!(e.getValue() instanceof MultiTextureShaderProgram))continue;
			ShaderProgram s = e.getValue();
			s.start();
			((MultiTextureShaderProgram)s).connectTextures();
			s.stop();
		}
	}

	public void cleanUp() {
		for(Entry<String,ShaderProgram> e : shaders.entrySet()) {
			e.getValue().cleanUp();
		}
	}

	public ShaderProgram getShader(String name) {
		return shaders.get(name);
	}
	
	public EntityShader getEntityShader() {
		return (EntityShader)getShader("Entity");
	}

	public TerrainShader getTerrainShader() {
		return (TerrainShader)getShader("Terrain");
	}
	
	public SkyShader getSkyShader() {
		return (SkyShader)getShader("Sky");
	}
	
	public CelestialShader getCelestialShader() {
		return (CelestialShader)getShader("Celestial");
	}

	public StarShader getStarShader() {
		return (StarShader)getShader("Star");
	}
	
	public LightShader getLightShader() {
		return (LightShader)getShader("Light");
	}

	public SunLightShader getSunLightShader() {
		return (SunLightShader)getShader("SunLight");
	}
	
	public GBufferShader getGBufferShader() {
		return (GBufferShader)getShader("GBuffer");
	}

	public PostShaderProgram getContrastShader() {
		return (PostShaderProgram)getShader("Contrast");
	}

	public VBlurShader getVBlurShader() {
		return (VBlurShader)getShader("VBlur");
	}
	
	public HBlurShader getHBlurShader() {
		return (HBlurShader)getShader("HBlur");
	}
	
	public SSAOShader getSSAOShader() {
		return (SSAOShader)getShader("SSAO");
	}

	public PostShaderProgram getSSAOBlurShader() {
		return (PostShaderProgram)getShader("SSAOBlur");
	}

	public DownSampleShader getDownSampleShader() {
		return (DownSampleShader)getShader("DownSample");
	}
	
	public FlareShader getFlareShader() {
		return (FlareShader)getShader("Flare");
	}
	
	public FeatureShader getFeatureShader() {
		return (FeatureShader)getShader("Feature");
	}
	
	public TintShader getTintShader() {
		return (TintShader)getShader("Tint");
	}
	
	public PostShaderProgram getFXAAShader() {
		return (PostShaderProgram)getShader("FXAA");
	}
	
	public AABBShader getAabbShader() {
		return (AABBShader)getShader("AABB");
	}
	
	public GUIShader getGUIShader() {
		return (GUIShader)getShader("GUI");
	}
	
	public FontShader getFontShader() {
		return (FontShader)getShader("Font");
	}
}