package sjmhrp.physics.constraint.joints;

import sjmhrp.core.Globals;
import sjmhrp.linear.Matrix2d;
import sjmhrp.linear.Matrix3d;
import sjmhrp.linear.Matrix4d;
import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector2d;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.utils.GeometryUtils;
import sjmhrp.utils.ScalarUtils;

public class PrismaticJoint extends Joint {

	Vector3d localPoint1;
	Vector3d localPoint2;
	Vector3d al;
	Vector3d aw;
	Vector3d u;
	Vector3d n1;
	Vector3d n2;
	Vector3d r1;
	Vector3d r2;
	Vector3d r1uxaw;
	Vector3d r2xaw;
	Matrix2d effectiveMassTrans;
	Matrix3d effectiveMassRot;
	double effectiveMassLimit;
	double effectiveMassMotor;
	Vector2d impulseTrans = new Vector2d();
	Vector3d impulseRot = new Vector3d();
	double impulseMotor;
	double impulseLowerLimit;
	double impulseUpperLimit;
	Vector2d biasTrans = new Vector2d();
	Vector3d biasRot = new Vector3d();
	double biasLowerLimit;
	double biasUpperLimit;
	boolean isLimitEnabled;
	boolean isMotorEnabled;
	boolean isLowerLimitViolated;
	boolean isUpperLimitViolated;
	double lowerLimit = -1;
	double upperLimit = 1;
	double motorSpeed;
	double motorTorque;

	public PrismaticJoint(RigidBody b1, RigidBody b2, Vector3d anchorPoint, Vector3d axis) {
		super(b1,b2);
		axis.normalize();
		localPoint1 = Transform.transform(transform1.getInverse(),anchorPoint);
		localPoint2 = Transform.transform(transform2.getInverse(),anchorPoint);
		al = Matrix4d.transform(transform1.getInverse().orientation.getRotationMatrix(),axis);
		positionCorrection = PositionCorrection.NON_LINEAR_GAUSS_SEIDEL;
	}

	public PrismaticJoint(RigidBody b1, RigidBody b2, Vector3d anchorPoint, Vector3d axis, double lowerLimit, double upperLimit) {
		this(b1,b2,anchorPoint,axis);
		if(lowerLimit>upperLimit)throw new IllegalArgumentException("Lower Limit cannot be greater than Upper Limit");
		isLimitEnabled=true;
		this.lowerLimit=lowerLimit;
		this.upperLimit=upperLimit;
	}
	
	public PrismaticJoint(RigidBody b1, RigidBody b2, Vector3d anchorPoint, Vector3d axis, double lowerLimit, double upperLimit, double motorSpeed, double motorTorque) {
		this(b1,b2,anchorPoint,axis,lowerLimit,upperLimit);
		isMotorEnabled=true;
		this.motorSpeed=motorSpeed;
		this.motorTorque=motorTorque;
	}
	
	public PrismaticJoint setLimits(double lowerLimit, double upperLimit) {
		if(lowerLimit>upperLimit)throw new IllegalArgumentException("Lower Limit cannot be greater than Upper Limit");
		isLimitEnabled=true;
		this.lowerLimit=lowerLimit;
		this.upperLimit=upperLimit;
		return this;
	}
	
	public PrismaticJoint removeLimits() {
		isLimitEnabled=false;
		return this;
	}
	
	public PrismaticJoint setMotor(double motorSpeed, double motorTorque) {
		isMotorEnabled=true;
		this.motorSpeed=motorSpeed;
		this.motorTorque=motorTorque;
		return this;
	}
	
	public PrismaticJoint removeMotor() {
		isMotorEnabled=false;
		return this;
	}
	
