package sjmhrp.physics.dynamics.forces;

import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.utils.linear.Vector3d;

public class Spring extends Force {

	private static final long serialVersionUID = 7711297537246027354L;
	
	double springConstant;
	double naturalLength;
	boolean breakable;
	double breakingLengthSquared;
	RigidBody body1;
	RigidBody body2;
	Vector3d anchor;
	
	public Spring(RigidBody b1, RigidBody b2, double k) {
		this(b1,b2,k,0);
	}
	
	public Spring(RigidBody b1, RigidBody b2, double k, double l) {
		body1=b1;
		body2=b2;
		springConstant=k;
		naturalLength=l;
	}
	
	public Spring(RigidBody b1, RigidBody b2, double k, double l, double b) {
		this(b1,b2,k,l);
		breakable=true;
		breakingLengthSquared=b*b;
	}

	public Spring(RigidBody body, Vector3d anchor, double k) {
		this(body,anchor,k,0);
	}
	
	public Spring(RigidBody body, Vector3d anchor, double k, double l) {
		body1=body;
		this.anchor=anchor;
		springConstant=k;
		naturalLength=l;
	}
	
	public Spring(RigidBody body, Vector3d anchor, double k, double l, double b) {
		this(body,anchor,k,l);
		breakable=true;
		breakingLengthSquared=b*b;
	}

	public Spring setAnchor(Vector3d v) {
		anchor.set(v);
		return this;
	}
	
	@Override
	public boolean update() {
		if(anchor==null) {
			return !breakable||Vector3d.sub(body1.getPosition(),body2.getPosition()).lengthSquared()<=breakingLengthSquared;
		} else {
			return !breakable||Vector3d.sub(body1.getPosition(),anchor).lengthSquared()<=breakingLengthSquared;
		}
	}

	@Override
	public void applyForce() {
		if(anchor==null) {
			Vector3d normal = Vector3d.sub(body2.getPosition(),body1.getPosition());
			double x = naturalLength-normal.length();
			normal.scale(-springConstant*x);
			body1.applyCentralForce(normal);
			body2.applyCentralForce(normal.negate());
		} else {
			Vector3d normal = Vector3d.sub(anchor,body1.getPosition());
			double x = naturalLength-normal.length();
			normal.scale(-springConstant*x);
			body1.applyCentralForce(normal);
		}
	}	
}