package sjmhrp.physics.collision;

import sjmhrp.linear.Vector3d;

public class RaycastResult {

	final Vector3d normal = new Vector3d();
	final Vector3d hitPoint = new Vector3d();
	double distance;
	
	boolean collides = false;
	
	public void setOutput(Vector3d normal, Vector3d hitPoint, double distance) {
		collides=true;
		this.normal.set(normal);
		this.hitPoint.set(hitPoint);
		this.distance = distance;
	}
	
	public Vector3d getNormal() {
		return normal;
	}
	
	public Vector3d getHitPoint() {
		return hitPoint;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public boolean collides() {
		return collides;
	}
	
	@Override
	public String toString() {
		return "RaycastResult[Normal: "+normal+", HitPoint: "+hitPoint+", Distance: "+distance+"]";
	}
}