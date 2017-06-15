package sjmhrp.physics.constraint.joints;

import sjmhrp.core.Globals;
import sjmhrp.linear.Matrix2d;
import sjmhrp.linear.Matrix3d;
import sjmhrp.linear.Matrix4d;
import sjmhrp.linear.Quaternion;
import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector2d;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.utils.GeometryUtils;
import sjmhrp.utils.MatrixUtils;
import sjmhrp.utils.ScalarUtils;

public class RevoluteJoint extends Joint {

	Vector3d localPoint1;
	Vector3d localPoint2;
	Vector3d a1l;
	Vector3d a2l;
	Vector3d a1;
	Vector3d a2;
	Vector3d b2;
	Vector3d c2;
	Matrix3d r1;
	Matrix3d r2;
	Matrix3d effectiveMassTrans;
	Matrix2d effectiveMassRot;
	double effectiveMassLimitMotor;
	Vector3d impulseTrans = new Vector3d();
	Vector2d impulseRot = new Vector2d();
	double impulseMotor;
	double impulseLowerLimit;
	double impulseUpperLimit;
	Vector3d biasTrans = new Vector3d();
	Vector2d biasRot = new Vector2d();
	double biasLowerLimit;
	double biasUpperLimit;
	Quaternion initOrientationDifInv;
	boolean isLimitEnabled = false;
	boolean isMotorEnabled = false;
	boolean isLowerLimitViolated = false;
	boolean isUpperLimitViolated = false;
	double lowerLimit = -1;
	double upperLimit = 1;
	double motorSpeed = 0;
	double motorTorque = 0;

	public RevoluteJoint(RigidBody b1, RigidBody b2, Vector3d anchorPoint, Vector3d axis) {
		super(b1,b2);
		axis.normalize();
		localPoint1 = Transform.transform(transform1.getInverse(),anchorPoint);
		localPoint2 = Transform.transform(transform2.getInverse(),anchorPoint);
		a1l = Matrix4d.transform(transform1.getInverse().orientation.getRotationMatrix(),axis);
		a2l = Matrix4d.transform(transform2.getInverse().orientation.getRotationMatrix(),axis);
		positionCorrection = PositionCorrection.NON_LINEAR_GAUSS_SEIDEL;
		initOrientationDifInv=Quaternion.mul(transform2.orientation,transform1.orientation.getInverse()).normalize().invert();
	}

	public RevoluteJoint(RigidBody b1, RigidBody b2, Vector3d anchorPoint, Vector3d axis, double lowerLimit, double upperLimit) {
		this(b1,b2,anchorPoint,axis);
		if(lowerLimit>upperLimit)throw new IllegalArgumentException("Lower Limit cannot be greater than Upper Limit");
		isLimitEnabled=true;
		this.lowerLimit=lowerLimit;
		this.upperLimit=upperLimit;
	}

	public RevoluteJoint(RigidBody b1, RigidBody b2, Vector3d anchorPoint, Vector3d axis, double lowerLimit, double upperLimit, double motorSpeed, double motorTorque) {
		this(b1,b2,anchorPoint,axis,lowerLimit,upperLimit);
		isMotorEnabled=true;
		this.motorSpeed=motorSpeed;
		this.motorTorque=motorTorque;
	}
	
	public RevoluteJoint setLimits(double lowerLimit, double upperLimit) {
		if(lowerLimit>upperLimit)throw new IllegalArgumentException("Lower Limit cannot be greater than Upper Limit");
		isLimitEnabled=true;
		this.lowerLimit=lowerLimit;
		this.upperLimit=upperLimit;
		return this;
	}
	
	public RevoluteJoint removeLimits() {
		isLimitEnabled=false;
		return this;
	}
	
	public RevoluteJoint setMotor(double motorSpeed, double motorTorque) {
		isMotorEnabled=true;
		this.motorSpeed=motorSpeed;
		this.motorTorque=motorTorque;
		return this;
	}
	
