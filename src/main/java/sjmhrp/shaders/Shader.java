package sjmhrp.shaders;

import java.util.HashMap;
import java.util.Map.Entry;

import sjmhrp.debug.AABBShader;
import sjmhrp.entity.EntityShader;
import sjmhrp.flare.DownSampleShader;
import sjmhrp.flare.FeatureShader;
import sjmhrp.flare.FlareShader;
import sjmhrp.light.LightShader;
import sjmhrp.light.SunLightShader;
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
		addShader("EntityShader",new EntityShader());
		addShader("TerrainShader",new TerrainShader());
		addShader("SkyShader",new SkyShader());
		addShader("CelestialShader",new CelestialShader());
		addShader("StarShader",new StarShader());
		addShader("GBufferShader",new GBufferShader());
		addShader("LightShader",new LightShader());
		addShader("SunLightShader",new SunLightShader());
		addShader("ContrastShader",new PostShaderProgram("post/Generic","post/Contrast"));
		addShader("VBlurShader",new VBlurShader());
		addShader("HBlurShader",new HBlurShader());
		addShader("SSAOShader",new SSAOShader());
		addShader("SSAOBlurShader",new PostShaderProgram("post/Generic","post/SSAOBlur"));
		addShader("DownSampleShader",new DownSampleShader());
		addShader("FlareShader",new FlareShader());
		addShader("FeatureShader",new FeatureShader());
		addShader("TintShader",new TintShader());
		addShader("AABBShader",new AABBShader());
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
			if(e.getValue() instanceof MultiTextureShaderProgram) {
				ShaderProgram s = e.getValue();
				s.start();
				((MultiTextureShaderProgram)s).connectTextures();
				s.stop();
			}
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
		return (EntityShader)getShader("EntityShader");
	}

	public TerrainShader getTerrainShader() {
		return (TerrainShader)getShader("TerrainShader");
	}
	
	public SkyShader getSkyShader() {
		return (SkyShader)getShader("SkyShader");
	}
	
	public CelestialShader getCelestialShader() {
		return (CelestialShader)getShader("CelestialShader");
	}

	public StarShader getStarShader() {
		return (StarShader)getShader("StarShader");
	}
	
	public LightShader getLightShader() {
		return (LightShader)getShader("LightShader");
	}

	public SunLightShader getSunLightShader() {
		return (SunLightShader)getShader("SunLightShader");
	}
	
	public GBufferShader getGBufferShader() {
		return (GBufferShader)getShader("GBufferShader");
	}

	public PostShaderProgram getContrastShader() {
		return (PostShaderProgram)getShader("ContrastShader");
	}

	public VBlurShader getVBlurShader() {
		return (VBlurShader)getShader("VBlurShader");
	}
	
	public HBlurShader getHBlurShader() {
		return (HBlurShader)getShader("HBlurShader");
	}
	
	public SSAOShader getSSAOShader() {
		return (SSAOShader)getShader("SSAOShader");
	}

	public PostShaderProgram getSSAOBlurShader() {
		return (PostShaderProgram)getShader("SSAOBlurShader");
	}

	public DownSampleShader getDownSampleShader() {
		return (DownSampleShader)getShader("DownSampleShader");
	}
	
	public FlareShader getFlareShader() {
		return (FlareShader)getShader("FlareShader");
	}
	
	public FeatureShader getFeatureShader() {
		return (FeatureShader)getShader("FeatureShader");
	}
	
	public TintShader getTintShader() {
		return (TintShader)getShader("TintShader");
	}
	
	public AABBShader getAabbShader() {
		return (AABBShader)getShader("AABBShader");
	}
}