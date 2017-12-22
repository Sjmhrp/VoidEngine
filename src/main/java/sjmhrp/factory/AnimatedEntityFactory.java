package sjmhrp.factory;

import java.util.ArrayList;

import sjmhrp.render.Loader;
import sjmhrp.render.animation.AnimatedModel;
import sjmhrp.render.entity.Entity;
import sjmhrp.render.models.ModelPool;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Quaternion;
import sjmhrp.utils.linear.Vector3d;

public class AnimatedEntityFactory extends Factory<Entity> {

	private static final long serialVersionUID = -4744783949241373770L;

	String model;
	ArrayList<String> animations;
	TextureFactory texture;
	Vector3d position;
	Quaternion orientation;
	Matrix4d skew;
	
	public AnimatedEntityFactory(String model) {
		this.model=model;
		animations = new ArrayList<String>();
		texture = new TextureFactory();
		position = new Vector3d();
		orientation = new Quaternion();
		skew = new Matrix4d().setIdentity();
	}
	
	public AnimatedEntityFactory setTexture(String texture) {
		this.texture.setTexture(texture);
		return this;
	}

	public AnimatedEntityFactory setNormalMap(String normalMap) {
		texture.setNormalMap(normalMap);
		return this;
	}

	public AnimatedEntityFactory setSpecularMap(String specularMap) {
		texture.setSpecularMap(specularMap);
		return this;
	}

	public AnimatedEntityFactory setColour(Vector3d colour) {
		texture.setColour(colour);
		return this;
	}

	public AnimatedEntityFactory setColour(String colour) {
		texture.setColour(colour);
		return this;
	}
	
	public AnimatedEntityFactory addAnimation(String animation) {
		animations.add(animation);
		return this;
	}
	
	public AnimatedEntityFactory setPosition(Vector3d position) {
		this.position=position;
		return this;
	}
	
	public AnimatedEntityFactory setOrientation(Quaternion orientation) {
		this.orientation=orientation;
		return this;
	}

	public AnimatedEntityFactory setSkew(Matrix4d skew) {
		this.skew=skew;
		return this;
	}
	
	@Override
	public Entity build() {
		AnimatedModel m = ModelPool.getAnimatedModel(model);
		for(String a : animations) {
			m.addAnimation(Loader.loadAnimation("/res/models/"+a+".dae"));
		}
		return new Entity(m,texture.build(),position,orientation).setSkew(skew);
	}
}