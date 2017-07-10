package sjmhrp.world;

import java.util.HashMap;

import sjmhrp.linear.Vector3d;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.sky.CelestialBody;
import sjmhrp.sky.SkyDome;

public class WorldState {

	HashMap<RigidBody,State> states;
	HashMap<CelestialBody,Vector3d> celestialBodies;
	int timeStamp;

	public WorldState(World world, int tick) {
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
		timeStamp = tick;
	}

	public int getTimeStep() {
		return timeStamp;
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