	public RevoluteJoint removeMotor() {
		isMotorEnabled=false;
		return this;
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
		a1 = Matrix4d.transform(transform1.orientation.getRotationMatrix(),a1l);
		a2 = Matrix4d.transform(transform2.orientation.getRotationMatrix(),a2l);
		b2 = new Vector3d();
		c2 = new Vector3d();
		double hingeAngle = hingeAngle(transform1.orientation,transform2.orientation);
		double lowerError = hingeAngle-lowerLimit;
		double upperError = upperLimit-hingeAngle;
		boolean lowerLimitViolatedOld = isLowerLimitViolated;
		isLowerLimitViolated=lowerError<=0;
		if(lowerLimitViolatedOld!=isLowerLimitViolated)impulseLowerLimit=0;
		boolean upperLimitViolatedOld = isUpperLimitViolated;
		isUpperLimitViolated=upperError<=0;
		if(upperLimitViolatedOld!=isUpperLimitViolated)impulseUpperLimit=0;
		GeometryUtils.computeBasis(a2,b2,c2);
		Vector3d b2xa1 = Vector3d.cross(b2,a1);
		Vector3d c2xa1 = Vector3d.cross(c2,a1);
		Vector3d I1b2xa1 = Matrix3d.transform(body1.getInvInertiaMatrix(),b2xa1);
		Vector3d I2b2xa1 = Matrix3d.transform(body2.getInvInertiaMatrix(),b2xa1);
		Vector3d I1c2xa1 = Matrix3d.transform(body1.getInvInertiaMatrix(),c2xa1);
		Vector3d I2c2xa1 = Matrix3d.transform(body2.getInvInertiaMatrix(),c2xa1);
		double a = b2xa1.dot(I1b2xa1)+b2xa1.dot(I2b2xa1);
		double b = b2xa1.dot(I1c2xa1)+b2xa1.dot(I2c2xa1);
		double c = c2xa1.dot(I1b2xa1)+c2xa1.dot(I2b2xa1);
		double d = c2xa1.dot(I1c2xa1)+c2xa1.dot(I2c2xa1);
		effectiveMassRot = new Matrix2d(a,b,c,d);
		effectiveMassRot.invert();
		if(Globals.positionCorrection&&positionCorrection==PositionCorrection.BAUMGARTE) {
			biasTrans = Vector3d.sub(Vector3d.add(body2.getPosition(),rB),Vector3d.add(body1.getPosition(),rA)).scale(Globals.BAUMGARTE/PhysicsEngine.getTimeStep());
			biasRot = new Vector2d(a1.dot(b2),a1.dot(c2)).scale(Globals.BAUMGARTE/PhysicsEngine.getTimeStep());
		}
		if(isMotorEnabled||(isLimitEnabled&&(isLowerLimitViolated||isUpperLimitViolated))) {
			effectiveMassLimitMotor=0;
			effectiveMassLimitMotor+=a1.dot(Matrix3d.transform(body1.getInvInertiaMatrix(),a1));
			effectiveMassLimitMotor+=a1.dot(Matrix3d.transform(body2.getInvInertiaMatrix(),a1));
			effectiveMassLimitMotor=effectiveMassLimitMotor<=0?0:1/effectiveMassLimitMotor;
			if(isLimitEnabled) {
				biasLowerLimit=0;
				biasUpperLimit=0;
				if(Globals.positionCorrection&&positionCorrection==PositionCorrection.BAUMGARTE){
					biasLowerLimit=Globals.BAUMGARTE*lowerError;
					biasUpperLimit=Globals.BAUMGARTE*upperError;
				}
			}
		}
		if(!Globals.warmstart)return;
		applyImpulseTrans(impulseTrans);
		applyImpulseRot(impulseRot);
		applyImpulseLimitMotor(impulseMotor+impulseLowerLimit-impulseUpperLimit);
	}

