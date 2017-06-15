package sjmhrp.textures;

public class TerrainTexture {

	ModelTexture background;
	ModelTexture rTexture;
	ModelTexture gTexture;
	ModelTexture bTexture;
	ModelTexture blendMap;
	
	boolean blend;
	
	public TerrainTexture(ModelTexture background) {
		this.background=background;
		blend = false;
	}
	
	public TerrainTexture(String background) {
		this(TexturePool.getTexture(background));
	}
	
	public TerrainTexture(ModelTexture background, ModelTexture rTexture, ModelTexture gTexture, ModelTexture bTexture, ModelTexture blendMap) {
		this.background=background;
		this.rTexture=rTexture;
		this.gTexture=gTexture;
		this.bTexture=bTexture;
		this.blendMap=blendMap;
		blend = true;
	}

	public TerrainTexture(String background, String rTexture, String gTexture, String bTexture, String blendMap) {
		this(TexturePool.getTexture(background),TexturePool.getTexture(rTexture),TexturePool.getTexture(gTexture),TexturePool.getTexture(bTexture),TexturePool.getTexture(blendMap));
	}
	
	public ModelTexture getBackground() {
		return background;
	}

	public ModelTexture getRTexture() {
		return rTexture;
	}

	public ModelTexture getGTexture() {
		return gTexture;
	}

	public ModelTexture getBTexture() {
		return bTexture;
	}

	public ModelTexture getBlendMap() {
		return blendMap;
	}
	
	public boolean useBlend() {
		return blend;
	}
}