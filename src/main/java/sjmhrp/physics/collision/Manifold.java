package sjmhrp.physics.collision;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import sjmhrp.core.Globals;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.utils.GeometryUtils;
import sjmhrp.utils.ScalarUtils;
import sjmhrp.utils.VectorUtils;
import sjmhrp.utils.linear.Transform;
import sjmhrp.utils.linear.Vector3d;

public class Manifold implements Serializable {

	private static final long serialVersionUID = 4791203012622865480L;

	public static final int MANIFOLD_SIZE = 4;

	public final ArrayList<Contact> points = new ArrayList<Contact>();
	public CollisionBody body1;
	public CollisionBody body2;
	public Transform transform1;
	public Transform transform2;
	Vector3d prevSeperatingAxis = new Vector3d(1,0,0);
	double combinedFriction;
	double combinedRestitution;

	boolean persistent = true;
	boolean isInIsland = false;

	public Manifold(CollisionBody b1, CollisionBody b2, Transform t1, Transform t2) {
		body1=b1;
		body2=b2;
		transform1=t1;
		transform2=t2;
		combinedFriction=Math.sqrt(body1.getFriction()*body2.getFriction());
		combinedRestitution=0.5*(body1.getRestitution()+body2.getRestitution());
	}

	public void updateTransforms(Transform t1, Transform t2) {
		transform1=t1;
		transform2=t2;
		persistent=true;
	}

	public void addContact(Contact contact) {
		Contact closest = null;
		double dist = Float.MAX_VALUE;
		for(Contact c : points) {
			Vector3d rA = Vector3d.sub(contact.globalPointA,c.globalPointA);
			Vector3d rB = Vector3d.sub(contact.globalPointB,c.globalPointB);
			boolean rAc = rA.lengthSquared()>Globals.PERSISTENT_THRESHOLD_SQUARED;
			boolean rBc = rB.lengthSquared()>Globals.PERSISTENT_THRESHOLD_SQUARED;
			double rAl = rA.lengthSquared();
			if(!(rAc&&rBc)&&rAl<dist){
				dist = rAl;
				closest = c;
			}
		}
		if(closest==null) {
			points.add(contact);
		} else {
			contact.normalImpulse=closest.normalImpulse;
			contact.frictionImpluse1=closest.frictionImpluse1;
			contact.frictionImpluse2=closest.frictionImpluse2;
			points.remove(closest);
			points.add(contact);
		}
	}

	public void update() {
		persistent=false;
		for(Iterator<Contact> i=points.iterator();i.hasNext();) {
			Contact c = i.next();
			Vector3d globalA = Transform.transform(transform1,c.localPointA);
			Vector3d globalB = Transform.transform(transform2,c.localPointB);
			Vector3d rAB = Vector3d.sub(globalB,globalA);
			Vector3d rA = Vector3d.sub(c.globalPointA,globalA);
			Vector3d rB = Vector3d.sub(c.globalPointB,globalB);
			boolean p = c.originalNormal.dot(rAB)<0;
			boolean rAc = rA.lengthSquared()<=Globals.PERSISTENT_THRESHOLD_SQUARED;
			boolean rBc = rB.lengthSquared()<=Globals.PERSISTENT_THRESHOLD_SQUARED;
			if(!(p&&rAc&&rBc)||c.depth<=0) {
				i.remove();
			} else {
				rAB.negate();
				c.updateNormal(rAB);
				c.depth=rAB.length();
				c.globalPointA.set(globalA);
				c.globalPointB.set(globalB);
			}
		}
		if(points.size()<=MANIFOLD_SIZE)return;
		Contact deepest = null;
		double depth = -Float.MAX_VALUE;
		for(Contact c : points) {
			if(c.depth>depth) {
				deepest = c;
				depth = c.depth;
			}
		}
		Contact furthest1 = null;
		double distanceSq1 = -Float.MAX_VALUE;
		for(Contact c : points) {
			double d = Vector3d.sub(c.localPointA,deepest.localPointA).lengthSquared();
			if(d>distanceSq1) {
				furthest1 = c;
				distanceSq1 = d;
			}
		}
		Contact furthest2 = null;
		double distanceSq2 = -Float.MAX_VALUE;
		for(Contact c : points) {
			double d = GeometryUtils.distanceToLineSq(c.localPointA,deepest.localPointA,furthest1.localPointA);
			if(d>distanceSq2) {
				furthest2 = c;
				distanceSq2 = d;
			}
		}
		Contact furthest3 = null;
		double distanceSq3 = -Float.MAX_VALUE;
		for(Contact c : points) {
			double d = GeometryUtils.distanceToTriangleSq(c.localPointA,deepest.localPointA,furthest1.localPointA,furthest2.localPointA);
			if(d>distanceSq3) {
				furthest3 = c;
				distanceSq3 = d;
			}
		}
		points.clear();
		points.add(deepest);
		points.add(furthest1);
		points.add(furthest2);
		points.add(furthest3);
	}

