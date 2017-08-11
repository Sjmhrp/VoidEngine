package sjmhrp.physics.collision.narrowphase;

import sjmhrp.io.Log;
import sjmhrp.linear.Transform;
import sjmhrp.physics.collision.RaycastResult;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.physics.dynamics.Ray;
import sjmhrp.physics.shapes.ConvexShape;

public class ConvexRayCollider implements RaycastAlgorithm{
	
	@Override
	public RaycastResult raycast(Ray ray, CollisionBody body, Transform t) {
		RaycastResult result = new RaycastResult();
		GJKRay gjk = new GJKRay(ray,(ConvexShape)body.getCollisionShape(),t,result);
		try {
			gjk.process();
		} catch(Exception e) {
			Log.printError(e);
		}
		return result;
	}
}