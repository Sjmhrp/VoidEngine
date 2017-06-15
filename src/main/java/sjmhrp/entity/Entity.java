package sjmhrp.entity;

import sjmhrp.linear.Matrix4d;
import sjmhrp.linear.Quaternion;
import sjmhrp.linear.Vector3d;
import sjmhrp.models.TexturedModel;
import sjmhrp.render.Loader;
import sjmhrp.render.RenderRegistry;
import sjmhrp.utils.MatrixUtils;

public class Entity {

	protected TexturedModel model;
	protected Vector3d position;
	protected Quaternion rotation;
	protected Matrix4d skewMatrix;

	private int textureIndex = 0;
	private double reflectivity = 0;

	public Entity(TexturedModel model, Vector3d position, Quaternion rotation, boolean b) {
		this.model = model;
		this.position = position;
		this.rotation = rotation;
		skewMatrix = new Matrix4d();
		skewMatrix.setIdentity();
		if(b)RenderRegistry.registerEntity(this);
	}

	public Entity(TexturedModel model, Vector3d position, Quaternion rotation) {
		this(model,position,rotation,true);
	}

	public Entity(TexturedModel model, Vector3d position) {
		this(model,position,new Quaternion());
	}
	
	public Entity(TexturedModel model, Vector3d position, Quaternion rotation, int index) {
		this(model,position,rotation);
		textureIndex = index;
	}

	public Entity loadNormalMap(String map) {
		if(!model.hasNormalMap())model.loadNormalMap(Loader.loadTexture("map/"+map));
		return this;
	}

	public Entity loadSpecularMap(String map) {
		if(!model.hasSpecularMap())model.loadSpecularMap(Loader.loadTexture("map/"+map));
		return this;
	}

	public Matrix4d getSkewMatrix() {
		return skewMatrix;
	}

	public Entity setSkew(Matrix4d skew) {
		skewMatrix = skew;
		return this;
	}

	public Entity scale(double d) {
		skewMatrix.scale(d);
		return this;
	}

	public TexturedModel getModel() {
		return model;
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	public Vector3d getPosition() {
		return position;
	}

	public Quaternion getRotation() {
		return rotation;
	}

	public void setPosition(Vector3d position) {
		this.position = position;
	}

	public Matrix4d getTransformMatrix() {
		return Matrix4d.mul(MatrixUtils.createTransform(position,rotation),skewMatrix);
	}

	public double getTextureXOffset() {
		int col = textureIndex%model.getTexture().getNumberOfRows();
		return (double)col/model.getTexture().getNumberOfRows();
	}

	public double getTextureYOffset() {
		int row = textureIndex/model.getTexture().getNumberOfRows();
		return (double)row/model.getTexture().getNumberOfRows();
	}

	public double getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(double reflectivity) {
		this.reflectivity = reflectivity;
	}
}