	@Override
	public void prestep() {
		r1 = Matrix4d.transform(transform1.orientation.getRotationMatrix(),localPoint1);
		r2 = Matrix4d.transform(transform2.orientation.getRotationMatrix(),localPoint2);
		aw = Matrix4d.transform(transform1.orientation.getRotationMatrix(),al);
		n1 = new Vector3d();
		n2 = new Vector3d();
		GeometryUtils.computeBasis(aw,n1,n2);
		u = Vector3d.sub(Vector3d.add(transform2.position,r2),Vector3d.add(transform1.position,r1));
		double dot = u.dot(aw);
		double lowerLimitError = dot-lowerLimit;
		double upperLimitError = upperLimit-dot;
		boolean isLowerLimitViolatedOld = isLowerLimitViolated;
		boolean isUpperLimitViolatedOld = isUpperLimitViolated;
		isLowerLimitViolated=lowerLimitError<=0;
		isUpperLimitViolated=upperLimitError<=0;
		if(isLowerLimitViolatedOld!=isLowerLimitViolated)impulseLowerLimit=0;
		if(isUpperLimitViolatedOld!=isUpperLimitViolated)impulseUpperLimit=0;
		Vector3d r1u = Vector3d.add(r1,u);
		Vector3d r1uxn1 = Vector3d.cross(r1u,n1);
		Vector3d r1uxn2 = Vector3d.cross(r1u,n2);
		Vector3d r2xn1 = Vector3d.cross(r2,n1);
		Vector3d r2xn2 = Vector3d.cross(r2,n2);
		r1uxaw = Vector3d.cross(r1u,aw);
		r2xaw = Vector3d.cross(r2,aw);
		double a = body1.getInvMass()+body2.getInvMass()+r1uxn1.dot(Matrix3d.transform(body1.getInvInertiaMatrix(),r1uxn1))+r2xn1.dot(Matrix3d.transform(body2.getInvInertiaMatrix(),r2xn1));
		double b = r1uxn1.dot(Matrix3d.transform(body1.getInvInertiaMatrix(),r1uxn2))+r2xn1.dot(Matrix3d.transform(body2.getInvInertiaMatrix(),r2xn2));
		double c = r1uxn2.dot(Matrix3d.transform(body1.getInvInertiaMatrix(),r1uxn1))+r2xn2.dot(Matrix3d.transform(body2.getInvInertiaMatrix(),r2xn1));
		double d = body1.getInvMass()+body2.getInvMass()+r1uxn2.dot(Matrix3d.transform(body1.getInvInertiaMatrix(),r1uxn2))+r2xn2.dot(Matrix3d.transform(body2.getInvInertiaMatrix(),r2xn2));
		effectiveMassTrans = new Matrix2d(a,b,c,d);
		effectiveMassTrans.invert();
		effectiveMassRot = new Matrix3d(body1.getInvInertiaMatrix()).add(body2.getInvInertiaMatrix());
		effectiveMassRot.invert();
		if(Globals.positionCorrection&&positionCorrection==PositionCorrection.BAUMGARTE) {
			Vector3d w = Vector3d.sub(Vector3d.add(transform2.position,r2),Vector3d.add(transform1.position,r1));
			biasTrans = new Vector2d(w.dot(n1),w.dot(n2));
			biasRot = Vector3d.sub(transform2.orientation.getVector(),transform1.orientation.getVector()).scale(Globals.BAUMGARTE/PhysicsEngine.getTimeStep());
		}
		if(isLimitEnabled&&(isLowerLimitViolated||isUpperLimitViolated)) {
			effectiveMassLimit=0;
			effectiveMassLimit+=body1.getInvMass()+r1uxaw.dot(Matrix3d.transform(body1.getInvInertiaMatrix(),r1uxaw));
			effectiveMassLimit+=body2.getInvMass()+r2xaw.dot(Matrix3d.transform(body2.getInvInertiaMatrix(),r2xaw));
			effectiveMassLimit=effectiveMassLimit<=0?0:1/effectiveMassLimit;
			biasLowerLimit=0;
			biasUpperLimit=0;
			if(Globals.positionCorrection&&positionCorrection==PositionCorrection.BAUMGARTE) {
				biasLowerLimit=Globals.BAUMGARTE*lowerLimitError;
				biasUpperLimit=Globals.BAUMGARTE*upperLimitError;
			}
		}
		if(isMotorEnabled) {
			effectiveMassMotor = body1.getInvMass()+body2.getInvMass();
			effectiveMassMotor=effectiveMassMotor<=0?0:1/effectiveMassMotor;
		}
		if(!Globals.warmstart)return;
		applyImpulseTrans(impulseTrans);
		applyImpulseRot(impulseRot);
		applyImpulseLimit(impulseUpperLimit-impulseLowerLimit);
		applyImpulseMotor(impulseMotor);
	}

