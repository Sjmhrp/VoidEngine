package sjmhrp.physics.collision.broadphase;

import sjmhrp.linear.Vector3d;

public class AABB {

	private Vector3d min = new Vector3d();
	private Vector3d max = new Vector3d();
	private Vector3d center = new Vector3d();
	private Vector3d radius = new Vector3d();

	public AABB(Vector3d min, Vector3d max) {
		radius = new Vector3d();
		radius.x = max.x-min.x;
		radius.y = max.y-min.y;
		radius.z = max.z-min.z;
		radius.x/=2;
		radius.y/=2;
		radius.z/=2;
		center.x = min.x+radius.x;
		center.y = min.y+radius.y;
		center.z = min.z+radius.z;
		this.min = min;
		this.max = max;
	}

	public void update() {
		min.x=center.x-radius.x;
		min.y=center.y-radius.y;
		min.z=center.z-radius.z;
		max.x=center.x+radius.x;
		max.y=center.y+radius.y;
		max.z=center.z+radius.z;
	}

	public void scale(double x, double y, double z) {
		radius.x*=x;
		radius.y*=y;
		radius.z*=z;
	}

	public double getVolume() {
		return 8*radius.x*radius.y*radius.z;
	}

	public double getArea() {
		return 2*(radius.x*radius.y+radius.x*radius.z+radius.y*radius.z);
	}

	public void setCenter(Vector3d center) {
		this.center = center;
	}

	public Vector3d getCenter() {
		return center;
	}

	public Vector3d getRadius() {
		return radius;
	}

	public Vector3d getMin() {
		return min;
	}

	public Vector3d getMax() {
		return max;
	}

	@Override
	public String toString() {
		return min+";"+max;
	}
}