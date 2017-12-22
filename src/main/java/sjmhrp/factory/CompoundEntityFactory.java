package sjmhrp.factory;

import java.util.ArrayList;

import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.physics.shapes.CompoundShape;
import sjmhrp.physics.shapes.ConvexShape;
import sjmhrp.render.entity.Entity;
import sjmhrp.render.models.ModelPool;
import sjmhrp.render.models.RawModel;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Vector3d;

public class CompoundEntityFactory extends Factory<ArrayList<Entity>> {

	private static final long serialVersionUID = 2199438155048004692L;
	 
	RigidBody body;
	int shapes;
	TextureFactory[] textures;
	String[] models;
	boolean[] hasModels;
	
	public CompoundEntityFactory(RigidBody body) {
		this.body=body;
		shapes = ((CompoundShape)body.getCollisionShape()).getShapes().size();
		textures = new TextureFactory[shapes];
		models = new String[shapes];
		hasModels = new boolean[shapes];
		for(int i = 0; i < shapes; i++) {
			textures[i]=new TextureFactory();
		}
	}
	
	public CompoundEntityFactory setTextures(String... textures) {
		if(textures.length!=shapes)return this;
		for(int i = 0; i < shapes; i++) {
			this.textures[i].setTexture(textures[i]);
		}
		return this;
	}
	
	public CompoundEntityFactory setNormalMaps(String... normalMaps) {
		if(normalMaps.length!=shapes)return this;
		for(int i = 0; i < shapes; i++) {
			this.textures[i].setNormalMap(normalMaps[i]);
		}
		return this;
	}
	
	public CompoundEntityFactory setSpecularMaps(String... specularMaps) {
		if(specularMaps.length!=shapes)return this;
		for(int i = 0; i < shapes; i++) {
			this.textures[i].setSpecularMap(specularMaps[i]);
		}
		return this;
	}
	
	public CompoundEntityFactory setColours(Vector3d... colours) {
		if(colours.length!=shapes)return this;
		for(int i = 0; i < shapes; i++) {
			this.textures[i].setColour(colours[i]);
		}
		return this;
	}
	
	public CompoundEntityFactory setColours(String... colours) {
		if(colours.length!=shapes)return this;
		for(int i = 0; i < shapes; i++) {
			this.textures[i].setColour(colours[i]);
		}
		return this;
	}
	
	public CompoundEntityFactory setModels(String... models) {
		if(models.length!=shapes)return this;
		this.models=models;
		for(int i = 0; i < shapes; i++) {
			hasModels[i]=models[i]!=null;
		}
		return this;
	}
	
	@Override
	public ArrayList<Entity> build() {
		if(!body.isCompound())return null;
		CompoundShape shape = (CompoundShape)body.getCollisionShape();
		ArrayList<Entity> es = new ArrayList<Entity>();
		for(int i = 0; i < shapes; i++) {
			ConvexShape s = shape.getShapes().get(i);
			RawModel m = ModelPool.getModel(hasModels[i]?models[i]:s.getName());
			es.add(new Entity(m,textures[i].build(),body.getPosition(),body.getOrientation()).setSkew(Matrix4d.mul(shape.getOffset(s).getMatrix(),s.getSkewMatrix())));
		}
		return es;
	}
}