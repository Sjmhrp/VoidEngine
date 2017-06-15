package sjmhrp.textures;

public class ModelTexture {

	private int albedoID;
	private int normalID;
	private int specularID;
	
	private boolean transparent = false;
	private boolean useFakeLighting = false;
	
	private int numberOfRows = 1;
	
	public ModelTexture(int id) {
		albedoID = id;
		normalID = 0;
		specularID = 0;
	}

	public void loadNormalMap(int id) {
		normalID = id;
	}

	public void loadSpecularMap(int id) {
		specularID = id;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public void setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
	}

	public ModelTexture setTransparent() {
		transparent = true;
		return this;
	}
	
	public boolean getTransparency() {
		return transparent;
	}
	
	public ModelTexture setFakeLighting() {
		useFakeLighting = true;
		return this;
	}
	
	public boolean getFakeLighting() {
		return useFakeLighting;
	}

	public int getAlbedoID() {
		return albedoID;
	}

	public int getNormalID() {
		return normalID;
	}

	public int getSpecularID() {
		return specularID;
	}
}
