package sjmhrp.physics.dynamics;

import java.util.ArrayList;
import java.util.Iterator;

import sjmhrp.core.Globals;
import sjmhrp.io.ConfigHandler;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.physics.collision.Manifold;
import sjmhrp.physics.constraint.joints.Joint;
import sjmhrp.render.debug.DebugRenderer;

public class Island {
	
	static final double MAX_SPEED = 0.01;
	public static final double MAX_SLEEP_TIME = 20;
	
	private ArrayList<RigidBody> bodies = new ArrayList<RigidBody>();
	private ArrayList<Manifold> manifolds = new ArrayList<Manifold>();
	private ArrayList<Joint> joints = new ArrayList<Joint>();
	private double sleepTimer = MAX_SLEEP_TIME;

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

	public void prestep() {
		for(Manifold m : manifolds) {
			if(ConfigHandler.getBoolean("debug"))DebugRenderer.addContacts(m);
			m.prestep();
		}
		Iterator<Joint> i = joints.iterator();
		while(i.hasNext()) {
			Joint j = i.next();
			j.prestep();
			if(j.isBroken()) {
				j.destroy();
				i.remove();
			}
		}
	}
	
	public void solveVelocityConstraints() {
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

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Island))return false;
		Island i = (Island)o;
		for(RigidBody b : bodies) {
			if(!i.getBodies().contains(b))return false;
		}
		for(RigidBody b : getBodies()) {
			if(!bodies.contains(b))return false;
		}
		return true;
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
	
	public void setSleepTimer(double t) {
		sleepTimer=t;
	}
	
	public double getSleepTimer() {
		return sleepTimer;
	}
	
	public boolean isSleeping() {
		return sleepTimer<=0;
	}
	
	public boolean shouldSleep() {
		for(RigidBody b : bodies) {
			if(!b.canSleep()||b.getLinearVel().length()>MAX_SPEED||b.getAngularVel().length()>MAX_SPEED)return false;
		}
		return true;
	}
	
	public void sleep() {
		boolean shouldSleep = shouldSleep();
		sleepTimer=shouldSleep?sleepTimer-PhysicsEngine.getTimeStep():MAX_SLEEP_TIME;
		sleepTimer=Math.max(sleepTimer,0);
		for(RigidBody body : bodies) {
			if(body.canSleep())body.setSleeping(sleepTimer<=0);
		}
	}
}