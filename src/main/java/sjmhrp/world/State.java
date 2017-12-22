package sjmhrp.world;

import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.utils.linear.Quaternion;
import sjmhrp.utils.linear.Vector3d;

public class State {
	
	Vector3d position;
	Quaternion rotation;
	Vector3d linearVel;
	Vector3d angularVel;
	double invmass;

	public State(RigidBody b) {
		this.position = new Vector3d(b.getPosition());
		this.rotation = new Quaternion(b.getOrientation());
		this.linearVel = new Vector3d(b.getLinearVel());
		this.angularVel = new Vector3d(b.getAngularVel());
		this.invmass = b.getInvMass();
	}

	public void load(RigidBody b) {
		b.getPosition().set(position);
		b.getOrientation().set(rotation);
		b.getLinearVel().set(linearVel);
		b.getAngularVel().set(angularVel);
		b.setInvMass(invmass);
	}
}