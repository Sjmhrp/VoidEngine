package sjmhrp.render.debug;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDisable;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import sjmhrp.physics.PhysicsEngine;
import sjmhrp.physics.collision.Contact;
import sjmhrp.physics.collision.Manifold;
import sjmhrp.physics.collision.RaycastResult;
import sjmhrp.physics.dynamics.Ray;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.physics.shapes.CompoundShape;
import sjmhrp.physics.shapes.ConvexShape;
import sjmhrp.render.Loader;
import sjmhrp.render.RenderHandler;
import sjmhrp.render.entity.Entity;
import sjmhrp.render.entity.EntityShader;
import sjmhrp.render.models.ModelPool;
import sjmhrp.render.models.RawModel;
import sjmhrp.render.shader.Shader;
import sjmhrp.render.textures.ModelTexture;
import sjmhrp.render.textures.TexturePool;
import sjmhrp.render.view.Camera;
import sjmhrp.utils.Profiler;
import sjmhrp.utils.linear.Quaternion;
import sjmhrp.utils.linear.Transform;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.world.World;

public class DebugRenderer {

	public static final int MAX_INSTANCES = 100000;
	public static final int DATA_LENGTH = 6;
	public static final double CONTACT_SIZE = 0.5;

	private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(DATA_LENGTH * MAX_INSTANCES);
	
	static ArrayList<Entity> contacts = new ArrayList<Entity>();
	static RawModel sphereModel;
	static ModelTexture sphereTexture;
	static int vao;
	static int vbo;
	
	public static void init() {
		vao = Loader.createVao();
		vbo = Loader.createEmptyVBO(MAX_INSTANCES*DATA_LENGTH);
		Loader.addInstancedAttrib(vao,vbo,0,4,DATA_LENGTH,0);
		Loader.addInstancedAttrib(vao,vbo,1,4,DATA_LENGTH,3);
		sphereModel = ModelPool.getModel("SPHERE");
		sphereTexture = TexturePool.getColour("red");
	}

	public static void clearContacts() {
		contacts.clear();
	}

	public static void addContacts(Manifold m) {
		for(Contact c : m.points) {
			addContact(c.globalPointA);
		}
	}

	public static void addContact(Vector3d pos) {
		contacts.add(new Entity(sphereModel,sphereTexture,pos,new Quaternion(),false,false).scale(CONTACT_SIZE));
	}

	public static ArrayList<Entity> getContacts() {
		return contacts;
	}

	public static void setContacts(ArrayList<Entity> c) {
		contacts = c;
	}

	public static void raycast(Camera camera) {
		Vector3d dir = camera.getRotMatrix().getInverse().transform(new Vector3d(0,0,-1));
		Ray ray = new Ray(camera.getPosition(),dir,50);
		PhysicsEngine.getWorlds().forEach(world->{
			RaycastResult result = world.raycast(ray);
			if(result.collides())addContact(result.hitPoint());
		});
	}

	public static void render(World world, Camera camera) {
		renderAABB(Shader.getAabbShader(),world,camera);
		renderContacts(Shader.getEntityShader(),camera);
	}

	static void renderContacts(EntityShader s, Camera c) {
		s.start();
		GL30.glBindVertexArray(sphereModel.getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		s.loadRows(sphereTexture.getNumberOfRows());
		s.loadFakeLighting(sphereTexture.getFakeLighting());
		s.loadReflect(0);
		s.loadNormals(false);
		s.loadSpecular(false);
		glDisable(GL_BLEND);
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, sphereTexture.getAlbedoID());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		glBindTexture(GL_TEXTURE_2D, sphereTexture.getNormalID());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		glBindTexture(GL_TEXTURE_2D, sphereTexture.getSpecularID());
		for(Entity e : new ArrayList<Entity>(contacts)) {
			RenderHandler.renderEntity(e,s,c);
		}
		RenderHandler.unbind();
		s.stop();
	}

	static void renderAABB(AABBShader s, World w, Camera c) {
		s.start();
		s.loadViewMatrix(c.getViewMatrix());
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		RenderHandler.disableCulling();
		GL30.glBindVertexArray(vao);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		int pointer = 0;
		int count = 0;
		float[] data = new float[MAX_INSTANCES*DATA_LENGTH];
		for(RigidBody b : w.getRigidBodies()) {
			if(b.getCollisionShape() instanceof CompoundShape) {
				CompoundShape cs = ((CompoundShape)b.getCollisionShape());
				for(ConvexShape shape : cs.getShapes()) {
					Transform t = Transform.mul(b.getTransform(),cs.getOffset(shape));
					Vector3d pos = shape.getBoundingBox(t).getCenter();
					Vector3d radius = shape.getBoundingBox(t).getRadius();
					data[pointer++] = (float)pos.x;
					data[pointer++] = (float)pos.y;
					data[pointer++] = (float)pos.z;
					data[pointer++] = (float)radius.x;
					data[pointer++] = (float)radius.y;
					data[pointer++] = (float)radius.z;
					count++;
				}
			} else {
				Vector3d pos = b.getBoundingBox().getCenter();
				Vector3d radius = b.getBoundingBox().getRadius();
				data[pointer++] = (float) pos.x;
				data[pointer++] = (float) pos.y;
				data[pointer++] = (float) pos.z;
				data[pointer++] = (float) radius.x;
				data[pointer++] = (float) radius.y;
				data[pointer++] = (float) radius.z;
				count++;
			}
		}
		Loader.updateVbo(vbo,data,buffer);
		GL31.glDrawArraysInstanced(GL11.GL_POINTS,0,1,count);
		Profiler.drawCalls++;
		RenderHandler.unbind();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		RenderHandler.enableCulling();
		s.stop();
	}
}