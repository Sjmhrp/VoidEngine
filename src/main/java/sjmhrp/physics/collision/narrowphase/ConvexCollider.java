package sjmhrp.physics.collision.narrowphase;

import sjmhrp.io.Log;
import sjmhrp.physics.collision.Contact;
import sjmhrp.physics.collision.Manifold;
import sjmhrp.physics.shapes.ConvexShape;
import sjmhrp.utils.linear.Transform;

public class ConvexCollider extends NarrowPhaseAlgorithm{

	public ConvexCollider(Manifold m) {
		this.m=m;
	}

	@Override
	public boolean collide(Transform t1, Transform t2) {
		Contact c = new Contact();
		ConvexShape c1 = (ConvexShape)m.body1.getCollisionShape();
		ConvexShape c2 = (ConvexShape)m.body2.getCollisionShape();
		GJK gjk = new GJK(c1,c2,t1,t2,c);
		try {
			gjk.process(m.getPrevSeperatingAxis());
		} catch(Exception e) {
			Log.printError(e);
		}
		if(!c.collides)return false;
		m.setSeperatingAxis(c.normal);
		m.addContact(c);
		return true;
	}
}