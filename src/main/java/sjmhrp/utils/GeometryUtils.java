package sjmhrp.utils;

import sjmhrp.linear.Vector2d;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.collision.broadphase.AABB;

public class GeometryUtils {

	public static double distanceToLineSq(Vector3d point, Vector3d a, Vector3d b) {
		return Vector3d.sub(point,closestPointOnLine(point,a,b)).lengthSquared();
	}

	public static double distanceToTriangleSq(Vector3d point, Vector3d v1, Vector3d v2, Vector3d v3) {
		return Vector3d.sub(point,closestPointOnTriangle(point,v1,v2,v3)).lengthSquared();
	}

	public static Vector3d closestPointOnTriangle(Vector3d point, Vector3d v1, Vector3d v2, Vector3d v3) {
		Vector3d p12 = closestPointOnLine(point,v1,v2);
		Vector3d p13 = closestPointOnLine(point,v1,v3);
		Vector3d p23 = closestPointOnLine(point,v2,v3);
		double l1 = Vector3d.sub(p12, point).lengthSquared();
		double l2 = Vector3d.sub(p13, point).lengthSquared();
		double l3 = Vector3d.sub(p23, point).lengthSquared();
		double min = Math.min(Math.max(l1, l2), l3);
		if(min==l1)return p12;
		if(min==l2)return p13;
		return p23;
	}

	public static Vector3d closestPointOnLine(Vector3d point, Vector3d a, Vector3d b) {
		Vector3d c = Vector3d.sub(point,a);
		Vector3d v = Vector3d.sub(b,a);
		double d = v.length();
		if(v.lengthSquared()==0)return new Vector3d(a);
		v.normalize();
		double t = v.dot(c);
		if(t<0)return a;
		if(t>d)return b;
		v.scale(t);
		return Vector3d.add(a,v);
	}

	public static boolean pointInTriangle(Vector3d point, Vector3d v1, Vector3d v2, Vector3d v3) {
		Vector3d p1 = Vector3d.sub(v1, point);
		Vector3d p2 = Vector3d.sub(v2, point);
		Vector3d p3 = Vector3d.sub(v3, point);
		if(p1.lengthSquared()==0||p2.lengthSquared()==0||p3.lengthSquared()==0)return false;
		p1.normalize();
		p2.normalize();
		p3.normalize();
		double angle = 0;
		angle+=Math.acos(p1.dot(p2));
		angle+=Math.acos(p1.dot(p3));
		angle+=Math.acos(p2.dot(p3));
		return (Math.abs(angle)-2*Math.PI)<0.005;
	}

	public static void computeBasis(Vector3d a, Vector3d b, Vector3d c) {
		if(Math.abs(a.x)>0.57735026f){
			b.set(a.y,-a.x,0);
		} else {
			b.set(0,a.z,-a.y);
		}
		b.normalize();
		c.set(Vector3d.cross(a,b));
		c.normalize();
	}

	public static AABB combine(AABB a, AABB b) {
		Vector3d min = new Vector3d();
		Vector3d max = new Vector3d();
		min.x = Math.min(a.getMin().x, b.getMin().x);
		min.y = Math.min(a.getMin().y, b.getMin().y);
		min.z = Math.min(a.getMin().z, b.getMin().z);
		max.x = Math.max(a.getMax().x, b.getMax().x);
		max.y = Math.max(a.getMax().y, b.getMax().y);
		max.z = Math.max(a.getMax().z, b.getMax().z);
		return new AABB(min,max);
	}

	public static boolean contains(AABB a, AABB b) {
		if(b.getMin().x<a.getMin().x) return false;
		if(b.getMin().y<a.getMin().y) return false;
		if(b.getMin().z<a.getMin().z) return false;
		if(b.getMax().x>a.getMax().x) return false;
		if(b.getMax().y>a.getMax().y) return false;
		if(b.getMax().z>a.getMax().z) return false;
		return true;
	}

	public static boolean intersects(AABB a, AABB b) {
		if(a.getMin().x>b.getMax().x)return false;
		if(a.getMin().y>b.getMax().y)return false;
		if(a.getMin().z>b.getMax().z)return false;
		if(b.getMin().x>a.getMax().x)return false;
		if(b.getMin().y>a.getMax().y)return false;
		if(b.getMin().z>a.getMax().z)return false;
		return true;
	}

	public static double barycentric(Vector3d p1, Vector3d p2, Vector3d p3, Vector2d pos) {
		double det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		double l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		double l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		double l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
}