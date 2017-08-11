package sjmhrp.physics.collision.narrowphase;

import sjmhrp.io.Log;
import sjmhrp.linear.Transform;
import sjmhrp.physics.collision.RaycastResult;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.physics.dynamics.Ray;
import sjmhrp.physics.shapes.StaticTriMesh;
import sjmhrp.physics.shapes.TriangleShape;

public class TriMeshRayCollider implements RaycastAlgorithm {

	@Override
	public RaycastResult raycast(Ray ray, CollisionBody body, Transform t) {
		StaticTriMesh mesh = (StaticTriMesh)body.getCollisionShape();
		RaycastResult closest = null;
		for(TriangleShape triangle : mesh.query(ray)) {
			RaycastResult result = new RaycastResult();
			GJKRay gjk = new GJKRay(ray,triangle,new Transform(),result);
			try {
				gjk.process();
			} catch(Exception e) {
				Log.printError(e);
			}
			if((closest==null||result.getDistance()<closest.getDistance())&&result.collides())closest=result;
		}
		return closest==null?new RaycastResult():closest;
	}
}