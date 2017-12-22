package sjmhrp.world;

import java.util.ArrayList;
import java.util.HashMap;

import sjmhrp.physics.PhysicsEngine;
import sjmhrp.physics.constraint.joints.Joint;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.physics.dynamics.forces.Force;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.world.sky.CelestialBody;
import sjmhrp.world.sky.SkyDome;

public class WorldState {
	
	ArrayList<CollisionBody> bodies;
	ArrayList<Joint> joints;
	ArrayList<Force> forces;
	HashMap<RigidBody,State> states;
	HashMap<CelestialBody,Vector3d> celestialBodies;
	long timeStamp;
	double timeStep;

	public WorldState(World world, int tick) {
		bodies = new ArrayList<CollisionBody>(world.bodies);
		joints = new ArrayList<Joint>(world.joints);
		forces = new ArrayList<Force>(world.forces);
		states = new HashMap<RigidBody,State>();
		celestialBodies = new HashMap<CelestialBody,Vector3d>();
		for(RigidBody body : world.rigidBodies) {
			states.put(body,new State(body));
		}
		if(world.hasSky()) {
			for(CelestialBody body : world.getSky().getBodies()) {
				celestialBodies.put(body,new Vector3d(body.getPosition()));
			}
		}
		timeStamp = System.nanoTime();
		timeStep=PhysicsEngine.getTimeStep();
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public double getTimeStep() {
		return timeStep;
	}
	
	public State get(RigidBody b) {
		return states.get(b);
	}
	
	public void loadSky(SkyDome s) {
		for(CelestialBody body : s.getBodies()) {
			body.getPosition().set(celestialBodies.get(body));
		}
	}
}