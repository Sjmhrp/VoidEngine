package sjmhrp.physics.shapes;

import sjmhrp.linear.Matrix4d;
import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.collision.broadphase.AABB;

public abstract class CollisionShape {

	public abstract AABB getBoundingBox(Transform t);
	
	public abstract Vector3d calculateLocalInertia(double mass);

	public boolean isInfinite() {
		return false;
	}

	public abstract Matrix4d getSkewMatrix();
	
	public abstract String getName();	
}