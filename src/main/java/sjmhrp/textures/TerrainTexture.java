package sjmhrp.textures;

import java.io.Serializable;

public class TerrainTexture implements Serializable{
	
	private static final long serialVersionUID = 703561087132300468L;
	
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
	
	public void reload() {
		background = TexturePool.getTexture(background.getName());
		if(rTexture!=null)rTexture = TexturePool.getTexture(rTexture.getName());
		if(gTexture!=null)gTexture = TexturePool.getTexture(gTexture.getName());
		if(bTexture!=null)bTexture = TexturePool.getTexture(bTexture.getName());
		if(blendMap!=null)blendMap = TexturePool.getTexture(blendMap.getName());
	}
}