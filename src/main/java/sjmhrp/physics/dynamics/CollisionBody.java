package sjmhrp.physics.dynamics;

import java.io.Serializable;

import sjmhrp.physics.collision.broadphase.AABB;
import sjmhrp.physics.shapes.CollisionShape;
import sjmhrp.physics.shapes.CompoundShape;
import sjmhrp.physics.shapes.ConvexShape;
import sjmhrp.physics.shapes.SphereShape;
import sjmhrp.physics.shapes.StaticTriMesh;
import sjmhrp.utils.linear.Matrix3d;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Quaternion;
import sjmhrp.utils.linear.Transform;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.world.World;

public class CollisionBody implements Serializable{

	private static final long serialVersionUID = 8586413579905937863L;
	
	protected Vector3d position;
	protected Quaternion orientation;

	protected double friction = 0.4;
	protected double restitution = 0.0;

	protected CollisionShape collisionShape;

	transient protected World world;

	public CollisionBody(CollisionShape collisionShape) {
		this(new Vector3d(),new Quaternion(),collisionShape);
	}

	public CollisionBody(Vector3d position, CollisionShape collisionShape) {
		this(position,new Quaternion(),collisionShape);
	}

	public CollisionBody(Vector3d position, Quaternion orientation, CollisionShape collisionShape) {
		this.position=position;
		this.orientation=orientation;
		this.collisionShape=collisionShape;
	}

	public AABB getBoundingBox() {
		return getBoundingBox(getTransform());
	}

	public AABB getBoundingBox(Transform t) {
		return collisionShape.getBoundingBox(t);
	}

	public Transform getTransform() {
		return new Transform(position,orientation);
	}

	public boolean isInfinite() {
		return collisionShape.isInfinite();
	}

	public boolean isSphere() {
		return collisionShape instanceof SphereShape;
	}

	public boolean isConvex() {
		return collisionShape instanceof ConvexShape;
	}

	public boolean isCompound() {
		return collisionShape instanceof CompoundShape;
	}
	
	public boolean isStatic() {
		return true;
	}

	public boolean isStaticTriMesh() {
		return collisionShape instanceof StaticTriMesh;
	}

	public CollisionShape getCollisionShape() {
		return collisionShape;
	}

	public Matrix4d getSkew() {
		return collisionShape.getSkewMatrix();
	}

	public double getFriction() {
		return friction;
	}

	public double getRestitution() {
		return restitution;
	}

	public void setWorld(World w) {
		world = w;
	}

	public World getWorld() {
		return world;
	}
	
	public void setFriction(double f) {
		friction=f;
	}

	public void setRestitution(double c) {
		restitution=c;
	}

	public void setPosition(double x, double y, double z) {
		position.x = x;
		position.y = y;
		position.z = z;
	}

	public void setPosition(Vector3d position) {
		this.position.set(position);
	}

	public void setTransform(Transform transform) {
		position.set(transform.position);
		orientation.set(transform.orientation);
	}

	public Vector3d getPosition() {
		return position;
	}

	public void addPosition(Vector3d v) {
		position.add(v);
	}

	public void setOrientation(Quaternion rotation) {
		this.orientation.set(rotation);
	}

	public void setOrientation(Matrix4d rotation) {
		this.orientation.set(new Quaternion(rotation.to3Matrix()));
	}

	public Quaternion getOrientation() {
		return orientation;
	}

	public Matrix3d getRotation() {
		return orientation.getRotationMatrix().to3Matrix();
	}
	
	public Matrix3d getInvRotation() {
		return orientation.getRotationMatrix().to3Matrix().invert();
	}
	
	public CollisionBody rotate(double x, double y, double z) {
		return rotate(new Vector3d(x,y,z));
	}

	public CollisionBody rotate(Vector3d v) {
		orientation.rotate(v,1);
		return this;
	}

	public Matrix3d getInvInertiaMatrix() {
		return new Matrix3d();
	}

	public Vector3d getInvInertia() {
		return new Vector3d();
	}
	
	public double getInvMass() {
		return 0;
	}

	public void addLinearVelocity(Vector3d v){}

	public void addAngularVelocity(Vector3d v){}

	public Vector3d getLinearVel() {
		return new Vector3d();
	}

	public Vector3d getVelocityAtPoint(Vector3d p) {
		return new Vector3d();
	}
	
	public Vector3d getAngularVel() {
		return new Vector3d();
	}
	
	public Vector3d support(Vector3d direction) {
		if(direction.lengthSquared()==0)return new Vector3d();
		Vector3d dir = direction.getUnit();
		Vector3d d = Matrix3d.transform(getInvRotation(),dir);
		if(isConvex()) {
			return ((ConvexShape)collisionShape).getLocalSupportPoint(d);
		}
		if(isCompound()) {
			Vector3d furthest = null;
			for(ConvexShape s : ((CompoundShape)collisionShape).getShapes()) {
				Vector3d v = s.getLocalSupportPoint(d);
				if(furthest==null||v.dot(d)>furthest.dot(d))furthest=v;
			}
			return furthest;
		}
		return null;
	}
}