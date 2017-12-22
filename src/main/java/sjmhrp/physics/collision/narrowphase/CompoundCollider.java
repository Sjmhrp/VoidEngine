package sjmhrp.physics.collision.narrowphase;

import sjmhrp.io.Log;
import sjmhrp.physics.collision.Contact;
import sjmhrp.physics.collision.Manifold;
import sjmhrp.physics.shapes.CompoundShape;
import sjmhrp.physics.shapes.ConvexShape;
import sjmhrp.utils.linear.Transform;
import sjmhrp.utils.linear.Vector3d;

public class CompoundCollider extends NarrowPhaseAlgorithm{

	public CompoundCollider(Manifold m) {
		this.m=m;
	}
	
	@Override
	public boolean collide(Transform t1, Transform t2) {
		boolean collides = false;
		CompoundShape c1 = (CompoundShape)m.body1.getCollisionShape();
		CompoundShape c2 = (CompoundShape)m.body2.getCollisionShape();
		m.setSeperatingAxis(Vector3d.sub(t1.position,t2.position).getUnit());
		for(ConvexShape s1 : c1.getShapes()) {
			Transform trans1 = Transform.mul(t1,c1.getOffset(s1));
			for(ConvexShape s2 : c2.getShapes()) {
				Transform trans2 = Transform.mul(t2,c2.getOffset(s2));
				Contact c = new Contact();
				GJK gjk = new GJK(s1,s2,trans1,trans2,c);
				try {
					gjk.process(m.getPrevSeperatingAxis());
				} catch(Exception e) {
					Log.printError(e);
				}
				c1.getOffset(s1).transform(c.localPointA);
				c2.getOffset(s2).transform(c.localPointB);
				if(c.collides)m.addContact(c);
				collides|=c.collides;
			}
		}
		return collides;
	}
}