package sjmhrp.physics.collision.narrowphase;

import sjmhrp.io.Log;
import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.collision.Contact;
import sjmhrp.physics.collision.Manifold;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.physics.shapes.CompoundShape;
import sjmhrp.physics.shapes.ConvexShape;
import sjmhrp.physics.shapes.StaticTriMesh;
import sjmhrp.physics.shapes.TriangleShape;

public class CompoundTriMeshCollider extends NarrowPhaseAlgorithm{

	public CompoundTriMeshCollider(Manifold m) {
	this.m=m;
	}
	
	@Override
	public boolean collide(Transform t1, Transform t2) {
		CollisionBody b1;
		CollisionBody b2;
		Transform compoundTransform;
		Transform staticTransform;
		int order = 0;
		if(m.body1.isConvex()) {
			b1=m.body1;
			b2=m.body2;
			compoundTransform=t1;
			staticTransform=t2;
		} else {
			b2=m.body1;
			b1=m.body2;
			staticTransform=t1;
			compoundTransform=t2;
			order=1;
		}
		m.setSeperatingAxis(Vector3d.sub(compoundTransform.position,staticTransform.position).getUnit());
		CompoundShape c1 = (CompoundShape)b1.getCollisionShape();
		StaticTriMesh c2 = (StaticTriMesh)b2.getCollisionShape();
		boolean collides = false;
		for(ConvexShape s : c1.getShapes()) {
			Transform trans = Transform.mul(compoundTransform,c1.getOffset(s));
			for(TriangleShape t : c2.query(s.getBoundingBox(trans))) {
				Contact c = new Contact();
				GJK gjk = order==0?new GJK(s,t,trans,new Transform(),c):new GJK(t,s,new Transform(),trans,c);
				try {
					gjk.process(m.getPrevSeperatingAxis());
				} catch(Exception e) {
					Log.printError(e);
				}
				if(order==0) {
					c1.getOffset(s).transform(c.localPointA);
				} else {
					c1.getOffset(s).transform(c.localPointB);
				}
				if(c.collides)m.addContact(c);
				collides|=c.collides;
			}
		}
		return collides;
	}
}