package sjmhrp.models;

import sjmhrp.textures.ModelTexture;

public class TexturedModel {

	private RawModel rawModel;
	private ModelTexture texture;

	public TexturedModel(RawModel m, ModelTexture t) {
		rawModel = m;
		texture = t;
	}
	
	public RawModel getRawModel() {
		return rawModel;
	}

	public ModelTexture getTexture() {
		return texture;
	}

	public void loadNormalMap(int id) {
		texture.loadNormalMap(id);
	}

	public void loadSpecularMap(int id) {
		texture.loadSpecularMap(id);
	}

	public boolean hasNormalMap() {
		return texture.getNormalID()!=0;
	}

	public boolean hasSpecularMap() {
		return texture.getSpecularID()!=0;
	}

	public int getAlbedoMap() {
		return texture.getAlbedoID();
	}

	public int getNormalMap() {
		return texture.getNormalID();
	}

	public int getSpecularMap() {
		return texture.getSpecularID();
	}
}