package sjmhrp.physics.collision.narrowphase;

import sjmhrp.io.Log;
import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.collision.Contact;
import sjmhrp.physics.collision.Manifold;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.physics.shapes.ConvexShape;
import sjmhrp.physics.shapes.StaticTriMesh;
import sjmhrp.physics.shapes.TriangleShape;

public class ConvexTriMeshCollider extends NarrowPhaseAlgorithm{

	public ConvexTriMeshCollider(Manifold m) {
		this.m=m;
	}

	@Override
	public boolean collide(Transform t1, Transform t2) {
		CollisionBody b1;
		CollisionBody b2;
		Transform convexTransform;
		Transform staticTransform;
		int order = 0;
		if(m.body1.isConvex()) {
			b1=m.body1;
			b2=m.body2;
			convexTransform=t1;
			staticTransform=t2;
		} else {
			b2=m.body1;
			b1=m.body2;
			staticTransform=t1;
			convexTransform=t2;
			order=1;
		}
		m.setSeperatingAxis(Vector3d.sub(convexTransform.position,staticTransform.position).getUnit());
		ConvexShape c1 = (ConvexShape)b1.getCollisionShape();
		StaticTriMesh c2 = (StaticTriMesh)b2.getCollisionShape();
		boolean collides = false;
		for(TriangleShape t : c2.query(c1.getBoundingBox(convexTransform))) {
			Contact c = new Contact();
			GJK gjk = order==0?new GJK(c1,t,convexTransform,new Transform(),c):new GJK(t,c1,new Transform(),convexTransform,c);
			try {
				gjk.process(m.getPrevSeperatingAxis());
			} catch(Exception e) {
				Log.printError(e);
			}
			if(c.collides)m.addContact(c);
			collides|=c.collides;
		}
		return collides;
	}
}