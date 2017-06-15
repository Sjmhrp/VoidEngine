package sjmhrp.physics.shapes;

import sjmhrp.linear.Matrix4d;
import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.collision.broadphase.AABB;

public class SphereShape extends ConvexShape {

	public SphereShape(double radius) {
		implicitShapeDimensions.x=radius;
		setMargin(radius);
	}

	@Override
	public Vector3d getLocalSupportPointWithoutMargin(Vector3d d) {
		return new Vector3d();
	}

	@Override
	public AABB getBoundingBox(Transform t) {
		Vector3d min = Vector3d.sub(t.position,new Vector3d(getRadius()));
		Vector3d max = Vector3d.add(t.position,new Vector3d(getRadius()));
		return new AABB(min,max);
	}

	@Override
	public Vector3d calculateLocalInertia(double mass) {
		return new Vector3d(0.4f*mass*getRadius()*getRadius());
	}

	public double getRadius() {
		return implicitShapeDimensions.x*localScaling.x;
	}

	@Override
	public String getName() {
		return "SPHERE";
	}

	@Override
	public Matrix4d getSkewMatrix() {
		return new Matrix4d(getRadius());
	}
}