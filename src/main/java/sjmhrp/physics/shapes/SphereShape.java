package sjmhrp.physics.shapes;

import static java.lang.Math.PI;

import sjmhrp.physics.collision.broadphase.AABB;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Transform;
import sjmhrp.utils.linear.Vector3d;

public class SphereShape extends ConvexShape {

	private static final long serialVersionUID = -252539298613101923L;

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
		return new Vector3d(0.4*mass*getRadius()*getRadius());
	}

	public double getRadius() {
		return implicitShapeDimensions.x*localScaling.x;
	}

	public double getDiameter() {
		return 2*getRadius();
	}
	
	public double getVolume() {
		double r = getRadius();
		return 4d/3d*PI*r*r*r;
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