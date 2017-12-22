package sjmhrp.physics.dynamics;

import sjmhrp.physics.PhysicsEngine;
import sjmhrp.physics.shapes.CollisionShape;
import sjmhrp.render.RenderRegistry;
import sjmhrp.render.entity.Entity;
import sjmhrp.utils.linear.Matrix3d;
import sjmhrp.utils.linear.Quaternion;
import sjmhrp.utils.linear.Transform;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.world.World;

public class RigidBody extends CollisionBody {

	private static final long serialVersionUID = -374575764140226617L;
	
	protected Vector3d velocity = new Vector3d();
	protected Vector3d prevVelocity = new Vector3d();
	protected Vector3d angularVelocity = new Vector3d();
	protected Vector3d prevAngularVelocity = new Vector3d();
	protected Vector3d totalForce = new Vector3d();
	protected Vector3d totalTorque = new Vector3d();
	protected boolean sleeping = false;
	protected boolean canSleep = true;
	protected boolean isInIsland = false;

	protected double invmass = 1;
	protected Matrix3d invInertiaMatrix = new Matrix3d();
	
	protected Vector3d angularFactor = new Vector3d(1);

	public RigidBody(double mass, CollisionShape shape) {
		this(new Vector3d(),new Quaternion(),mass,shape);
	}

	public RigidBody(Vector3d position, double mass, CollisionShape shape) {
		this(position,new Quaternion(),mass,shape);
	}

	public RigidBody(Transform t, double mass, CollisionShape shape) {
		this(t.position,new Quaternion(t.orientation),mass,shape);
	}

	public RigidBody(Vector3d position, Quaternion orientation, double mass, CollisionShape shape) {
		super(position,orientation,shape);
		this.invmass = mass==0?0:1f/mass;
		calculateInertiaMatrix(mass);
	}

	public void destroy() {
		world.removeBody(this);
		for(Entity e : RenderRegistry.getEntities(this)) {
			RenderRegistry.removeEntity(e);
		}
	}
	
	public void calculateInertiaMatrix(double mass) {
		Matrix3d m = new Matrix3d();
		Vector3d inertia = collisionShape.calculateLocalInertia(mass);
		if(invmass!=0&&inertia.x!=0&&inertia.y!=0&&inertia.z!=0) {
			m.setIdentity();
			m.m00=1d/inertia.x;
			m.m11=1d/inertia.y;
			m.m22=1d/inertia.z;
		}
		this.invInertiaMatrix = m;
	}

	public void integratePosition() {
		if(invmass==0)return;
		position.add(Vector3d.scale(0.5*PhysicsEngine.getTimeStep(),Vector3d.add(velocity,prevVelocity)));
		orientation.rotate(Vector3d.add(angularVelocity,prevAngularVelocity).scale(angularFactor),0.5*PhysicsEngine.getTimeStep());
	}

	public void integrateVelocity() {
		if(invmass==0)return;
		double dt = PhysicsEngine.getTimeStep();
		prevVelocity.set(velocity);
		prevAngularVelocity.set(angularVelocity);
		velocity.add(Vector3d.scale(dt*invmass,totalForce));
		angularVelocity.add(Matrix3d.transform(invInertiaMatrix,Vector3d.scale(dt,totalTorque)));
		clearForces();
	}

	public Transform predictTransform() {
		if(invmass==0||(isSleeping()&&canSleep()))return getTransform();
		Vector3d p = new Vector3d(position);
		Quaternion q = new Quaternion(orientation);
		p.add(Vector3d.scale(0.5*PhysicsEngine.getTimeStep(),Vector3d.add(velocity,prevVelocity)));
		q.rotate(Vector3d.add(angularVelocity,prevAngularVelocity),0.5*PhysicsEngine.getTimeStep());
		return new Transform(p,q);
	}

	public void stop() {
		velocity.zero();
		angularVelocity.zero();
	}
	
	public void setLinearVel(Vector3d velocity) {
		this.velocity.set(velocity);
	}

	@Override
	public void addLinearVelocity(Vector3d v) {
		velocity.add(v);
	}

	@Override
	public void addAngularVelocity(Vector3d v) {
		angularVelocity.add(v);
	}

	public boolean isDynamic() {
		return invmass!=0;
	}

	public void clearForces() {
		totalForce.set(0, 0, 0);
		totalTorque.set(0, 0, 0);
	}

	public void setLinearVel(double x, double y, double z) {
		velocity.x = x;
		velocity.y = y;
		velocity.z = z;
	}

	@Override
	public Vector3d getLinearVel() {
		return velocity;
	}

	@Override
	public Vector3d getVelocityAtPoint(Vector3d p) {
		return Vector3d.add(velocity,Vector3d.cross(angularVelocity,p));
	}
	
	public void setAngularVel(Vector3d angularVelocity) {
		this.angularVelocity.set(angularVelocity);
	}

	public void setAngularVel(double x, double y, double z) {
		angularVelocity.x = x;
		angularVelocity.y = y;
		angularVelocity.z = z;
	}
	
	public void applyCentralForce(Vector3d force) {
		totalForce.add(force);
	}

	public void applyTorque(Vector3d torque) {
		totalTorque.add(torque);
	}

	public void applyForce(Vector3d force, Vector3d relPosition) {
		applyCentralForce(force);
		applyTorque(Vector3d.cross(relPosition,force));
	}

	public void applyImpulse(Vector3d impulse,Vector3d relPosition) {
		impulse.scale(invmass);
		addLinearVelocity(impulse);
		addAngularVelocity(Vector3d.cross(relPosition,impulse));
	}
	
	@Override
	public RigidBody rotate(Vector3d v) {
		super.rotate(v);
		return this;
	}
	
	@Override
	public RigidBody rotate(double x, double y, double z) {
		super.rotate(x,y,z);
		return this;
	}
	
	@Override
	public Vector3d getAngularVel() {
		return angularVelocity;
	}

	public void setAngularFactor(Vector3d d) {
		angularFactor = d;
	}
	
	public Vector3d getAngularFactor() {
		return angularFactor;
	}
	
	public void setInvMass(double invmass) {
		if(this.invmass==invmass)return;
		this.invmass = invmass;
		calculateInertiaMatrix(invmass==0?0:1f/invmass);
	}

	@Override
	public double getInvMass() {
		return invmass;
	}

	@Override
	public Matrix3d getInvInertiaMatrix() {
		return invInertiaMatrix;
	}

	@Override
	public Vector3d getInvInertia() {
		return new Vector3d(invInertiaMatrix.m00,invInertiaMatrix.m11,invInertiaMatrix.m22);
	}
	
	public double getMass() {
		return invmass==0?0:1d/invmass;
	}

	public World getWorld() {
		return world;
	}

	public Vector3d getGravityDir() {
		return world.getGravityDir(this);
	}
	
	@Override
	public boolean isStatic() {
		return invmass==0;
	}

	public void setSleeping(boolean b) {
		sleeping=b;
	}

	public boolean isSleeping() {
		return sleeping;
	}

	public void setCanSleep(boolean b) {
		canSleep=b;
	}
	
	public boolean canSleep() {
		return canSleep;
	}
	
	public boolean isInIsland() {
		return isInIsland;
	}

	public void setInIsland(boolean isInIsland) {
		this.isInIsland = isInIsland;
	}
}