	public void prestep() {
		for(Contact c : points) {
			Vector3d rA = Vector3d.sub(c.globalPointA,body1.getPosition());
			Vector3d rB = Vector3d.sub(c.globalPointB,body2.getPosition());
			Vector3d dv = new Vector3d(body2.getLinearVel());
			dv.sub(body1.getLinearVel());
			dv.add(Vector3d.cross(body2.getAngularVel(),rB));
			dv.sub(Vector3d.cross(body1.getAngularVel(),rA));
			Vector3d vn = body1.getInvInertiaMatrix().transform(VectorUtils.crossABA(rA,c.normal));
			vn.add(body2.getInvInertiaMatrix().transform(VectorUtils.crossABA(rB,c.normal)));
			Vector3d vt1 = body1.getInvInertiaMatrix().transform(VectorUtils.crossABA(rA,c.tangent1));
			vt1.add(body2.getInvInertiaMatrix().transform(VectorUtils.crossABA(rB,c.tangent1)));
			Vector3d vt2 = body1.getInvInertiaMatrix().transform(VectorUtils.crossABA(rA,c.tangent2));
			vt2.add(body2.getInvInertiaMatrix().transform(VectorUtils.crossABA(rB,c.tangent2)));
			double m = (body1.getInvMass()+body2.getInvMass());
			c.normalMass = 1f/(m+Math.abs(vn.dot(c.normal)));
			c.frictionMass1 = 1f/(m+vt1.dot(c.tangent1));
			c.frictionMass2 = 1f/(m+vt2.dot(c.tangent2));
			c.bias = -(Globals.positionCorrection?Globals.BAUMGARTE:0)/PhysicsEngine.getTimeStep()*Math.max(0,c.depth-Globals.LINEARSLOP);
			c.bias+=combinedRestitution*Math.min(0,dv.dot(c.normal)+Globals.RESTITUTIONSLOP);
			if(Globals.accumulateImpulse) {
				Vector3d p = Vector3d.scale(c.normalImpulse,c.normal);
				p.add(Vector3d.scale(c.frictionImpluse1,c.tangent1));
				p.add(Vector3d.scale(c.frictionImpluse2,c.tangent2));
				body1.addLinearVelocity(Vector3d.scale(-body1.getInvMass(),p));
				body2.addLinearVelocity(Vector3d.scale(body2.getInvMass(),p));
				body1.addAngularVelocity(body1.getInvInertiaMatrix().transform(Vector3d.cross(rA,p)).negate());
				body2.addAngularVelocity(body2.getInvInertiaMatrix().transform(Vector3d.cross(rB,p)));
			}
		}
	}