	@Override
	public void applyImpulse() {
		Vector2d lambdaTrans = calculateImpulseTrans();
		Vector3d lambdaRot = calculateImpulseRot();
		double lambdaMotor = isMotorEnabled?calculateImpulseMotor():0;
		double lambdaLowerLimit = isLimitEnabled&&isLowerLimitViolated?calculateImpulseLowerLimit():0;
		double lambdaUpperLimit = isLimitEnabled&&isUpperLimitViolated?calculateImpulseUpperLimit():0;
		if(Globals.accumulateImpulse) {
			impulseTrans.add(lambdaTrans);
			impulseRot.add(lambdaRot);
		}
		applyImpulseTrans(lambdaTrans);
		applyImpulseRot(lambdaRot);
		applyImpulseLimit(lambdaUpperLimit-lambdaLowerLimit);
		applyImpulseMotor(lambdaMotor);
	}

	Vector2d calculateImpulseTrans() {
		double x = n1.dot(body2.getLinearVel())+body2.getAngularVel().dot(Vector3d.cross(r2,n1))-n1.dot(body1.getLinearVel())-body1.getAngularVel().dot(Vector3d.cross(Vector3d.add(r1,u),n1));
		double y = n2.dot(body2.getLinearVel())+body2.getAngularVel().dot(Vector3d.cross(r2,n2))-n2.dot(body1.getLinearVel())-body1.getAngularVel().dot(Vector3d.cross(Vector3d.add(r1,u),n2));
		Vector2d minusJV = new Vector2d(-x,-y);
		minusJV.sub(biasTrans);
		return effectiveMassTrans.transform(minusJV);
	}

	Vector3d calculateImpulseRot() {
		Vector3d minusJV = Vector3d.sub(body1.getAngularVel(),body2.getAngularVel());
		minusJV.sub(biasRot);
		return effectiveMassRot.transform(minusJV);
	}

	double calculateImpulseLowerLimit() {
		double Jv = Vector3d.sub(body2.getLinearVel(),body1.getLinearVel()).dot(aw)+r2xaw.dot(body2.getAngularVel())-r1uxaw.dot(body1.getAngularVel());
		double lambda = effectiveMassLimit*(-Jv-biasLowerLimit);
		double temp = impulseLowerLimit;
		impulseLowerLimit = Math.max(impulseLowerLimit+lambda,0);
		return impulseLowerLimit-temp;
	}
	
	double calculateImpulseUpperLimit() {
		double Jv = Vector3d.sub(body1.getLinearVel(),body2.getLinearVel()).dot(aw)+r1uxaw.dot(body1.getAngularVel())-r2xaw.dot(body2.getAngularVel());
		double lambda = effectiveMassLimit*(-Jv-biasUpperLimit);
		double temp = impulseUpperLimit;
		impulseUpperLimit = Math.max(impulseUpperLimit+lambda,0);
		return impulseUpperLimit-temp;
	}
	
	double calculateImpulseMotor() {
		double Jv = aw.dot(Vector3d.scale(body1.getLinearVel(),body2.getLinearVel()));
		double maxImpulse = motorTorque*PhysicsEngine.getTimeStep();
		double lambda = effectiveMassMotor*(Jv-motorSpeed);
		double temp = impulseMotor;
		impulseMotor = ScalarUtils.clamp(impulseMotor+lambda,-maxImpulse,maxImpulse);
		return impulseMotor-temp;
	}
	
	void applyImpulseTrans(Vector2d lambda) {
		Vector3d dv = Vector3d.add(Vector3d.scale(lambda.x,n1),Vector3d.scale(lambda.y,n2));
		Vector3d dw1 = Matrix3d.transform(body1.getInvInertiaMatrix(),Vector3d.cross(Vector3d.add(r1,u),n1)).scale(-lambda.x);
		dw1.add(Matrix3d.transform(body1.getInvInertiaMatrix(),Vector3d.cross(Vector3d.add(r1,u),n2)).scale(-lambda.y));
		Vector3d dw2 = Matrix3d.transform(body2.getInvInertiaMatrix(),Vector3d.cross(r2,n1)).scale(lambda.x);
		dw2.add(Matrix3d.transform(body2.getInvInertiaMatrix(),Vector3d.cross(r2,n2)).scale(lambda.y));
		body1.addLinearVelocity(Vector3d.scale(-body1.getInvMass(),dv));
		body2.addLinearVelocity(Vector3d.scale(body2.getInvMass(),dv));
		body1.addAngularVelocity(dw1);
		body2.addAngularVelocity(dw2);
	}

