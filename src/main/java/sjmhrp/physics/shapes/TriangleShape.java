package sjmhrp.physics.shapes;

import sjmhrp.linear.Matrix4d;
import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.collision.broadphase.AABB;
import sjmhrp.utils.GeometryUtils;

public class TriangleShape extends ConvexShape{
	
	public Vector3d p1,p2,p3;
	
	public TriangleShape(Vector3d p1, Vector3d p2, Vector3d p3) {
		this.p1=p1;
		this.p2=p2;
		this.p3=p3;
	}

	@Override
	public Vector3d getLocalSupportPointWithoutMargin(Vector3d d) {
		Vector3d closest = p1;
		double dot = p1.dot(d);
		double n = p2.dot(d);
		if(n>dot) {
			closest=p2;
			dot=n;
		}
		n=p3.dot(d);
		if(n>dot) {
			closest=p3;
			dot=n;
		}
		return new Vector3d(closest);
	}

	@Override
	public AABB getBoundingBox(Transform t) {
		return GeometryUtils.combine(GeometryUtils.combine(new AABB(p1,p1),new AABB(p2,p2)),new AABB(p3,p3));
	}

	@Override
	public Vector3d calculateLocalInertia(double mass) {
		return null;
	}

	@Override
	public String getName() {
		return "TRIANGLE";
	}

	@Override
	public Matrix4d getSkewMatrix() {
		return new Matrix4d().setIdentity();
	}
}