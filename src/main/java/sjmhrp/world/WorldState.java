package sjmhrp.world;

import java.util.HashMap;

import sjmhrp.linear.Quaternion;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.sky.Sun;

public class WorldState {

	HashMap<RigidBody,State> states;
	Vector3d sunPosition = new Vector3d();
	Quaternion sunOrientation = new Quaternion();
	int timeStamp;

	public WorldState(World world, int tick) {
		states = new HashMap<RigidBody,State>();
		for(RigidBody body : world.rigidBodies) {
			states.put(body,new State(body));
		}
		sunPosition.set(world.getSun().getPosition());
		sunOrientation.set(world.getSun().getOrientation());
		timeStamp = tick;
	}

	public int getTimeStep() {
		return timeStamp;
	}

	public State get(RigidBody b) {
		return states.get(b);
	}

	public void loadSun(Sun s) {
		s.getPosition().set(sunPosition);
		s.getOrientation().set(sunOrientation);
	}
}