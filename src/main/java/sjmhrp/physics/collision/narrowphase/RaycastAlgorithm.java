package sjmhrp.physics.collision.narrowphase;

import sjmhrp.physics.collision.RaycastResult;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.physics.dynamics.Ray;
import sjmhrp.utils.linear.Transform;

public interface RaycastAlgorithm {

	public abstract RaycastResult raycast(Ray ray, CollisionBody body, Transform t);
}