package sjmhrp.physics.constraint.joints;

import sjmhrp.core.Globals;
import sjmhrp.linear.Matrix3d;
import sjmhrp.linear.Matrix4d;
import sjmhrp.linear.Quaternion;
import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.utils.MatrixUtils;

public class WeldJoint extends Joint {

	private static final long serialVersionUID = -7168973032428356534L;
	
	Vector3d localPoint1;
	Vector3d localPoint2;
	Matrix3d r1;
	Matrix3d r2;
	Matrix3d effectiveMassTrans;
	Matrix3d effectiveMassRot;
	Vector3d impulseTrans = new Vector3d();
	Vector3d impulseRot = new Vector3d();
	Vector3d biasTrans = new Vector3d();
	Vector3d biasRot = new Vector3d();
	Quaternion initialDifference;

	public WeldJoint(RigidBody b1, RigidBody b2, Vector3d anchorPoint) {
		super(b1, b2);
		localPoint1 = Transform.transform(transform1.getInverse(),anchorPoint);
		localPoint2 = Transform.transform(transform2.getInverse(),anchorPoint);
		initialDifference = Quaternion.mul(transform2.orientation,transform1.orientation.getInverse());
		initialDifference.normalize();
		initialDifference.invert();
		positionCorrection = PositionCorrection.NON_LINEAR_GAUSS_SEIDEL;
	}

	@Override
	public void prestep() {
		Vector3d rA = Matrix4d.transform(transform1.orientation.getRotationMatrix(),localPoint1);
		Vector3d rB = Matrix4d.transform(transform2.orientation.getRotationMatrix(),localPoint2);
		r1 = MatrixUtils.createSkewSymmetric(rA);
		r2 = MatrixUtils.createSkewSymmetric(rB);
		Matrix3d IrA = Matrix3d.mul(r1,Matrix3d.mul(body1.getInvInertiaMatrix(),r1.getTranspose()));
		Matrix3d IrB = Matrix3d.mul(r2,Matrix3d.mul(body2.getInvInertiaMatrix(),r2.getTranspose()));
		effectiveMassTrans = new Matrix3d(IrA);
		effectiveMassTrans.add(IrB);
		effectiveMassTrans.add(new Matrix3d(body1.getInvMass()+body2.getInvMass()));
		effectiveMassTrans.invert();
		effectiveMassRot = new Matrix3d(body1.getInvInertiaMatrix());
		effectiveMassRot.add(body2.getInvInertiaMatrix());
		effectiveMassRot.invert();
		if(Globals.positionCorrection&&positionCorrection==PositionCorrection.BAUMGARTE) {
			Quaternion dq = Quaternion.mul(transform2.orientation,transform1.orientation.getInverse());
			Quaternion error = Quaternion.mul(dq,initialDifference);
			biasTrans = Vector3d.sub(Vector3d.add(transform2.position,rB),Vector3d.add(transform1.position,rA)).scale(Globals.BAUMGARTE/PhysicsEngine.getTimeStep());
			biasRot = Matrix3d.transform(effectiveMassRot,error.getVector()).scale(Globals.BAUMGARTE/PhysicsEngine.getTimeStep());
		}
		if(!Globals.warmstart)return;
		applyImpulseTrans(impulseTrans);
		applyImpulseRot(impulseRot);
	}

	@Override
	public void applyImpulse() {
		Vector3d lambdaTrans = calculateImpulseTrans();
		Vector3d lambdaRot = calculateImpulseRot();
		if(Globals.accumulateImpulse) {
			impulseTrans.add(lambdaTrans);
			impulseRot.add(lambdaRot);
		}
		applyImpulseTrans(lambdaTrans);
		applyImpulseRot(lambdaRot);
	}

	Vector3d calculateImpulseTrans() {
		Vector3d minusJV = new Vector3d(body1.getLinearVel());
		minusJV.sub(body2.getLinearVel());
		minusJV.add(Matrix3d.transform(r2,body2.getAngularVel()));
		minusJV.sub(Matrix3d.transform(r1,body1.getAngularVel()));
		minusJV.sub(biasTrans);
		return effectiveMassTrans.transform(minusJV);
	}

	Vector3d calculateImpulseRot() {
		Vector3d minusJV = Vector3d.sub(body1.getAngularVel(),body2.getAngularVel());
		minusJV.sub(biasRot);
		return effectiveMassRot.transform(minusJV);
	}

	void applyImpulseTrans(Vector3d lambda) {
		body1.addLinearVelocity(Vector3d.scale(-body1.getInvMass(),lambda));
		body2.addLinearVelocity(Vector3d.scale(body2.getInvMass(),lambda));
		body1.addAngularVelocity(Matrix3d.transform(Matrix3d.mul(body1.getInvInertiaMatrix(),r1),lambda).negate());
		body2.addAngularVelocity(Matrix3d.transform(Matrix3d.mul(body2.getInvInertiaMatrix(),r2),lambda));
	}

	void applyImpulseRot(Vector3d lambda) {
		body1.addAngularVelocity(Matrix3d.transform(body1.getInvInertiaMatrix(),lambda).negate());
		body2.addAngularVelocity(Matrix3d.transform(body2.getInvInertiaMatrix(),lambda));
	}

	@Override
	public void solvePosition() {
		if(!(Globals.positionCorrection&&positionCorrection==PositionCorrection.NON_LINEAR_GAUSS_SEIDEL))return;
		Vector3d rA = Matrix4d.transform(body1.getOrientation().getRotationMatrix(),localPoint1);
		Vector3d rB = Matrix4d.transform(body2.getOrientation().getRotationMatrix(),localPoint2);
		r1 = MatrixUtils.createSkewSymmetric(rA);
		r2 = MatrixUtils.createSkewSymmetric(rB);
		Matrix3d IrA = Matrix3d.mul(r1,Matrix3d.mul(body1.getInvInertiaMatrix(),r1.getTranspose()));
		Matrix3d IrB = Matrix3d.mul(r2,Matrix3d.mul(body2.getInvInertiaMatrix(),r2.getTranspose()));
		effectiveMassTrans = new Matrix3d(IrA);
		effectiveMassTrans.add(IrB);
		effectiveMassTrans.add(new Matrix3d(body1.getInvMass()+body2.getInvMass()));
		effectiveMassTrans.invert();
		Vector3d lambdaTrans = Vector3d.sub(Vector3d.add(body1.getPosition(),rA),Vector3d.add(body2.getPosition(),rB));
		effectiveMassTrans.transform(lambdaTrans);
		body1.addPosition(Vector3d.scale(-body1.getInvMass(),lambdaTrans));
		body2.addPosition(Vector3d.scale(body2.getInvMass(),lambdaTrans));
	}
}