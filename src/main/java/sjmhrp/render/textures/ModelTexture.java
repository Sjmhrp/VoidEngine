package sjmhrp.render.textures;

import java.io.Serializable;

public class ModelTexture implements Serializable {

	private static final long serialVersionUID = -282143818561756582L;
	
	private String name;
	private int albedoID;
	private int normalID;
	private int specularID;
	
	private boolean transparent = false;
	private boolean useFakeLighting = false;
	
	private int numberOfRows = 1;
	
	public ModelTexture(String name) {
		this(0,name);
	}
	
	public ModelTexture(int id, String name) {
		albedoID = id;
		normalID = 0;
		specularID = 0;
		this.name = name;
	}

	public boolean hasNormalMap() {
		return normalID!=0;
	}
	
	public boolean hasSpecularMap() {
		return specularID!=0;
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
	
	public ModelTexture setFakeLighting(boolean b) {
		useFakeLighting = b;
		return this;
	}
	
	public boolean getFakeLighting() {
		return useFakeLighting;
	}

	public String getName() {
		return name;
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
