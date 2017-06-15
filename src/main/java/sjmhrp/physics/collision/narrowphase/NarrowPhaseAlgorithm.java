package sjmhrp.physics.collision.narrowphase;

import sjmhrp.linear.Transform;
import sjmhrp.physics.collision.Manifold;

public abstract class NarrowPhaseAlgorithm {

	Manifold m;

	public abstract boolean collide(Transform t1, Transform t2);
}