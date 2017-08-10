package sjmhrp.entity;

import java.io.Serializable;
import java.util.ArrayList;

import sjmhrp.linear.Vector3d;
import sjmhrp.models.ModelPool;
import sjmhrp.models.RawModel;
import sjmhrp.models.TexturedModel;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.physics.shapes.CompoundShape;
import sjmhrp.physics.shapes.ConvexShape;
import sjmhrp.textures.ModelTexture;
import sjmhrp.textures.TexturePool;
import sjmhrp.utils.ColourUtils;

public class EntityBuilder implements Serializable {

	private static final long serialVersionUID = -1949393937274450939L;

	public static ArrayList<EntityBuilder> entityBuilders = new ArrayList<EntityBuilder>();
	
	RigidBody body;
	String texture;
	String normalMap;
	String specularMap;
	String model;
	Vector3d colour = new Vector3d();
	boolean hasTexture = false;
	boolean hasNormals = false;
	boolean hasSpecular = false;
	boolean hasModel = false;
	
	public EntityBuilder(RigidBody body) {
		this.body=body;
		entityBuilders.add(this);
	}
	
	public EntityBuilder setTexture(String texture) {
		this.texture = texture;
		hasTexture=true;
		return this;
	}

	public EntityBuilder setNormalMap(String normalMap) {
		this.normalMap = normalMap;
		hasNormals=true;
		return this;
	}

	public EntityBuilder setSpecularMap(String specularMap) {
		this.specularMap = specularMap;
		hasSpecular=true;
		return this;
	}

	public EntityBuilder setModel(String model) {
		this.model = model;
		hasModel = true;
		return this;
	}
	
	public EntityBuilder setColour(Vector3d colour) {
		this.colour = colour;
		hasTexture=false;
		return this;
	}

	public EntityBuilder setColour(String colour) {
		return setColour(ColourUtils.getColour(colour));
	}
	
	public Entity build() {
		ModelTexture t = null;
		if(hasTexture) {
			if(hasNormals&&hasSpecular)t=TexturePool.getTexture(texture,normalMap,specularMap);
			if(hasNormals&&!hasSpecular)t=TexturePool.getTexture(texture,normalMap);
			if(!hasNormals)t=TexturePool.getTexture(texture);
		} else {
			t=TexturePool.getColour(colour);
		}
		RawModel m = ModelPool.getModel(hasModel?model:body.getCollisionShape().getName());
		return new Entity(new TexturedModel(m,t),body.getPosition(),body.getOrientation()).setSkew(body.getSkew());
	}

	public static ArrayList<Entity> createCompoundEntity(RigidBody b, String[] textures) {
		if(!(b.getCollisionShape() instanceof CompoundShape))return null;
		CompoundShape shape = (CompoundShape)b.getCollisionShape();
		ArrayList<Entity> es = new ArrayList<Entity>();
		int n = 0;
		for(ConvexShape s : shape.getShapes()) {
			es.add(new Entity(new TexturedModel(ModelPool.getModel(s.getName()),TexturePool.getTexture(textures[n++])),b.getPosition(),b.getOrientation()).setSkew(s.getSkewMatrix().mul(shape.getOffset(s).getMatrix())));
		}
		return es;
	}
	
	public static ArrayList<EntityBuilder> getEntityBuilders() {
		return entityBuilders;
	}
}