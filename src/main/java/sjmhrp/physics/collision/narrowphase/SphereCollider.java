package sjmhrp.physics.collision.narrowphase;

import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.collision.Contact;
import sjmhrp.physics.collision.Manifold;
import sjmhrp.physics.shapes.SphereShape;

public class SphereCollider extends NarrowPhaseAlgorithm{

	public SphereCollider(Manifold m) {
		this.m=m;
	}

	@Override
	public boolean collide(Transform t1, Transform t2) {
		m.points.clear();
		Vector3d normal = Vector3d.sub(t2.position,t1.position);
		double r1 = ((SphereShape)m.body1.getCollisionShape()).getRadius();
		double r2 = ((SphereShape)m.body2.getCollisionShape()).getRadius();
		double ls = r1+r2;
		ls*=ls;
		if(normal.lengthSquared()>=ls)return false;
		double depth = (double)Math.sqrt(ls)-normal.length();
		normal.normalize();
		Vector3d globalA = new Vector3d(normal).scale(r1).add(t1.position);
		Vector3d globalB = normal.getNegative().scale(r2).add(t2.position);
		Vector3d localA = Transform.transform(t1.getInverse(),globalA);
		Vector3d localB = Transform.transform(t2.getInverse(),globalB);
		Contact c = new Contact();
		c.setOutput(true,localA,localB,globalA,globalB,normal,depth);
		m.addContact(c);
		return true;
	}
}