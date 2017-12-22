package sjmhrp.render.textures;

import java.io.Serializable;

import sjmhrp.render.RenderHandler;

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
		this(new ModelTexture(background));
		RenderHandler.addTask(this::reload);
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
		this(new ModelTexture(background),new ModelTexture(rTexture),new ModelTexture(gTexture),new ModelTexture(bTexture),new ModelTexture(blendMap));
		RenderHandler.addTask(this::reload);
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
		if(!RenderHandler.isRenderer()) {
			RenderHandler.addTask(this::reload);
			return;
		}
		background = TexturePool.getTexture(background.getName());
		if(rTexture!=null)rTexture = TexturePool.getTexture(rTexture.getName());
		if(gTexture!=null)gTexture = TexturePool.getTexture(gTexture.getName());
		if(bTexture!=null)bTexture = TexturePool.getTexture(bTexture.getName());
		if(blendMap!=null)blendMap = TexturePool.getTexture(blendMap.getName());
	}
}