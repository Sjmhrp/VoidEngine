package sjmhrp.physics.shapes;

import java.io.Serializable;

import sjmhrp.physics.collision.broadphase.AABB;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Transform;
import sjmhrp.utils.linear.Vector3d;

public abstract class CollisionShape implements Serializable {

	private static final long serialVersionUID = -8597685680203511423L;

	public abstract AABB getBoundingBox(Transform t);
	
	public abstract Vector3d calculateLocalInertia(double mass);

	public boolean isInfinite() {
		return false;
	}

	public abstract Matrix4d getSkewMatrix();
	
	public abstract String getName();	
}