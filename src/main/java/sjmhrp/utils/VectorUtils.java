package sjmhrp.utils;

import sjmhrp.linear.Vector2d;
import sjmhrp.linear.Vector3d;
import sjmhrp.linear.Vector4d;

public class VectorUtils {

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

	public static Vector3d crossABA(Vector3d a, Vector3d b) {
		return Vector3d.scale(a.lengthSquared(),b).sub(Vector3d.scale(a.dot(b),a));
	}

	public static int leastSignificantComponent(Vector3d u) {
		Vector3d v = new Vector3d(u).abs();
		return v.x<v.y?v.x<v.z?0:2:v.y<v.z?1:2;
	}
	
	public static Vector2d lerp(Vector2d a, Vector2d b, double f) {
		return new Vector2d(ScalarUtils.lerp(a.x,b.x,f),ScalarUtils.lerp(a.y,b.y,f));
	}
	
	public static Vector3d lerp(Vector3d a, Vector3d b, double f) {
		return new Vector3d(ScalarUtils.lerp(a.x,b.x,f),ScalarUtils.lerp(a.y,b.y,f),ScalarUtils.lerp(a.z,b.z,f));
	}
	
	public static Vector4d lerp(Vector4d a, Vector4d b, double f) {
		return new Vector4d(ScalarUtils.lerp(a.x,b.x,f),ScalarUtils.lerp(a.y,b.y,f),ScalarUtils.lerp(a.z,b.z,f),ScalarUtils.lerp(a.w,b.w,f));
	}
}