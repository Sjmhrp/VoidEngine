package sjmhrp.shaders;

import sjmhrp.debug.AABBShader;
import sjmhrp.entity.EntityShader;
import sjmhrp.light.LightShader;
import sjmhrp.light.SunLightShader;
import sjmhrp.post.GBufferShader;
import sjmhrp.post.HBlurShader;
import sjmhrp.post.SSAOShader;
import sjmhrp.post.VBlurShader;
import sjmhrp.sky.SkyShader;
import sjmhrp.sky.SunShader;
import sjmhrp.terrain.TerrainShader;
import sjmhrp.view.Frustum;

public class Shader {
	
	private EntityShader entityShader;
	private TerrainShader terrainShader;
	private SunShader sunShader;
	private SkyShader skyShader;
	private LightShader lightShader;
	private SunLightShader sunLightShader;
	private GBufferShader gBufferShader;
	private PostShaderProgram contrastShader;
	private VBlurShader vBlurShader;
	private HBlurShader hBlurShader;
	private SSAOShader ssaoShader;
	private PostShaderProgram ssaoBlurShader;

	private AABBShader aabbShader;

	public Shader() {
		entityShader = new EntityShader();
		terrainShader = new TerrainShader();
		sunShader = new SunShader();
		skyShader = new SkyShader();
		gBufferShader = new GBufferShader();
		lightShader = new LightShader();
		sunLightShader = new SunLightShader();
		contrastShader = new PostShaderProgram("Generic","Contrast");
		vBlurShader = new VBlurShader();
		hBlurShader = new HBlurShader();
		ssaoShader = new SSAOShader();
		ssaoBlurShader = new PostShaderProgram("Generic","SSAOBlur");
		aabbShader = new AABBShader();
		initProjectionMatrices();
		connectTextures();
	}

	public void initProjectionMatrices() {
		initProjectionMatrix(entityShader);
		initProjectionMatrix(terrainShader);
		initProjectionMatrix(sunShader);
		initProjectionMatrix(skyShader);
		initProjectionMatrix(lightShader);
		initProjectionMatrix(sunLightShader);
		initProjectionMatrix(ssaoShader);
		initProjectionMatrix(aabbShader);
	}

	public void connectTextures() {
		entityShader.start();
		entityShader.connectTextures();
		entityShader.stop();
		terrainShader.start();
		terrainShader.connectTextures();
		terrainShader.stop();
		lightShader.start();
		lightShader.connectTextures();
		lightShader.stop();
		sunLightShader.start();
		sunLightShader.connectTextures();
		sunLightShader.stop();
		gBufferShader.start();
		gBufferShader.connectTextures();
		gBufferShader.stop();
		ssaoShader.start();
		ssaoShader.connectTextures();
		ssaoShader.stop();
	}

	static void initProjectionMatrix(ShaderProgram shader) {
		shader.start();
		shader.loadProjectionMatrix(Frustum.getProjectionMatrix());
		shader.stop();
	}

	public void cleanUp() {
		entityShader.cleanUp();
		terrainShader.cleanUp();
		sunShader.cleanUp();
		skyShader.cleanUp();
		lightShader.cleanUp();
		sunLightShader.cleanUp();
		gBufferShader.cleanUp();
		contrastShader.cleanUp();
		vBlurShader.cleanUp();
		hBlurShader.cleanUp();
		ssaoShader.cleanUp();
		ssaoBlurShader.cleanUp();
		aabbShader.cleanUp();
	}

	public EntityShader getEntityShader() {
		return entityShader;
	}

	public TerrainShader getTerrainShader() {
		return terrainShader;
	}

	public SunShader getSunShader() {
		return sunShader;
	}
	
	public SkyShader getSkyShader() {
		return skyShader;
	}
	
	public LightShader getLightShader() {
		return lightShader;
	}

	public SunLightShader getSunLightShader() {
		return sunLightShader;
	}
	
	public GBufferShader getGBufferShader() {
		return gBufferShader;
	}

	public PostShaderProgram getContrastShader() {
		return contrastShader;
	}

	public VBlurShader getVBlurShader() {
		return vBlurShader;
	}
	
	public HBlurShader getHBlurShader() {
		return hBlurShader;
	}
	
	public SSAOShader getSSAOShader() {
		return ssaoShader;
	}

	public PostShaderProgram getSSAOBlurShader() {
		return ssaoBlurShader;
	}

	public AABBShader getAabbShader() {
		return aabbShader;
	}
}