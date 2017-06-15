package sjmhrp.utils;

import sjmhrp.linear.Vector3d;

public class VectorUtils {

	public static double distance(Vector3d a, Vector3d b) {
		return Vector3d.sub(a,b).length();
	}

	public static Vector3d minAxis(Vector3d v) {
		return getAxis(leastSignificantComponent(v));
	}
	
	public static Vector3d getAxis(int n) {
		return new Vector3d(n==0?1:0,n==1?1:0,n==2?1:0);
	}

	public static Vector3d crossABA(Vector3d a, Vector3d b) {
		return Vector3d.scale(a.lengthSquared(),b).sub(Vector3d.scale(a.dot(b),a));
	}

	public static int leastSignificantComponent(Vector3d u) {
		Vector3d v = new Vector3d(u).abs();
		return v.x<v.y?v.x<v.z?0:2:v.y<v.z?1:2;
	}
}