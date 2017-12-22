package sjmhrp.physics.collision;

import sjmhrp.physics.collision.narrowphase.CompoundCollider;
import sjmhrp.physics.collision.narrowphase.CompoundRayCollider;
import sjmhrp.physics.collision.narrowphase.CompoundTriMeshCollider;
import sjmhrp.physics.collision.narrowphase.ConvexCollider;
import sjmhrp.physics.collision.narrowphase.ConvexCompoundCollider;
import sjmhrp.physics.collision.narrowphase.ConvexRayCollider;
import sjmhrp.physics.collision.narrowphase.ConvexTriMeshCollider;
import sjmhrp.physics.collision.narrowphase.NarrowPhaseAlgorithm;
import sjmhrp.physics.collision.narrowphase.RaycastAlgorithm;
import sjmhrp.physics.collision.narrowphase.SphereCollider;
import sjmhrp.physics.collision.narrowphase.TriMeshRayCollider;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.physics.dynamics.Ray;
import sjmhrp.utils.linear.Transform;
import sjmhrp.utils.linear.Vector3d;

public class CollisionHandler {

	public static boolean collide(Manifold m, Transform t1, Transform t2) {
		NarrowPhaseAlgorithm n = getAlgorithm(m);
		return n!=null&&n.collide(t1,t2);
	}

	public static RaycastResult raycast(Ray ray, CollisionBody body) {
		RaycastAlgorithm n = getAlgorithm(body);
		return n==null?new RaycastResult(ray):n.raycast(ray,body,body.isStaticTriMesh()?new Transform():body.getTransform()).setBody(body);
	}
	
	public static double resolveSingleBilateral(CollisionBody body1, Vector3d pos1, CollisionBody body2, Vector3d pos2, double distance, Vector3d normal) {
		if(normal.lengthSquared()>1.1)return 0;
		Vector3d relpos1 = Vector3d.sub(pos1,body1.getPosition());
		Vector3d relpos2 = Vector3d.sub(pos2,body2.getPosition());
		Vector3d vel1 = body1.getVelocityAtPoint(relpos1);
		Vector3d vel2 = body2.getVelocityAtPoint(relpos2);
		Vector3d vel = Vector3d.sub(vel1,vel2);
		Vector3d aJ=body1.getRotation().transpose().transform(Vector3d.cross(relpos1,normal));
		Vector3d bJ=body2.getRotation().transpose().transform(Vector3d.cross(relpos2,normal.getNegative()));
		Vector3d minV0=Vector3d.scale(body1.getInvInertia(),aJ);
		Vector3d minV1=Vector3d.scale(body2.getInvInertia(),bJ);
		double diag=body1.getInvMass()+minV0.dot(aJ)+body2.getInvMass()+minV1.dot(bJ);
		return -0.2*normal.dot(vel)/diag;
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
	
	static RaycastAlgorithm getAlgorithm(CollisionBody b) {
		if(b.isConvex())return new ConvexRayCollider();
		if(b.isCompound())return new CompoundRayCollider();
		if(b.isStaticTriMesh())return new TriMeshRayCollider();
		return null;
	}
}