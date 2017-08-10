package sjmhrp.linear;

import java.io.Serializable;

public class Vector4d implements Serializable{
	
	private static final long serialVersionUID = 4055110419647812882L;
	
	public double x,y,z,w;
	
	public Vector4d() {}
	
	public Vector4d(double d) {
		set(d,d,d,d);
	}
	
	public Vector4d(double x, double y, double z, double w) {
		set(x,y,z,w);
	}
	
	public Vector4d(Vector4d v) {
		set(v.x,v.y,v.z,v.w);
	}
	
	public Vector4d set(Vector4d v) {
		x=v.x;
		y=v.y;
		z=v.z;
		w=v.w;
		return this;
	}
	
	public Vector4d set(double x, double y, double z, double w) {
		this.x=x;
		this.y=y;
		this.z=z;
		this.w=w;
		return this;
	}
	
	public Vector4d zero() {
		return set(0,0,0,0);
	}
	
	public Vector4d add(Vector4d v) {
		x+=v.x;
		y+=v.y;
		z+=v.z;
		w+=v.w;
		return this;
	}
	
	public Vector4d sub(Vector4d v) {
		x-=v.x;
		y-=v.y;
		z-=v.z;
		w-=v.w;
		return this;
	}
	
	public Vector4d scale(double d) {
		x*=d;
		y*=d;
		z*=d;
		w*=d;
		return this;
	}
	
	public Vector4d scale(Vector4d v) {
		x*=v.x;
		y*=v.y;
		z*=v.z;
		w*=v.w;
		return this;
	}
	
	public Vector4d abs() {
		x=Math.abs(x);
		y=Math.abs(y);
		z=Math.abs(z);
		w=Math.abs(w);
		return this;
	}
	
	public Vector4d getAbs() {
		return new Vector4d(this).abs();
	}
	
	public Vector4d negate() {
		return set(-x,-y,-z,-w);
	}
	
	public Vector4d getNegative() {
		return new Vector4d(this).negate();
	}
	
	public Vector4d normalize() {
		double l = length();
		if(l==0)throw new IllegalStateException("Cannot Normalize Zero Vector");
		return scale(1/l);
	}
	
	public Vector4d getUnit() {
		return new Vector4d(this).normalize();
	}
	
	public double dot(Vector4d v) {
		return x*v.x+y*v.y+z*v.z+w*v.w;
	}
	
	public double lengthSquared() {
		return dot(this);
	}
	
	public double length() {
		return Math.sqrt(lengthSquared());
	}
	
	public static Vector4d add(Vector4d u, Vector4d v) {
		return new Vector4d(u).add(v);
	}

	public static Vector4d sub(Vector4d u, Vector4d v) {
		return new Vector4d(u).sub(v);
	}
	
	public static Vector4d scale(double d, Vector4d v) {
		return new Vector4d(v).scale(d);
	}
	
	public static Vector4d scale(Vector4d u, Vector4d v) {
		return new Vector4d(u).scale(v);
	}
	
	public static double dot(Vector4d u, Vector4d v) {
		return u.dot(v);
	}
	
	@Override
	public String toString() {
		return "Vector4d["+x+", "+y+", "+z+", "+w+"]";
	}
}