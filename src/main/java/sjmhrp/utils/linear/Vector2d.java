package sjmhrp.utils.linear;

import java.io.Serializable;
import java.util.Objects;

public class Vector2d implements Serializable{
	
	private static final long serialVersionUID = -3383228540145847036L;
	
	public double x,y;

	public Vector2d() {}

	public Vector2d(double d) {
		set(d,d);
	}

	public Vector2d(double x, double y) {
		set(x,y);
	}

	public Vector2d(Vector2d v) {
		set(v.x,v.y);
	}

	public Vector2d(Vector3d v) {
		set(v.x,v.y);
	}
	
	public Vector2d(Vector4d v) {
		set(v.x,v.y);
	}
	
	public Vector2d set(Vector2d v) {
		x=v.x;
		y=v.y;
		return this;
	}
	
	public Vector2d set(double x, double y) {
		this.x=x;
		this.y=y;
		return this;
	}

	public double get(int i) {
		switch(i) {
			case 0:return x;
			case 1:return y;
		}
		return 0;
	}
	
	public Vector2d zero() {
		return set(0,0);
	}

	public Vector2d add(Vector2d v) {
		x+=v.x;
		y+=v.y;
		return this;
	}

	public Vector2d sub(Vector2d v) {
		x-=v.x;
		y-=v.y;
		return this;
	}

	public Vector2d scale(double d) {
		x*=d;
		y*=d;
		return this;
	}

	public Vector2d scale(Vector2d v) {
		x*=v.x;
		y*=v.y;
		return this;
	}
	
	public Vector2d mod(double m) {
		x%=m;
		y%=m;
		return this;
	}
	
	public Vector2d abs() {
		x=Math.abs(x);
		y=Math.abs(y);
		return this;
	}

	public Vector2d getAbs() {
		return new Vector2d(this).abs();
	}
	
	public Vector2d negate() {
		return set(-x,-y);
	}

	public Vector2d getNegative() {
		return new Vector2d(this).negate();
	}
	
	public Vector2d normalize() {
		double l = length();
		if(l==0) throw new IllegalStateException("Cannot Normalize the Zero Vector");
		return scale(1/l);
	}

	public Vector2d getUnit() {
		return new Vector2d(this).getUnit();
	}
	
	public Vector2d reciprocal() {
		x=1d/x;
		y=1d/y;
		return this;
	}
	
	public Vector2d getReciprocal() {
		return new Vector2d(this).reciprocal();
	}
	
	public double dot(Vector2d v) {
		return x*v.x+y*v.y;
	}

	public double lengthSquared() {
		return dot(this);
	}

	public double length() {
		return Math.sqrt(lengthSquared());
	}

	public static Vector2d add(Vector2d u, Vector2d v) {
		return new Vector2d(u).add(v);
	}
	
	public static Vector2d sub(Vector2d u, Vector2d v) {
		return new Vector2d(u).sub(v);
	}

	public static Vector2d scale(double d, Vector2d v) {
		return new Vector2d(v).scale(d);
	}
	
	public static Vector2d scale(Vector2d u, Vector2d v) {
		return new Vector2d(u).scale(v);
	}
	
	public static double dot(Vector2d u, Vector2d v) {
		return u.dot(v);
	}
	
	@Override
	public String toString() {
		return "Vector2d["+x+", "+y+"]";
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Vector2d))return false;
		Vector2d v = (Vector2d)o;
		return x==v.x&&y==v.y;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x,y);
	}
}