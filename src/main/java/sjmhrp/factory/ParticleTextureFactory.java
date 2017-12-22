package sjmhrp.factory;

import sjmhrp.render.textures.ParticleTexture;
import sjmhrp.render.textures.TexturePool;
import sjmhrp.utils.ColourUtils;
import sjmhrp.utils.linear.Vector3d;

public class ParticleTextureFactory extends Factory<ParticleTexture> {
	
	private static final long serialVersionUID = 1678529990545096172L;
	
	String texture;
	Vector3d colour = new Vector3d();
	int rows = 1;
	boolean hasTexture = false;
	
	public ParticleTextureFactory setRows(int rows) {
		if(rows!=0)this.rows=rows;
		return this;
	}
	
	public ParticleTextureFactory setTexture(String texture) {
		this.texture=texture;
		hasTexture=true;
		return this;
	}
	
	public ParticleTextureFactory setColour(String colour) {
		return setColour(ColourUtils.getColour(colour));
	}

	public ParticleTextureFactory setColour(Vector3d colour) {
		this.colour=colour;
		hasTexture=false;
		return this;
	}
	
	@Override
	public ParticleTexture build() {
		ParticleTexture t = null;
		if(hasTexture) {
			t=TexturePool.getParticleTexture(texture,rows);
		} else {
			t=new ParticleTexture(TexturePool.getColour(colour).getAlbedoID(),rows);
		}
		return t;
	}
	
	@Override
	protected boolean store() {return false;}
}