	public void applyImpulse() {
		for(Contact c : points) {
			Vector3d rA = Vector3d.sub(c.globalPointA,body1.getPosition());
			Vector3d rB = Vector3d.sub(c.globalPointB,body2.getPosition());
			Vector3d dv = new Vector3d(body2.getLinearVel());
			dv.sub(body1.getLinearVel());
			dv.add(Vector3d.cross(body2.getAngularVel(),rB));
			dv.sub(Vector3d.cross(body1.getAngularVel(),rA));
			double vn = dv.dot(c.normal);
			double lambda = -(vn+c.bias)*c.normalMass;
			if(Globals.accumulateImpulse) {
				double temp = c.normalImpulse;
				c.normalImpulse=Math.max(c.normalImpulse+lambda,0);
				lambda = c.normalImpulse-temp;
			} else {
				c.normalImpulse=lambda;
				lambda = lambda>0?lambda:0;
			}
			Vector3d p = Vector3d.scale(lambda,c.normal);
			body1.addLinearVelocity(Vector3d.scale(-body1.getInvMass(),p));
			body2.addLinearVelocity(Vector3d.scale(body2.getInvMass(),p));
			body1.addAngularVelocity(body1.getInvInertiaMatrix().transform(Vector3d.cross(rA,p)).negate());
			body2.addAngularVelocity(body2.getInvInertiaMatrix().transform(Vector3d.cross(rB,p)));
			double maxFriction = combinedFriction*c.normalImpulse;

			dv = new Vector3d(body2.getLinearVel());
			dv.sub(body1.getLinearVel());
			dv.add(Vector3d.cross(body2.getAngularVel(),rB));
			dv.sub(Vector3d.cross(body1.getAngularVel(),rA));
			double vt1 = dv.dot(c.tangent1);
			lambda = -vt1*c.frictionMass1;
			if(Globals.accumulateImpulse) {
				double temp = c.frictionImpluse1;
				c.frictionImpluse1=ScalarUtils.clamp(c.frictionImpluse1+lambda,-maxFriction,maxFriction);
				lambda = c.frictionImpluse1-temp;
			} else {
				lambda = ScalarUtils.clamp(lambda,-maxFriction,maxFriction);
			}
			p = Vector3d.scale(lambda,c.tangent1);
			body1.addLinearVelocity(Vector3d.scale(-body1.getInvMass(),p));
			body2.addLinearVelocity(Vector3d.scale(body2.getInvMass(),p));
			body1.addAngularVelocity(body1.getInvInertiaMatrix().transform(Vector3d.cross(rA,p)).negate());
			body2.addAngularVelocity(body2.getInvInertiaMatrix().transform(Vector3d.cross(rB,p)));

			dv = new Vector3d(body2.getLinearVel());
			dv.sub(body1.getLinearVel());
			dv.add(Vector3d.cross(body2.getAngularVel(),rB));
			dv.sub(Vector3d.cross(body1.getAngularVel(),rA));
			double vt2 = dv.dot(c.tangent2);
			lambda = -vt2*c.frictionMass2;
			if(Globals.accumulateImpulse) {
				double temp = c.frictionImpluse2;
				c.frictionImpluse2=ScalarUtils.clamp(c.frictionImpluse2+lambda,-maxFriction,maxFriction);
				lambda = c.frictionImpluse2-temp;
			} else {
				lambda = ScalarUtils.clamp(lambda,-maxFriction,maxFriction);
			}
			p = Vector3d.scale(lambda,c.tangent2);
			body1.addLinearVelocity(Vector3d.scale(-body1.getInvMass(),p));
			body2.addLinearVelocity(Vector3d.scale(body2.getInvMass(),p));
			body1.addAngularVelocity(body1.getInvInertiaMatrix().transform(Vector3d.cross(rA,p)).negate());
			body2.addAngularVelocity(body2.getInvInertiaMatrix().transform(Vector3d.cross(rB,p)));
		}
	}

	public void solvePosition() {
		for(Contact c : points) {
			Vector3d rA = Vector3d.sub(c.globalPointA,body1.getPosition());
			Vector3d rB = Vector3d.sub(c.globalPointB,body2.getPosition());
			Vector3d dv = new Vector3d(body2.getLinearVel());
			dv.sub(body1.getLinearVel());
			dv.add(Vector3d.cross(body2.getAngularVel(),rB));
			dv.sub(Vector3d.cross(body1.getAngularVel(),rA));
			Vector3d vn = body1.getInvInertiaMatrix().transform(VectorUtils.crossABA(rA,c.normal));
			vn.add(body2.getInvInertiaMatrix().transform(VectorUtils.crossABA(rB,c.normal)));
			double m = (body1.getInvMass()+body2.getInvMass());
			c.normalMass = 1f/(m+Math.abs(vn.dot(c.normal)));
			Vector3d d = new Vector3d(body2.getPosition());
			d.sub(body1.getPosition());
			d.add(rB);
			d.sub(rA);
			double lambda = vn.dot(c.normal);
			Vector3d p = Vector3d.scale(lambda,c.normal);
			body1.addPosition(Vector3d.scale(-body1.getInvMass(),p));
			body2.addPosition(Vector3d.scale(body2.getInvMass(),p));
			body1.rotate(body1.getInvInertiaMatrix().transform(Vector3d.cross(rA,p)).negate());
			body2.rotate(body2.getInvInertiaMatrix().transform(Vector3d.cross(rB,p)));
		}
	}
	
	public Vector3d getPrevSeperatingAxis() {
		return prevSeperatingAxis;
	}

	public void setSeperatingAxis(Vector3d axis) {
		prevSeperatingAxis.set(axis);
	}

	public boolean isPersistent() {
		return persistent;
	}

	public boolean isInIsland() {
		return isInIsland;
	}

	public void setInIsland(boolean isInisland) {
		this.isInIsland = isInisland;
	}
}