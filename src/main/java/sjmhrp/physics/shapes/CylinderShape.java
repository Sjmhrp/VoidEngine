package sjmhrp.physics.shapes;

import sjmhrp.linear.Matrix4d;
import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector2d;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.collision.broadphase.AABB;

public class CylinderShape extends ConvexShape {

	private static final long serialVersionUID = -3167063806837031435L;

	public CylinderShape(double radius, double height) {
		this(radius,radius,height);
	}

	public CylinderShape(double x, double z, double height) {
		implicitShapeDimensions.set(x,height*0.5,z);
	}

	@Override
	public Vector3d getLocalSupportPointWithoutMargin(Vector3d d) {
		Vector2d r = getRadius();
		double h = getHeight()*0.5;
		double l = Math.sqrt(d.x*d.x+d.z*d.z);
		double x = l==0?0:d.x/l*r.x;
		double y = Math.signum(d.y)*h;
		double z = l==0?0:d.z/l*r.y;
		return new Vector3d(x,y,z);
	}

	@Override
	public AABB getBoundingBox(Transform t) {
		Vector3d position = t.position;
		Vector2d r = getRadius();
		Vector3d radius = new Vector3d(r.x,getHeight()*0.5,r.y);
		t.orientation.getRotationMatrix().to3Matrix().abs().transform(radius);
		Vector3d min = Vector3d.sub(position,radius);
		Vector3d max = Vector3d.add(position,radius);
		return new AABB(min,max);
	}

	@Override
	public Vector3d calculateLocalInertia(double mass) {
		Vector2d radius = getRadius();
		double height = getHeight();
		double x = mass/12d*(height*height+3*radius.y*radius.y);
		double y = 0.25*mass*radius.lengthSquared();
		double z = mass/12d*(height*height+3*radius.x*radius.x);
		return new Vector3d(x,y,z);
	}

	@Override
	public Matrix4d getSkewMatrix() {
		return new Matrix4d(implicitShapeDimensions).scale(localScaling);
	}

	public Vector2d getRadius() {
		return new Vector2d(implicitShapeDimensions.x,implicitShapeDimensions.z).scale(new Vector2d(localScaling.x,localScaling.z));
	}

	public double getHeight() {
		return implicitShapeDimensions.y*localScaling.y*2;
	}

	@Override
	public String getName() {
		return "CYLINDER";
	}
}