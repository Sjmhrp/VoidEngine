package sjmhrp.physics.constraint.joints;

import sjmhrp.core.Globals;
import sjmhrp.linear.Matrix3d;
import sjmhrp.linear.Matrix4d;
import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.utils.MatrixUtils;

public class SphericalJoint extends Joint {

	private static final long serialVersionUID = -4822784126903109392L;
	
	Vector3d localPointA;
	Vector3d localPointB;
	Matrix3d rA;
	Matrix3d rB;
	Matrix3d effectiveMass;
	Vector3d impulse = new Vector3d();
	Vector3d bias = new Vector3d();

	public SphericalJoint(RigidBody b1, RigidBody b2, Vector3d point) {
		super(b1, b2);
		localPointA = Transform.transform(transform1.getInverse(),point);
		localPointB = Transform.transform(transform2.getInverse(),point);
		positionCorrection = PositionCorrection.NON_LINEAR_GAUSS_SEIDEL;
	}

	@Override
	public void prestep() {
		Vector3d r1 = Matrix4d.transform(transform1.orientation.getRotationMatrix(),localPointA);
		Vector3d r2 = Matrix4d.transform(transform2.orientation.getRotationMatrix(),localPointB);
		rA = MatrixUtils.createSkewSymmetric(r1);
		rB = MatrixUtils.createSkewSymmetric(r2);
		Matrix3d IrA = Matrix3d.mul(rA,Matrix3d.mul(body1.getInvInertiaMatrix(),rA.getTranspose()));
		Matrix3d IrB = Matrix3d.mul(rB,Matrix3d.mul(body2.getInvInertiaMatrix(),rB.getTranspose()));
		effectiveMass = new Matrix3d(IrA);
		effectiveMass.add(IrB);
		effectiveMass.add(new Matrix3d(body1.getInvMass()+body2.getInvMass()));
		effectiveMass.invert();
		if(Globals.positionCorrection&&positionCorrection==PositionCorrection.BAUMGARTE) {
			bias = new Vector3d(Vector3d.sub(Vector3d.add(transform2.position,r2),Vector3d.add(transform1.position,r1))).scale(Globals.BAUMGARTE/PhysicsEngine.getTimeStep());
		}
		if(!Globals.warmstart)return;
		body1.addLinearVelocity(Vector3d.scale(-body1.getInvMass(),impulse));
		body2.addLinearVelocity(Vector3d.scale(body2.getInvMass(),impulse));
		body1.addAngularVelocity(Matrix3d.transform(Matrix3d.mul(body1.getInvInertiaMatrix(),rA.getTranspose()),impulse));
		body2.addAngularVelocity(Matrix3d.transform(Matrix3d.mul(body2.getInvInertiaMatrix(),rB.getTranspose()),impulse).negate());
	}

	public Vector3d evaluateConstraint() {
		return new Vector3d(body2.getLinearVel()).sub(body1.getLinearVel()).add(Matrix3d.transform(rA,body1.getAngularVel())).sub(Matrix3d.transform(rB,body2.getAngularVel()));
	}

	@Override
	public void applyImpulse() {
		Vector3d lambda = new Vector3d(body1.getLinearVel());
		lambda.sub(body2.getLinearVel());
		lambda.add(Matrix3d.transform(rB,body2.getAngularVel()));
		lambda.sub(Matrix3d.transform(rA,body1.getAngularVel()));
		lambda.sub(bias);
		effectiveMass.transform(lambda);
		if(Globals.accumulateImpulse)impulse.add(lambda);
		body1.addLinearVelocity(Vector3d.scale(-body1.getInvMass(),lambda));
		body2.addAngularVelocity(Vector3d.scale(body2.getInvMass(),lambda));
		body1.addAngularVelocity(Matrix3d.transform(Matrix3d.mul(body1.getInvInertiaMatrix(),rA.getTranspose()),lambda));
		body2.addAngularVelocity(Matrix3d.transform(Matrix3d.mul(body2.getInvInertiaMatrix(),rB.getTranspose()),lambda).negate());
	}

	@Override
	public void solvePosition() {
		if(!(Globals.positionCorrection&&positionCorrection==PositionCorrection.NON_LINEAR_GAUSS_SEIDEL))return;
		Vector3d r1 = Matrix4d.transform(body1.getOrientation().getRotationMatrix(),localPointA);
		Vector3d r2 = Matrix4d.transform(body2.getOrientation().getRotationMatrix(),localPointB);
		rA = MatrixUtils.createSkewSymmetric(r1);
		rB = MatrixUtils.createSkewSymmetric(r2);
		Matrix3d IrA = Matrix3d.mul(rA,Matrix3d.mul(body1.getInvInertiaMatrix(),rA.getTranspose()));
		Matrix3d IrB = Matrix3d.mul(rB,Matrix3d.mul(body2.getInvInertiaMatrix(),rB.getTranspose()));
		effectiveMass = new Matrix3d(IrA);
		effectiveMass.add(IrB);
		effectiveMass.add(new Matrix3d(body1.getInvMass()+body2.getInvMass()));
		effectiveMass.invert();
		Vector3d lambda = Vector3d.sub(Vector3d.add(body1.getPosition(),r1),Vector3d.add(body2.getPosition(),r2));
		effectiveMass.transform(lambda);
		body1.addPosition(Vector3d.scale(-body1.getInvMass(),lambda));
		body2.addPosition(Vector3d.scale(body2.getInvMass(),lambda));
		body1.rotate(Matrix3d.transform(Matrix3d.mul(body1.getInvInertiaMatrix(),rA.getTranspose()),lambda));
		body2.rotate(Matrix3d.transform(Matrix3d.mul(body2.getInvInertiaMatrix(),rB.getTranspose()),lambda).negate());
	}
}