	void applyImpulseRot(Vector3d lambda) {
		body1.addAngularVelocity(Matrix3d.transform(body1.getInvInertiaMatrix(),lambda).negate());
		body2.addAngularVelocity(Matrix3d.transform(body2.getInvInertiaMatrix(),lambda));
	}

	void applyImpulseLimit(double lambda) {
		applyImpulseMotor(lambda);
		body1.addAngularVelocity(Matrix3d.transform(body1.getInvInertiaMatrix(),Vector3d.scale(-lambda,r1uxaw.getUnit())));
		body2.addAngularVelocity(Matrix3d.transform(body2.getInvInertiaMatrix(),Vector3d.scale(lambda,r2xaw.getUnit())));
	}
	
	void applyImpulseMotor(double lambda) {
		Vector3d impulseTrans = Vector3d.scale(lambda,aw);
		body1.addLinearVelocity(Vector3d.scale(body1.getInvMass(),impulseTrans));
		body2.addLinearVelocity(Vector3d.scale(-body2.getInvMass(),impulseTrans));
	}
	
	@Override
	public void solvePosition() {
		if(!(Globals.positionCorrection&&positionCorrection==PositionCorrection.NON_LINEAR_GAUSS_SEIDEL))return;
		r1 = Matrix4d.transform(body1.getOrientation().getRotationMatrix(),localPoint1);
		r2 = Matrix4d.transform(body2.getOrientation().getRotationMatrix(),localPoint2);
		Vector3d aw = Matrix4d.transform(body1.getOrientation().getRotationMatrix(),al);
		n1 = new Vector3d();
		n2 = new Vector3d();
		GeometryUtils.computeBasis(aw,n1,n2);
		u = Vector3d.sub(Vector3d.add(body2.getPosition(),r2),Vector3d.add(body1.getPosition(),r1));
		Vector3d r1u = Vector3d.add(r1,u);
		Vector3d r1uxn1 = Vector3d.cross(r1u,n1);
		Vector3d r1uxn2 = Vector3d.cross(r1u,n2);
		Vector3d r2xn1 = Vector3d.cross(r2,n1);
		Vector3d r2xn2 = Vector3d.cross(r2,n2);
		double a = body1.getInvMass()+body2.getInvMass()+r1uxn1.dot(Matrix3d.transform(body1.getInvInertiaMatrix(),r1uxn1))+r2xn1.dot(Matrix3d.transform(body2.getInvInertiaMatrix(),r2xn1));
		double b = r1uxn1.dot(Matrix3d.transform(body1.getInvInertiaMatrix(),r1uxn2))+r2xn1.dot(Matrix3d.transform(body2.getInvInertiaMatrix(),r2xn2));
		double c = r1uxn2.dot(Matrix3d.transform(body1.getInvInertiaMatrix(),r1uxn1))+r2xn2.dot(Matrix3d.transform(body2.getInvInertiaMatrix(),r2xn1));
		double d = body1.getInvMass()+body2.getInvMass()+r1uxn2.dot(Matrix3d.transform(body1.getInvInertiaMatrix(),r1uxn2))+r2xn2.dot(Matrix3d.transform(body2.getInvInertiaMatrix(),r2xn2));
		effectiveMassTrans = new Matrix2d(a,b,c,d);
		effectiveMassTrans.invert();
		Vector2d lambdaTrans = new Vector2d(-u.dot(n1),-u.dot(n2));
		effectiveMassTrans.transform(lambdaTrans);
		Vector3d dv = Vector3d.add(Vector3d.scale(lambdaTrans.x,n1),Vector3d.scale(lambdaTrans.y,n2));
		body1.addPosition(Vector3d.scale(-body1.getInvMass(),dv));
		body2.addPosition(Vector3d.scale(body2.getInvMass(),dv));
	}
}