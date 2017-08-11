package sjmhrp.physics.collision.narrowphase;

import sjmhrp.io.Log;
import sjmhrp.linear.Transform;
import sjmhrp.physics.collision.RaycastResult;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.physics.dynamics.Ray;
import sjmhrp.physics.shapes.CompoundShape;
import sjmhrp.physics.shapes.ConvexShape;

public class CompoundRayCollider implements RaycastAlgorithm {

	@Override
	public RaycastResult raycast(Ray ray, CollisionBody body, Transform t) {
		CompoundShape shape = (CompoundShape)body.getCollisionShape();
		RaycastResult closest = null;
		for(ConvexShape c : shape.getShapes()) {
			RaycastResult result = new RaycastResult();
			GJKRay gjk = new GJKRay(ray,c,Transform.mul(t,shape.getOffset(c)),result);
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