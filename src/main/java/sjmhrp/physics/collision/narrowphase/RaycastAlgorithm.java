package sjmhrp.physics.collision.narrowphase;

import sjmhrp.linear.Transform;
import sjmhrp.physics.collision.RaycastResult;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.physics.dynamics.Ray;

public interface RaycastAlgorithm {

	public abstract RaycastResult raycast(Ray ray, CollisionBody body, Transform t);
}