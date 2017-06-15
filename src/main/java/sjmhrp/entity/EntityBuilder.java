package sjmhrp.entity;

import java.util.ArrayList;

import sjmhrp.io.IOHandler;
import sjmhrp.linear.Transform;
import sjmhrp.models.ModelPool;
import sjmhrp.models.TexturedModel;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.physics.shapes.CompoundShape;
import sjmhrp.physics.shapes.ConvexShape;
import sjmhrp.textures.ModelTexture;
import sjmhrp.textures.TexturePool;

public class EntityBuilder {

	public static TexturedModel newModel(String obj, String texture) {
		return new TexturedModel(ModelPool.getModel(obj),TexturePool.getTexture(texture));
	}

	public static TexturedModel newModel(String obj, String texture, String normalMap) {
		return new TexturedModel(ModelPool.getModel(obj),TexturePool.getTexture(texture,normalMap));
	}

	public static TexturedModel newModel(String obj, String texture, String normalMap, String specularMap) {
		return new TexturedModel(ModelPool.getModel(obj),TexturePool.getTexture(texture,normalMap,specularMap));
	}

	public static RigidBody newStaticTriMesh(String mesh, Transform t) {
		return new RigidBody(t,0,IOHandler.parseCollisionMesh(mesh,t));
	}

	public static Entity createEntity(RigidBody b, String texture) {
		return createEntity(b,TexturePool.getTexture(texture));
	}

	public static Entity createEntity(RigidBody b, String model, String texture) {
		return createEntity(b,model,TexturePool.getTexture(texture));
	}

	public static Entity createEntity(RigidBody b, ModelTexture texture) {
		return createEntity(b,b.getCollisionShape().getName(),texture);
	}

	public static Entity createEntity(RigidBody b, String model, ModelTexture texture) {
		return new Entity(new TexturedModel(ModelPool.getModel(model),texture),b.getPosition(),b.getOrientation()).setSkew(b.getSkew());
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
}