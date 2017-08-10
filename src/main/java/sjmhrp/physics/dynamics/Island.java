package sjmhrp.physics.dynamics;

import java.util.ArrayList;

import sjmhrp.core.Globals;
import sjmhrp.debug.DebugRenderer;
import sjmhrp.io.ConfigHandler;
import sjmhrp.physics.collision.Manifold;
import sjmhrp.physics.constraint.joints.Joint;

public class Island {
	
	private ArrayList<RigidBody> bodies = new ArrayList<RigidBody>();
	private ArrayList<Manifold> manifolds = new ArrayList<Manifold>();
	private ArrayList<Joint> joints = new ArrayList<Joint>();

	public Island addBody(CollisionBody body) {
		if(body instanceof RigidBody)bodies.add((RigidBody)body);
		return this;
	}

	public Island addManifold(Manifold manifold) {
		manifolds.add(manifold);
		return this;
	}

	public Island addJoint(Joint joint) {
		joints.add(joint);
		return this;
	}

	public boolean contains(CollisionBody b) {
		if(!(b instanceof RigidBody))return false;
		return bodies.contains(b);
	}

	public void solveVelocityConstraints() {
		for(Manifold m : manifolds) {
			if(ConfigHandler.getBoolean("debug"))DebugRenderer.addContacts(m);
			m.prestep();
		}
		for(Joint j : joints) {
			j.prestep();
		}
		for(int i = 0; i < Globals.IMPULSE_ITERATIONS; i++) {
			for(Manifold m : manifolds) {
				m.applyImpulse();
			}
			for(Joint j : joints) {
				j.applyImpulse();
			}
		}
	}

	public void solvePositionConstraints() {
		for(int i = 0; i < Globals.POSITION_ITERATIONS; i++) {
			for(Joint j : joints) {
				j.solvePosition();
			}
		}
	}

	public ArrayList<RigidBody> getBodies() {
		return bodies;
	}

	public ArrayList<Manifold> getManifolds() {
		return manifolds;
	}

	public ArrayList<Joint> getJoints() {
		return joints;
	}
}