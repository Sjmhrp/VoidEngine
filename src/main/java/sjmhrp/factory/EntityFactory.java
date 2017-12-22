package sjmhrp.factory;

import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.render.entity.Entity;
import sjmhrp.render.models.ModelPool;
import sjmhrp.render.models.RawModel;
import sjmhrp.utils.linear.Vector3d;

public class EntityFactory extends Factory<Entity> {

	private static final long serialVersionUID = -1350038921685117186L;
	
	RigidBody body;
	TextureFactory texture;
	String model;
	boolean hasModel = false;
	boolean hasTrail = false;
	double trailLength = 0;
	Vector3d trailColour = new Vector3d();
	Vector3d trailOffset = new Vector3d();
	
	public EntityFactory(RigidBody body) {
		this.body=body;
		texture=new TextureFactory();
	}
	
	public EntityFactory setTexture(String texture) {
		this.texture.setTexture(texture);
		return this;
	}

	public EntityFactory setNormalMap(String normalMap) {
		texture.setNormalMap(normalMap);
		return this;
	}

	public EntityFactory setSpecularMap(String specularMap) {
		texture.setSpecularMap(specularMap);
		return this;
	}

	public EntityFactory setColour(Vector3d colour) {
		texture.setColour(colour);
		return this;
	}

	public EntityFactory setColour(String colour) {
		texture.setColour(colour);
		return this;
	}

	public EntityFactory setModel(String model) {
		this.model = model;
		hasModel = true;
		return this;
	}

	public EntityFactory addTrail(double length, Vector3d colour) {
		return addTrail(length,colour, new Vector3d());
	}
	
	public EntityFactory addTrail(double length, Vector3d colour, Vector3d offset) {
		hasTrail=true;
		trailLength=length;
		trailColour=colour;
		trailOffset=offset;
		return this;
	}
	
	@Override
	public Entity build() {
		RawModel m = ModelPool.getModel(hasModel?model:body.getCollisionShape().getName());
		Entity e = new Entity(m,texture.build(),body.getPosition(),body.getOrientation()).setSkew(body.getSkew());
		if(hasTrail)e.addTrail(trailLength,trailColour,trailOffset);
		return e;
	}
}