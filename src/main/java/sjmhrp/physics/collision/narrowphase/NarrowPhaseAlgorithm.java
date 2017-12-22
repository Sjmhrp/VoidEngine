package sjmhrp.physics.collision.narrowphase;

import sjmhrp.physics.collision.Manifold;
import sjmhrp.utils.linear.Transform;

public abstract class NarrowPhaseAlgorithm {

	Manifold m;

	public abstract boolean collide(Transform t1, Transform t2);
}