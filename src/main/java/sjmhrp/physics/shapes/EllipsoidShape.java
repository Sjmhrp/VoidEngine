package sjmhrp.physics.shapes;

import sjmhrp.linear.Matrix4d;
import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.collision.broadphase.AABB;

public class EllipsoidShape extends ConvexShape {

	public EllipsoidShape(double x, double y, double z) {
		implicitShapeDimensions.set(x,y,z);
	}

	public EllipsoidShape(Vector3d radius) {
		implicitShapeDimensions.set(radius);
	}

	@Override
	public Vector3d getLocalSupportPointWithoutMargin(Vector3d d) {
		return d.getUnit().scale(getRadius());
	}

	@Override
	public AABB getBoundingBox(Transform t) {
		Vector3d position = t.position;
		Vector3d radius = getRadius();
		t.orientation.getRotationMatrix().to3Matrix().abs().transform(radius);
		Vector3d min = Vector3d.sub(position,radius);
		Vector3d max = Vector3d.add(position,radius);
		return new AABB(min,max);
	}

	@Override
	public Vector3d calculateLocalInertia(double mass) {
		Vector3d r = getRadius();
		double x2 = r.x*r.x;
		double y2 = r.y*r.y;
		double z2 = r.z*r.z;
		Vector3d v = new Vector3d(y2+z2,x2+z2,x2+y2);
		return v.scale(0.2f*mass);
	}

	public Vector3d getRadius() {
		return new Vector3d(implicitShapeDimensions).scale(localScaling);
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