	@Override
	public void applyImpulse() {
		Vector3d lambdaTrans = calculateImpulseTrans();
		Vector2d lambdaRot = calculateImpulseRot();
		double lambdaMotor = isMotorEnabled?calculateImpulseMotor():0;
		double lambdaLowerLimit = isLimitEnabled&&isLowerLimitViolated?calculateImpulseLowerLimit():0;
		double lambdaUpperLimit = isLimitEnabled&&isUpperLimitViolated?calculateImpulseUpperLimit():0;
		if(Globals.accumulateImpulse) {
			impulseTrans.add(lambdaTrans);
			impulseRot.add(lambdaRot);
		}
		applyImpulseTrans(lambdaTrans);
		applyImpulseRot(lambdaRot);
		applyImpulseLimitMotor(lambdaMotor+lambdaLowerLimit-lambdaUpperLimit);
	}

	Vector3d calculateImpulseTrans() {
		Vector3d minusJV = new Vector3d(body1.getLinearVel());
		minusJV.sub(body2.getLinearVel());
		minusJV.add(Matrix3d.transform(r2,body2.getAngularVel()));
		minusJV.sub(Matrix3d.transform(r1,body1.getAngularVel()));
		minusJV.sub(biasTrans);
		return Matrix3d.transform(effectiveMassTrans,minusJV);
	}

	Vector2d calculateImpulseRot() {
		Vector3d dw = Vector3d.sub(body1.getAngularVel(),body2.getAngularVel());
		Vector2d minusJV = new Vector2d(Vector3d.cross(b2,a1).dot(dw),Vector3d.cross(c2,a1).dot(dw));
		minusJV.sub(biasRot);
		return effectiveMassRot.transform(minusJV);
	}

	double calculateImpulseLowerLimit() {
        double Jv = Vector3d.sub(body2.getAngularVel(),body1.getAngularVel()).dot(a1);
        double lambda = effectiveMassLimitMotor*(-Jv-biasLowerLimit);
        double temp = impulseLowerLimit;
        impulseLowerLimit = Math.max(impulseLowerLimit+lambda,0);
        return impulseLowerLimit-temp;
	}
	
	double calculateImpulseUpperLimit() {
		double Jv = Vector3d.sub(body1.getAngularVel(),body2.getAngularVel()).dot(a1);
        double lambda = effectiveMassLimitMotor*(-Jv-biasUpperLimit);
        double temp = impulseUpperLimit;
        impulseUpperLimit = Math.max(impulseUpperLimit+lambda,0);
        return impulseUpperLimit-temp;
	}
	
	double calculateImpulseMotor() {
		double Jv = a1.dot(Vector3d.sub(body1.getAngularVel(),body2.getAngularVel()));
		double maxImpulse = motorTorque*PhysicsEngine.getTimeStep();
		double lambda = effectiveMassLimitMotor*(Jv-motorSpeed);
		double temp = impulseMotor;
		impulseMotor = ScalarUtils.clamp(impulseMotor+lambda,-maxImpulse,maxImpulse);
		return impulseMotor-temp;
	}
	
	void applyImpulseTrans(Vector3d lambda) {
		body1.addLinearVelocity(Vector3d.scale(-body1.getInvMass(),lambda));
		body2.addLinearVelocity(Vector3d.scale(body2.getInvMass(),lambda));
		body1.addAngularVelocity(Matrix3d.transform(Matrix3d.mul(body1.getInvInertiaMatrix(),r1),lambda).negate());
		body2.addAngularVelocity(Matrix3d.transform(Matrix3d.mul(body2.getInvInertiaMatrix(),r2),lambda));
	}

	void applyImpulseRot(Vector2d lambda) {
		Vector3d l = Vector3d.cross(b2,a1).scale(lambda.x).add(Vector3d.cross(c2,a1).scale(lambda.y));
		body1.addAngularVelocity(Matrix3d.transform(body1.getInvInertiaMatrix(),l).negate());
		body2.addAngularVelocity(Matrix3d.transform(body2.getInvInertiaMatrix(),l));
	}

