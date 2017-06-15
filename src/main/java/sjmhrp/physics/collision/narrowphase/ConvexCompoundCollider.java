package sjmhrp.physics.collision.narrowphase;

import sjmhrp.io.Log;
import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.collision.Contact;
import sjmhrp.physics.collision.Manifold;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.physics.shapes.CompoundShape;
import sjmhrp.physics.shapes.ConvexShape;

public class ConvexCompoundCollider extends NarrowPhaseAlgorithm {

	public ConvexCompoundCollider(Manifold m) {
		this.m=m;
	}
	
	@Override
	public boolean collide(Transform t1, Transform t2) {
		CollisionBody b1 = m.body1;
		CollisionBody b2 = m.body2;
		Transform convexTransform;
		Transform compoundTransform;
		int order = 0;
		if(b1.getCollisionShape() instanceof ConvexShape) {
			convexTransform=t1;
			compoundTransform=t2;
		} else {
			b1=m.body2;
			b2=m.body1;
			convexTransform=t2;
			compoundTransform=t1;
			order=1;
		}
		ConvexShape c1 = (ConvexShape)b1.getCollisionShape();
		CompoundShape c2 = (CompoundShape)b2.getCollisionShape();
		m.setSeperatingAxis(Vector3d.sub(t1.position,t2.position).getUnit());
		boolean collides = false;
		for(ConvexShape s : c2.getShapes()) {
			Contact c = new Contact();
			Transform t = Transform.mul(compoundTransform,c2.getOffset(s));
			GJK gjk = order==0?new GJK(c1,s,convexTransform,t,c):new GJK(s,c1,t,convexTransform,c);
			try {
				gjk.process(m.getPrevSeperatingAxis());
			} catch(Exception e) {
				Log.printError(e);
			}
			if(order==0) {
				c2.getOffset(s).transform(c.localPointB);
			} else {
				c2.getOffset(s).transform(c.localPointA);
			}
			if(c.collides)m.addContact(c);
			collides|=c.collides;
		}
		return collides;
	}
}