package sjmhrp.utils;

import sjmhrp.core.Globals;
import sjmhrp.utils.linear.Vector2d;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.utils.linear.Vector4d;

public class VectorUtils {

	public static Vector3d chunkMin(Vector3d min, int size, Vector3d p) {
		int mask = ~(size-1);
		return new Vector3d((int)(p.x-min.x)&mask,(int)(p.y-min.y)&mask,(int)(p.z-min.z)&mask).add(min);
	}

	public static Vector3d chunkMin(int size, Vector3d p) {
		int mask = ~(size-1);
		return new Vector3d((int)(p.x+Globals.WORLD_SIZE)&mask,(int)(p.y+Globals.WORLD_SIZE)&mask,(int)(p.z+Globals.WORLD_SIZE)&mask).sub(new Vector3d(Globals.WORLD_SIZE));
	}
	
	public static double min(Vector2d v) {
		return Math.min(v.x,v.y);
	}
	
	public static double min(Vector3d v) {
		return Math.min(Math.min(v.x,v.y),v.z);
	}
	
	public static double min(Vector4d v) {
		return Math.min(Math.min(v.x,v.y),Math.min(v.z,v.w));
	}
	
	public static Vector2d min(Vector2d a, Vector2d b) {
		return new Vector2d(Math.min(a.x,b.x),Math.min(a.y,b.y));
	}
	
	public static Vector3d min(Vector3d a, Vector3d b) {
		return new Vector3d(Math.min(a.x,b.x),Math.min(a.y,b.y),Math.min(a.z,b.z));
	}
	
	public static Vector4d min(Vector4d a, Vector4d b) {
		return new Vector4d(Math.min(a.x,b.x),Math.min(a.y,b.y),Math.min(a.z,b.z),Math.min(a.w,b.w));
	}
	
	public static double max(Vector2d v) {
		return Math.max(v.x,v.y);
	}
	
	public static double max(Vector3d v) {
		return Math.max(Math.max(v.x,v.y),v.z);
	}
	
	public static double max(Vector4d v) {
		return Math.max(Math.max(v.x,v.y),Math.max(v.z,v.w));
	}
	
	public static Vector2d max(Vector2d a, Vector2d b) {
		return new Vector2d(Math.max(a.x,b.x),Math.max(a.y,b.y));
	}
	
	public static Vector3d max(Vector3d a, Vector3d b) {
		return new Vector3d(Math.max(a.x,b.x),Math.max(a.y,b.y),Math.max(a.z,b.z));
	}
	
	public static Vector4d max(Vector4d a, Vector4d b) {
		return new Vector4d(Math.max(a.x,b.x),Math.max(a.y,b.y),Math.max(a.z,b.z),Math.max(a.w,b.w));
	}
	
	public static Vector3d crossABA(Vector3d a, Vector3d b) {
		return Vector3d.scale(a.lengthSquared(),b).sub(Vector3d.scale(a.dot(b),a));
	}
	
	public static Vector2d clamp(Vector2d a, double min, double max) {
		return new Vector2d(ScalarUtils.clamp(a.x,min,max),ScalarUtils.clamp(a.y,min,max));
	}
	
	public static Vector3d clamp(Vector3d a, double min, double max) {
		return new Vector3d(ScalarUtils.clamp(a.x,min,max),ScalarUtils.clamp(a.y,min,max),ScalarUtils.clamp(a.z,min,max));
	}
	
	public static Vector4d clamp(Vector4d a, double min, double max) {
		return new Vector4d(ScalarUtils.clamp(a.x,min,max),ScalarUtils.clamp(a.y,min,max),ScalarUtils.clamp(a.z,min,max),ScalarUtils.clamp(a.w,min,max));
	}
	
	public static Vector2d clamp(Vector2d a, Vector2d min, Vector2d max) {
		return new Vector2d(ScalarUtils.clamp(a.x,min.x,max.x),ScalarUtils.clamp(a.y,min.y,max.y));
	}
	
	public static Vector3d clamp(Vector3d a, Vector3d min, Vector3d max) {
		return new Vector3d(ScalarUtils.clamp(a.x,min.x,max.x),ScalarUtils.clamp(a.y,min.y,max.y),ScalarUtils.clamp(a.z,min.z,max.z));
	}
	
	public static Vector4d clamp(Vector4d a, Vector4d min, Vector4d max) {
		return new Vector4d(ScalarUtils.clamp(a.x,min.x,max.x),ScalarUtils.clamp(a.y,min.y,max.y),ScalarUtils.clamp(a.z,min.z,max.z),ScalarUtils.clamp(a.w,min.w,max.w));
	}
	
	public static Vector2d clampLength(double length, Vector2d v) {
		double l = v.length();
		if(l>length&&l!=0)return new Vector2d(v).scale(length/l);
		return new Vector2d(v);
	}
	
	public static Vector3d clampLength(double length, Vector3d v) {
		double l = v.length();
		if(l>length&&l!=0)return new Vector3d(v).scale(length/l);
		return new Vector3d(v);
	}
	
	public static Vector4d clampLength(double length, Vector4d v) {
		double l = v.length();
		if(l>length&&l!=0)return new Vector4d(v).scale(length/l);
		return new Vector4d(v);
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

	public static Vector3d proj(Vector3d a, Vector3d b) {
		return Vector3d.scale(a.dot(b)/a.lengthSquared(),a);
	}
}