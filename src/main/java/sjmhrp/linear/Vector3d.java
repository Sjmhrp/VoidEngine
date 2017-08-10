package sjmhrp.linear;

import java.io.Serializable;

public class Vector3d implements Serializable{
	
	private static final long serialVersionUID = -2600152262788331002L;
	
	public double x,y,z;

	public Vector3d() {}

	public Vector3d(double d) {
		set(d,d,d);
	}

	public Vector3d(double x, double y, double z) {
		set(x,y,z);
	}

	public Vector3d(Vector3d v) {
		set(v.x,v.y,v.z);
	}

	public Vector3d set(Vector3d v) {
		x=v.x;
		y=v.y;
		z=v.z;
		return this;
	}
	
	public Vector3d set(double x, double y, double z) {
		this.x=x;
		this.y=y;
		this.z=z;
		return this;
	}

	public Vector3d zero() {
		return set(0,0,0);
	}

	public Vector3d add(Vector3d v) {
		x+=v.x;
		y+=v.y;
		z+=v.z;
		return this;
	}

	public Vector3d sub(Vector3d v) {
		x-=v.x;
		y-=v.y;
		z-=v.z;
		return this;
	}

	public Vector3d scale(double d) {
		x*=d;
		y*=d;
		z*=d;
		return this;
	}

	public Vector3d scale(Vector3d v) {
		x*=v.x;
		y*=v.y;
		z*=v.z;
		return this;
	}
	
	public Vector3d abs() {
		x=Math.abs(x);
		y=Math.abs(y);
		z=Math.abs(z);
		return this;
	}
	
	public Vector3d getAbs() {
		return new Vector3d(this).abs();
	}
	
	public Vector3d negate() {
		return set(-x,-y,-z);
	}
	
	public Vector3d getNegative() {
		return new Vector3d(this).negate();
	}
	
	public Vector3d normalize() {
		double l = length();
		if(l==0)throw new IllegalStateException("Cannot Normalize the Zero Vector");
		return scale(1/l);
	}
	
	public Vector3d getUnit() {
		return new Vector3d(this).normalize();
	}
	
	public double dot(Vector3d v) {
		return x*v.x+y*v.y+z*v.z;
	}

	public double lengthSquared() {
		return dot(this);
	}

	public double length() {
		return Math.sqrt(lengthSquared());
	}
	
	public static Vector3d add(Vector3d u, Vector3d v) {
		return new Vector3d(u).add(v);
	}
	
	public static Vector3d sub(Vector3d u, Vector3d v) {
		return new Vector3d(u).sub(v);
	}
	
	public static Vector3d scale(double d, Vector3d v) {
		return new Vector3d(v).scale(d);
	}
	
	public static Vector3d scale(Vector3d u, Vector3d v) {
		return new Vector3d(u).scale(v);
	}
	
	public static Vector3d cross(Vector3d u, Vector3d v) {
		return new Vector3d(u.y*v.z-u.z*v.y,v.x*u.z-v.z*u.x,u.x*v.y-u.y*v.x);
	}
	
	public static double dot(Vector3d u, Vector3d v) {
		return u.dot(v);
	}
	
	@Override
	public String toString() {
		return "Vector3d["+x+", "+y+", "+z+"]";
	}
}