package sjmhrp.factory;

import sjmhrp.render.entity.Entity;
import sjmhrp.render.models.ModelPool;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Quaternion;
import sjmhrp.utils.linear.Vector2d;
import sjmhrp.utils.linear.Vector3d;

public class DecalFactory extends Factory<Entity> {

	private static final long serialVersionUID = -2758657128479773774L;

	Vector3d position;
	Quaternion orientation = new Quaternion();
	Vector2d size = new Vector2d(1);
	TextureFactory texture;
	
	public DecalFactory(Vector3d position) {
		this.position=position;
		texture=new TextureFactory();
	}
	
	public DecalFactory setPosition(Vector3d pos) {
		position=pos;
		return this;
	}
	
	public DecalFactory setOrientation(Quaternion o) {
		orientation=o;
		return this;
	}
	
	public DecalFactory setSize(double d) {
		size=new Vector2d(d);
		return this;
	}
	
	public DecalFactory setSize(Vector2d size) {
		this.size=size;
		return this;
	}
	
	public DecalFactory setTexture(String texture) {
		this.texture.setTexture(texture);
		return this;
	}

	public DecalFactory setNormalMap(String normalMap) {
		texture.setNormalMap(normalMap);
		return this;
	}

	public DecalFactory setSpecularMap(String specularMap) {
		texture.setSpecularMap(specularMap);
		return this;
	}

	public DecalFactory setColour(Vector3d colour) {
		texture.setColour(colour);
		return this;
	}

	public DecalFactory setColour(String colour) {
		texture.setColour(colour);
		return this;
	}
	
	public Entity build() {
		return new Entity(ModelPool.getModel("3quad"),texture.build(),position,orientation,false,true).setSkew(new Matrix4d().setIdentity().scale(new Vector3d(size.x,size.y,0)));
	}
}