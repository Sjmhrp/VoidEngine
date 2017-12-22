package sjmhrp.factory;

import sjmhrp.render.textures.ModelTexture;
import sjmhrp.render.textures.TexturePool;
import sjmhrp.utils.ColourUtils;
import sjmhrp.utils.linear.Vector3d;

public class TextureFactory extends Factory<ModelTexture> {
	
	private static final long serialVersionUID = -7297854174481750375L;
	
	String texture;
	String normalMap;
	String specularMap;
	Vector3d colour = new Vector3d();
	boolean hasTexture = false;
	boolean hasNormal = false;
	boolean hasSpecular = false;
	
	public TextureFactory setTexture(String texture) {
		this.texture = texture;
		hasTexture=true;
		return this;
	}

	public TextureFactory setNormalMap(String normalMap) {
		this.normalMap = normalMap;
		hasNormal=true;
		return this;
	}

	public TextureFactory setSpecularMap(String specularMap) {
		this.specularMap = specularMap;
		hasSpecular=true;
		return this;
	}

	public TextureFactory setColour(Vector3d colour) {
		this.colour = colour;
		hasTexture=false;
		return this;
	}

	public TextureFactory setColour(String colour) {
		return setColour(ColourUtils.getColour(colour));
	}
	
	@Override
	public ModelTexture build() {
		ModelTexture t = null;
		if(hasTexture) {
			if(hasNormal&&hasSpecular)t=TexturePool.getTexture(texture,normalMap,specularMap);
			if(hasNormal&&!hasSpecular)t=TexturePool.getTexture(texture,normalMap);
			if(!hasNormal)t=TexturePool.getTexture(texture);
		} else {
			t=TexturePool.getColour(colour);
		}
		return t;
	}
	
	@Override
	protected boolean store() {return false;}
}