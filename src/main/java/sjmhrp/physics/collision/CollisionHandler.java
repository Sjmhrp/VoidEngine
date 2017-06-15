package sjmhrp.physics.collision;

import sjmhrp.linear.Transform;
import sjmhrp.physics.collision.narrowphase.CompoundCollider;
import sjmhrp.physics.collision.narrowphase.CompoundTriMeshCollider;
import sjmhrp.physics.collision.narrowphase.ConvexCollider;
import sjmhrp.physics.collision.narrowphase.ConvexCompoundCollider;
import sjmhrp.physics.collision.narrowphase.ConvexTriMeshCollider;
import sjmhrp.physics.collision.narrowphase.NarrowPhaseAlgorithm;
import sjmhrp.physics.collision.narrowphase.SphereCollider;

public class CollisionHandler {

	public static boolean collide(Manifold m, Transform t1, Transform t2) {
		NarrowPhaseAlgorithm n = getAlgorithm(m);
		return n!=null&&n.collide(t1,t2);
	}

	static NarrowPhaseAlgorithm getAlgorithm(Manifold m) {
		if(m.body1.isSphere()&&m.body2.isSphere())return new SphereCollider(m);
		if(m.body1.isConvex()&&m.body2.isConvex())return new ConvexCollider(m);
		if(m.body1.isCompound()&&m.body2.isCompound())return new CompoundCollider(m);
		if((m.body1.isConvex()&&m.body2.isCompound())||(m.body1.isCompound()&&m.body2.isConvex()))return new ConvexCompoundCollider(m);
		if((m.body1.isStaticTriMesh()&&m.body2.isCompound())||(m.body1.isCompound()&&m.body2.isStaticTriMesh()))return new CompoundTriMeshCollider(m);
		if((m.body1.isConvex()&&m.body2.isStaticTriMesh())||(m.body1.isStaticTriMesh()&&m.body2.isConvex()))return new ConvexTriMeshCollider(m);
		return null;
	}
}