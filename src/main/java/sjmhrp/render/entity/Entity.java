package sjmhrp.render.entity;

import java.util.Iterator;

import sjmhrp.event.TickListener;
import sjmhrp.particle.Trail;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.render.Loader;
import sjmhrp.render.RenderHandler;
import sjmhrp.render.RenderRegistry;
import sjmhrp.render.models.RawModel;
import sjmhrp.render.textures.ModelTexture;
import sjmhrp.utils.MatrixUtils;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Quaternion;
import sjmhrp.utils.linear.Vector3d;

public class Entity {

	protected RawModel model;
	protected ModelTexture texture;
	protected Vector3d position;
	protected Quaternion rotation;
	protected Matrix4d skewMatrix;

	private int textureIndex = 0;
	private double reflectivity = 0;
	private boolean isWireFrame = false;
	public boolean highlight = false;
	
	private transient Matrix4d transformMatrix = new Matrix4d().setIdentity();

	public Entity(RawModel model, ModelTexture texture, Vector3d position, Quaternion rotation, boolean entity, boolean decal) {
		this.model = model;
		this.texture=texture;
		this.position = position;
		this.rotation = rotation;
		skewMatrix = new Matrix4d().setIdentity();
		if(entity)RenderRegistry.registerEntity(this);
		if(decal)RenderRegistry.registerDecal(this);
	}

	public Entity(RawModel model, ModelTexture texture, Vector3d position, Quaternion rotation) {
		this(model,texture,position,rotation,true,false);
	}

	public Entity(RawModel model,ModelTexture texture,  Vector3d position) {
		this(model,texture,position,new Quaternion());
	}
	
	public Entity(RawModel model, ModelTexture texture, Vector3d position, Quaternion rotation, int index) {
		this(model,texture,position,rotation);
		textureIndex = index;
	}

	public void remove() {
		RenderRegistry.removeEntity(this);
	}
	
	public Entity loadNormalMap(String map) {
		if(!texture.hasNormalMap())texture.loadNormalMap(Loader.loadTexture("map/"+map));
		return this;
	}

	public Entity loadSpecularMap(String map) {
		if(!texture.hasSpecularMap())texture.loadSpecularMap(Loader.loadTexture("map/"+map));
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

	public Entity addTrail(double length, Vector3d colour, Vector3d offset) {
		if(!RenderHandler.isRenderer()) {
			RenderHandler.addTask(()->addTrail(length,colour,offset));
			return this;
		}
		new Trail(position,length,colour,offset);
		return this;
	}
	
	public Entity removeTrail() {
		Iterator<TickListener> i = PhysicsEngine.getListeners().iterator();
		while(i.hasNext()) {
			TickListener t = i.next();
			if(t instanceof Trail&&((Trail)t).getCentre()==position)i.remove();
		}
		return this;
	}
	
	public RawModel getModel() {
		return model;
	}

	public ModelTexture getTexture() {
		return texture;
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

	public void updateTransformMatrix() {
		transformMatrix.set(Matrix4d.mul(MatrixUtils.createTransform(position,rotation),skewMatrix));
	}
	
	public Matrix4d getTransformMatrix() {
		return transformMatrix;
	}

	public double getTextureXOffset() {
		int col = textureIndex%texture.getNumberOfRows();
		return (double)col/texture.getNumberOfRows();
	}

	public double getTextureYOffset() {
		int row = textureIndex/texture.getNumberOfRows();
		return (double)row/texture.getNumberOfRows();
	}

	public double getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(double reflectivity) {
		this.reflectivity = reflectivity;
	}
	
	public void setWireFrame(boolean b) {
		isWireFrame=b;
	}
	
	public boolean isWireFrame() {
		return isWireFrame;
	}
	
	public void setHighlight(boolean b) {
		highlight=b;
	}
	
	public boolean isHighlighted() {
		return highlight;
	}
}