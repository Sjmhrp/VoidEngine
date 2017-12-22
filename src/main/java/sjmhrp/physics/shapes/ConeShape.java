package sjmhrp.physics.shapes;

import sjmhrp.physics.collision.broadphase.AABB;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Transform;
import sjmhrp.utils.linear.Vector3d;

public class ConeShape extends ConvexShape {

	private static final long serialVersionUID = -2420960660959803309L;
	
	private double sinTheta;

	public ConeShape(double radius, double height) {
		implicitShapeDimensions.set(radius,height*0.5,radius);
		sinTheta = radius / Math.sqrt(radius*radius+height*height);
	}

	@Override
	public Vector3d getLocalSupportPointWithoutMargin(Vector3d v) {
		double l = sinTheta*v.length();
		double h = 0.5*getHeight();
		if(v.y>l)return new Vector3d(0,h,0);
		double pl = Math.sqrt(v.x*v.x+v.z*v.z);
		if(pl<=0)return new Vector3d(0,-h,0);
		double d = getRadius()/pl;
		return new Vector3d(v.x*d,-h,v.z*d);
	}

	@Override
	public AABB getBoundingBox(Transform t) {
		Vector3d position = t.position;
		double r = getRadius();
		Vector3d radius = new Vector3d(r,getHeight()*0.5,r);
		t.orientation.getRotationMatrix().to3Matrix().abs().transform(radius);
		Vector3d min = Vector3d.sub(position,radius);
		Vector3d max = Vector3d.add(position,radius);
		return new AABB(min,max);
	}

	@Override
	public Vector3d calculateLocalInertia(double mass) {
		double s = getRadius()*getRadius();
		double d = 0.15*mass*(s+0.5*getHeight());
		return new Vector3d(d,0.3*mass*s,d);
	}

	@Override
	public Matrix4d getSkewMatrix() {
		return new Matrix4d(implicitShapeDimensions).scale(localScaling);
	}

	public double getHeight() {
		return implicitShapeDimensions.y*localScaling.y*2;
	}

	public double getRadius() {
		return implicitShapeDimensions.x*localScaling.x;
	}

	public double getVolume() {
		double r = getRadius();
		return Math.PI*r*r*getHeight()/3d;
	}
	
	@Override
	public String getName() {
		return "CONE";
	}
}