package sjmhrp.utils;

import static java.lang.Math.abs;
import static sjmhrp.utils.linear.Vector3d.add;
import static sjmhrp.utils.linear.Vector3d.cross;
import static sjmhrp.utils.linear.Vector3d.scale;
import static sjmhrp.utils.linear.Vector3d.sub;

import sjmhrp.physics.collision.broadphase.AABB;
import sjmhrp.physics.dynamics.Ray;
import sjmhrp.utils.linear.Vector2d;
import sjmhrp.utils.linear.Vector3d;

public class GeometryUtils {

	public static double distance(Vector3d a, Vector3d b) {
		return Vector3d.sub(a,b).length();
	}

	public static double getAngle(Vector3d a, Vector3d b) {
		return Math.acos(a.getUnit().dot(b.getUnit()));
	}
	
	public static Vector3d minAxis(Vector3d v) {
		return getAxis(leastSignificantComponent(v));
	}
	
	public static Vector3d getAxis(int n) {
		return new Vector3d(n==0?1:0,n==1?1:0,n==2?1:0);
	}
	
	public static int leastSignificantComponent(Vector3d u) {
		Vector3d v = new Vector3d(u).abs();
		return v.x<v.y?v.x<v.z?0:2:v.y<v.z?1:2;
	}
	
	public static double sphereSDF(Vector3d pos, Vector3d origin, double radius) {
		return distance(pos,origin)-radius;
	}
	
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

	public static void orthonormalize(Vector3d n, Vector3d t) {
		n.normalize();
		t.sub(Vector3d.scale(t.dot(n),n));
		t.normalize();
	}
	
	public static Vector3d closest(AABB a, Vector3d p) {
		return VectorUtils.clamp(p,a.getMin(),a.getMax());
	}
	
	public static Vector3d closest(Vector3d min, double size, Vector3d p) {
		return VectorUtils.clamp(p,min,new Vector3d(size-1).add(min));
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

	public static boolean contains(AABB a, Vector3d p) {
		if(p.x<a.getMin().x) return false;
		if(p.y<a.getMin().y) return false;
		if(p.z<a.getMin().z) return false;
		if(p.x>a.getMax().x) return false;
		if(p.y>a.getMax().y) return false;
		if(p.z>a.getMax().z) return false;
		return true;
	}
	
	public static boolean contains(Vector3d min, double size, Vector3d p) {
		if(p.x<min.x)return false;
		if(p.y<min.y)return false;
		if(p.z<min.z)return false;
		if(p.x>=min.x+size)return false;
		if(p.y>=min.y+size)return false;
		if(p.z>=min.z+size)return false;
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

	public static boolean intersects(Ray r, AABB a) {
		double xmin,xmax,ymin,ymax,zmin,zmax;
		xmin = (a.getBounds()[r.getSign()[0]].x-r.getOrigin().x)*r.getInvdir().x;
		xmax = (a.getBounds()[1-r.getSign()[0]].x-r.getOrigin().x)*r.getInvdir().x;
		ymin = (a.getBounds()[r.getSign()[1]].y-r.getOrigin().y)*r.getInvdir().y;
		ymax = (a.getBounds()[1-r.getSign()[1]].y-r.getOrigin().y)*r.getInvdir().y;
		if (xmin>ymax||ymin>xmax)return false; 
		if (ymin>xmin)xmin=ymin;
		if (ymax<xmax)xmax=ymax; 
		zmin = (a.getBounds()[r.getSign()[2]].z-r.getOrigin().z)*r.getInvdir().z;
		zmax = (a.getBounds()[1-r.getSign()[2]].z-r.getOrigin().z)*r.getInvdir().z;
		if (xmin>zmax||zmin>xmax)return false; 
		if (zmin>xmin)xmin=zmin; 
		if (zmax<xmax)xmax=zmax;
		if(xmin<0&&xmax<0)return false;
		return true; 
	}
	
	public static double barycentric(Vector3d p1, Vector3d p2, Vector3d p3, Vector2d pos) {
		double det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		double l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		double l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		double l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	
	public static double quadArea(Vector3d p1, Vector3d p2, Vector3d p3, Vector3d p4) {
		return 0.5*(abs(cross(sub(p1,p2),sub(p3,p2)).length())+abs(cross(sub(p3,p4),sub(p1,p4)).length()));
	}
	
	public static double polygonArea(Vector2d... vertices) {
		double t = 0;
		for(int i = 0; i < vertices.length-1; i++) {
			t+=vertices[i].x*vertices[i+1].y;
			t-=vertices[i+1].x*vertices[i].y;
		}
		t+=vertices[vertices.length-1].x*vertices[0].y;
		t-=vertices[0].x*vertices[vertices.length-1].y;
		t*=0.5;
		return t;
	}
	
	public static double areaPlaneProj(Vector3d p1, Vector3d p2, Vector3d p3, Vector3d p4, Vector3d normal) {
		return quadArea(projectOntoPlane(p1,normal),projectOntoPlane(p2,normal),projectOntoPlane(p3,normal),projectOntoPlane(p4,normal));
	}
	
	public static Vector3d projectOntoPlane(Vector3d point, Vector3d normal) {
		return projectOntoPlane(point,normal,new Vector3d());
	}
	
	public static Vector3d projectOntoPlane(Vector3d point, Vector3d normal, Vector3d p) {
		return scale(sub(p,point).dot(normal)/normal.lengthSquared(),normal).add(point);
	}
	
	public static Vector3d mid(Vector3d a, Vector3d b) {
		return add(a,b).scale(0.5);
	}
}