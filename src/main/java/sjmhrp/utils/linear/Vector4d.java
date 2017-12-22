package sjmhrp.utils.linear;

import java.io.Serializable;
import java.util.Objects;

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
	
	public Vector4d(Vector2d v, double z, double w) {
		set(v.x,v.y,z,w);
	}
	
	public Vector4d(double x, Vector2d v, double w) {
		set(x,v.x,v.y,w);
	}
	
	public Vector4d(double x, double y, Vector2d v) {
		set(x,y,v.x,v.y);
	}
	
	public Vector4d(Vector2d a, Vector2d b) {
		set(a.x,a.y,b.x,b.y);
	}
	
	public Vector4d(Vector3d v, double w) {
		set(v.x,v.y,v.z,w);
	}
	
	public Vector4d(double x, Vector3d v) {
		set(x,v.x,v.y,v.z);
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
	
	public double get(int i) {
		switch(i) {
			case 0:return x;
			case 1:return y;
			case 2:return z;
			case 3:return w;
		}
		return 0;
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
	
	public Vector4d mod(double m) {
		x%=m;
		y%=m;
		z%=m;
		w%=m;
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
	
	public Vector4d reciprocal() {
		x=1d/x;
		y=1d/y;
		z=1d/z;
		w=1d/w;
		return this;
	}
	
	public Vector4d getReciprocal() {
		return new Vector4d(this).reciprocal();
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

	public Vector2d xy() {
		return new Vector2d(x,y);
	}
	
	public Vector2d xz() {
		return new Vector2d(x,z);
	}
	
	public Vector2d yz() {
		return new Vector2d(y,z);
	}

	public Vector2d xw() {
		return new Vector2d(x,w);
	}
	
	public Vector2d yw() {
		return new Vector2d(y,w);
	}
	
	public Vector2d zw() {
		return new Vector2d(z,w);
	}

	public Vector3d xyz() {
		return new Vector3d(x,y,z);
	}
	
	public Vector3d yzw() {
		return new Vector3d(y,z,w);
	}
	
	public Vector3d xzw() {
		return new Vector3d(x,z,w);
	}
	
	public Vector3d xyw() {
		return new Vector3d(x,y,w);
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
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Vector4d))return false;
		Vector4d v = (Vector4d)o;
		return x==v.x&&y==v.y&&z==v.z&&w==v.w;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x,y,z,w);
	}
}