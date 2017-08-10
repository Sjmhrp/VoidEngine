package sjmhrp.world;

import sjmhrp.linear.Quaternion;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.dynamics.RigidBody;

public class State {
	
	Vector3d position;
	Quaternion rotation;
	Vector3d linearVel;
	Vector3d angularVel;
	Vector3d gravity;
	double invmass;

	public State(RigidBody b) {
		this.position = new Vector3d(b.getPosition());
		this.rotation = new Quaternion(b.getOrientation());
		this.linearVel = new Vector3d(b.getLinearVel());
		this.angularVel = new Vector3d(b.getAngularVel());
		this.gravity = new Vector3d(b.getGravity());
		this.invmass = b.getInvMass();
	}

	public void load(RigidBody b) {
		b.getPosition().set(position);
		b.getOrientation().set(rotation);
		b.getLinearVel().set(linearVel);
		b.getAngularVel().set(angularVel);
		b.getGravity().set(gravity);
		b.setInvMass(invmass);
	}
}