	void applyImpulseLimitMotor(double lambda) {
		Vector3d l = Vector3d.scale(-lambda,a1);
		body1.addAngularVelocity(Matrix3d.transform(body1.getInvInertiaMatrix(),l));
		body2.addAngularVelocity(Matrix3d.transform(body2.getInvInertiaMatrix(),l.getNegative()));
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
		a1 = Matrix4d.transform(body1.getOrientation().getRotationMatrix(),a1l);
		a2 = Matrix4d.transform(body2.getOrientation().getRotationMatrix(),a2l);
		b2 = new Vector3d();
		c2 = new Vector3d();
		GeometryUtils.computeBasis(a2,b2,c2);
		Vector3d b2xa1 = Vector3d.cross(b2,a1);
		Vector3d c2xa1 = Vector3d.cross(c2,a1);
		Vector3d I1b2xa1 = Matrix3d.transform(body1.getInvInertiaMatrix(),b2xa1);
		Vector3d I2b2xa1 = Matrix3d.transform(body2.getInvInertiaMatrix(),b2xa1);
		Vector3d I1c2xa1 = Matrix3d.transform(body1.getInvInertiaMatrix(),c2xa1);
		Vector3d I2c2xa1 = Matrix3d.transform(body2.getInvInertiaMatrix(),c2xa1);
		double a = b2xa1.dot(I1b2xa1)+b2xa1.dot(I2b2xa1);
		double b = b2xa1.dot(I1c2xa1)+b2xa1.dot(I2c2xa1);
		double c = c2xa1.dot(I1b2xa1)+c2xa1.dot(I2b2xa1);
		double d = c2xa1.dot(I1c2xa1)+c2xa1.dot(I2c2xa1);
		effectiveMassRot = new Matrix2d(a,b,c,d);
		effectiveMassRot.invert();
		Vector3d lambdaTrans = Vector3d.sub(Vector3d.add(body1.getPosition(),rA),Vector3d.add(body2.getPosition(),rB));
		Vector2d lambdaRot = new Vector2d(a1.dot(b2),a1.dot(c2)).negate();
		effectiveMassTrans.transform(lambdaTrans);
		effectiveMassRot.transform(lambdaRot);
		Vector3d l = Vector3d.cross(b2,a1).scale(lambdaRot.x).add(Vector3d.cross(c2,a1).scale(lambdaRot.y));
		body1.addPosition(Vector3d.scale(-body1.getInvMass(),lambdaTrans));
		body2.addPosition(Vector3d.scale(body2.getInvMass(),lambdaTrans));
		body1.rotate(Matrix3d.transform(Matrix3d.mul(body1.getInvInertiaMatrix(),r1),lambdaTrans).negate());
		body2.rotate(Matrix3d.transform(Matrix3d.mul(body2.getInvInertiaMatrix(),r2),lambdaTrans));
		body1.rotate(Matrix3d.transform(body1.getInvInertiaMatrix(),l.getNegative()));
		body2.rotate(Matrix3d.transform(body2.getInvInertiaMatrix(),l));
	}

	private double hingeAngle(Quaternion q1, Quaternion q2) {
		double angle = 4;
		Quaternion diff = Quaternion.mul(q2,q1.getInverse()).normalize();
		Quaternion r = Quaternion.mul(diff,initOrientationDifInv).normalize();
		double cosHA = r.w;
		double sinHA = r.getVector().length();
		double dot = r.getVector().dot(a1);
		if(dot>=0) {
			angle=2*Math.atan2(sinHA,cosHA);
		} else {
			angle=2*Math.atan2(sinHA,-cosHA);
		}
		angle = computeNormalizedAngle(angle);
		return computeCorrespondingAngle(angle,lowerLimit,upperLimit);
	}
	
	private double computeNormalizedAngle(double angle) {
		angle%=2*Math.PI;
		angle=angle<-Math.PI?angle+2*Math.PI:angle>Math.PI?angle-2*Math.PI:angle;
		return angle;
	}
	
	private double computeCorrespondingAngle(double input, double lower, double upper) {
		if(upper<=lower)return input;
		if(input>upper) {
			double dUpper = Math.abs(computeNormalizedAngle(input-upper));
			double dLower = Math.abs(computeNormalizedAngle(input-lower));
			return dUpper>dLower?input-2*Math.PI:input;
		}
		if(input<lower) {
			double dUpper = Math.abs(computeNormalizedAngle(upper-input));
			double dLower = Math.abs(computeNormalizedAngle(lower-input));
			return dUpper>dLower?input:input+2*Math.PI;
		}
		return input;
	}
}