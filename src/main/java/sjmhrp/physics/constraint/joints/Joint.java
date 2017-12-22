package sjmhrp.physics.constraint.joints;

import java.io.Serializable;

import sjmhrp.core.Globals;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.utils.linear.Transform;

public abstract class Joint implements Serializable {
	
	private static final long serialVersionUID = 218972928092203567L;
	protected PositionCorrection positionCorrection;
	protected RigidBody body1;
	protected RigidBody body2;
	protected Transform transform1;
	protected Transform transform2;
	protected boolean isInIsland = false;
	protected boolean breakable = false;
	protected double breakingStrength = Globals.JOINT_BREAKING_STRENGTH;

	public Joint(RigidBody b1, RigidBody b2) {
		body1=b1;
		body2=b2;
		transform1 = body1.getTransform();
		transform2 = body2.getTransform();
	}

	public void updateTransforms(Transform t1, Transform t2) {
		transform1 = t1;
		transform2 = t2;
	}

	public void destroy() {
		body1.getWorld().removeJoint(this);
		body2.getWorld().removeJoint(this);
	}
	
	public abstract void prestep();

	public abstract void applyImpulse();

	public abstract void solvePosition();
	
	public abstract void resetImpulse();

	public abstract boolean isBroken();
	
	public RigidBody getBody1() {
		return body1;
	}

	public RigidBody getBody2() {
		return body2;
	}

	public boolean isInIsland() {
		return isInIsland;
	}

	public void setInIsland(boolean isInIsland) {
		this.isInIsland = isInIsland;
	}

	public boolean isBreakable() {
		return breakable;
	}
	
	public Joint setBreakable(boolean b) {
		breakable = b;
		return this;
	}
	
	public double getBreakingStrength() {
		return breakingStrength;
	}
	
	public Joint setBreakingStrength(double strength) {
		breakingStrength=strength;
		return this;
	}
	
	public enum PositionCorrection {
		NONE,
		BAUMGARTE,
		NON_LINEAR_GAUSS_SEIDEL
	}
}