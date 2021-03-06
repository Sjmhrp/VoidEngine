package sjmhrp.physics.shapes;

import sjmhrp.physics.collision.broadphase.AABB;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Transform;
import sjmhrp.utils.linear.Vector3d;

public class CapsuleShape extends ConvexShape {

	private static final long serialVersionUID = 5745793098498253367L;

	public CapsuleShape(double radius, double height) {
		implicitShapeDimensions.set(radius,height/2d,radius);
		setMargin(radius);
	}

	@Override
	public Vector3d getLocalSupportPointWithoutMargin(Vector3d d) {
		return new Vector3d(0,Math.signum(d.y)*implicitShapeDimensions.y,0);
	}

	@Override
	public AABB getBoundingBox(Transform t) {
		Vector3d position = t.position;
		Vector3d radius = new Vector3d(getRadius(),getHeight()/2d,getRadius());
		t.orientation.getRotationMatrix().to3Matrix().abs().transform(radius);
		Vector3d min = Vector3d.sub(position,radius);
		Vector3d max = Vector3d.add(position,radius);
		return new AABB(min,max);
	}

	@Override
	public Vector3d calculateLocalInertia(double mass) {
		double h = getHeight();
		double r = getRadius();
		double h2 = h*h;
		double r2 = r*r;
		double hr = h*r;
		double mCy = h*mass/(h+4/3d*r);
		double mS = mass-mCy;
		double a = mCy*(h2/12+0.25*r2)+mS*(0.4*r2+0.5*h2+0.375*hr);
		double b = 0.5*mCy*r2+0.4*mS*r2;
		return new Vector3d(a,b,a);
	}

	@Override
	public Matrix4d getSkewMatrix() {
		return new Matrix4d(new Vector3d(getRadius(),getHeight()/4d,getRadius())).scale(localScaling);
	}

	public double getHeight() {
		return 2*(implicitShapeDimensions.y+getRadius());
	}

	public double getRadius() {
		return implicitShapeDimensions.x;
	}

	public double getVolume() {
		double r = implicitShapeDimensions.x;
		double h = implicitShapeDimensions.y*2;
		return Math.PI*r*r*(h+4d/3d*r);
	}
	
	@Override
	public String getName() {
		return "CAPSULE";
	}
}