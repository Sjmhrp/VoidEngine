package sjmhrp.physics.collision.narrowphase;

import sjmhrp.io.Log;
import sjmhrp.physics.collision.RaycastResult;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.physics.dynamics.Ray;
import sjmhrp.physics.shapes.ConvexShape;
import sjmhrp.utils.linear.Transform;

public class ConvexRayCollider implements RaycastAlgorithm{
	
	@Override
	public RaycastResult raycast(Ray ray, CollisionBody body, Transform t) {
		RaycastResult result = new RaycastResult(ray);
		GJKRay gjk = new GJKRay(ray,(ConvexShape)body.getCollisionShape(),t,result);
		try {
			gjk.process();
		} catch(Exception e) {
			Log.printError(e);
		}
		return result;
	}
}