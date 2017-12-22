package sjmhrp.physics.collision;

import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.physics.dynamics.Ray;
import sjmhrp.utils.linear.Vector3d;

public class RaycastResult {

	public CollisionBody body;
	final Vector3d rayOrigin = new Vector3d();
	final Vector3d normal = new Vector3d();
	final Vector3d hitPoint = new Vector3d();
	public double distance;
	
	public boolean collides = false;
	
	public RaycastResult() {}
	
	public RaycastResult(Ray ray) {
		this.rayOrigin.set(ray.getOrigin());
	}
	
	public RaycastResult setOutput(Vector3d normal, Vector3d hitPoint, double distance) {
		collides=true;
		this.normal.set(normal);
		this.hitPoint.set(hitPoint);
		this.distance = distance;
		return this;
	}
	
	public RaycastResult setBody(CollisionBody body) {
		this.body=body;
		return this;
	}
	
	public Vector3d getRayOrigin() {
		return rayOrigin;
	}
	
	public Vector3d normal() {
		return normal;
	}
	
	public Vector3d hitPoint() {
		return hitPoint;
	}
	
	public double distance() {
		return distance;
	}
	
	public CollisionBody body() {
		return body;
	}
	
	public boolean collides() {
		return collides;
	}
	
	@Override
	public String toString() {
		return "RaycastResult[Normal: "+normal+", HitPoint: "+hitPoint+", Distance: "+distance+"]";
	}
}