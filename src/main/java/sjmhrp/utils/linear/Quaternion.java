package sjmhrp.utils.linear;

import java.io.Serializable;

public class Quaternion implements Serializable{
	
	private static final long serialVersionUID = 4528591258882504679L;
	
	public double x,y,z,w;
	
	public Quaternion() {
		set(0,0,0,1);
	}
	
	public Quaternion(double x, double y, double z, double w) {
		set(x,y,z,w);
	}
	
	public Quaternion(Vector3d v, double w) {
		set(v.x,v.y,v.z,w);
	}
	
	public Quaternion(Quaternion q) {
		set(q.x,q.y,q.z,q.w);
	}
	
	public Quaternion(Matrix3d matrix) {
		double diagonal = matrix.m00+matrix.m11+matrix.m22;
		if(diagonal>0) {
			double w4 = Math.sqrt(diagonal+1)*2;
			w=w4/4;
			x=(matrix.m21-matrix.m12)/w4;
			y=(matrix.m02-matrix.m20)/w4;
			z=(matrix.m10-matrix.m01)/w4;
		} else if(matrix.m00>matrix.m11&&matrix.m00>matrix.m22) {
			double x4 = Math.sqrt(1+matrix.m00-matrix.m11-matrix.m22)*2;
			w=(matrix.m21-matrix.m12)/x4;
			x=x4/4;
			y=(matrix.m01+matrix.m10)/x4;
			z=(matrix.m02+matrix.m20)/x4;
		} else if(matrix.m11>matrix.m22) {
			double y4 = Math.sqrt(1+matrix.m11-matrix.m00-matrix.m22)*2;
			w=(matrix.m02-matrix.m20)/y4;
			x=(matrix.m01+matrix.m10)/y4;
			y=y4/4;
			z=(matrix.m12+matrix.m21)/y4;
		} else {
			double z4 = Math.sqrt(1+matrix.m22-matrix.m00-matrix.m11)*2;
			w=(matrix.m10-matrix.m01)/z4;
			x=(matrix.m02+matrix.m20)/z4;
			y=(matrix.m12+matrix.m21)/z4;
			z=z4/4;
		}
		normalize();
	}
	
	public Quaternion set(Quaternion q) {
		x=q.x;
		y=q.y;
		z=q.z;
		w=q.w;
		return this;
	}
	
	public Quaternion set(double x, double y, double z, double w) {
		this.x=x;
		this.y=y;
		this.z=z;
		this.w=w;
		return this;
	}

	public Quaternion setAxis(Vector3d axis, double angle) {
		double d = axis.length();
		double s = Math.sin(angle*0.5)/d;
		return set(axis.x*s,axis.y*s,axis.z*s,Math.cos(angle*0.5));
	}
	
	public Matrix4d getRotationMatrix() {
		normalize();
		double n = lengthSquared();
		double s = n>0?2/n:0;
		double xs = x*s;
		double ys = y*s;
		double zs = z*s;
		double wxs = w*xs;
		double wys = w*ys;
		double wzs = w*zs;
		double xxs = x*xs;
		double xys = x*ys;
		double xzs = x*zs;
		double yys = y*ys;
		double yzs = y*zs;
		double zzs = z*zs;
		Matrix4d m = new Matrix4d();
		m.m00=1-yys-zzs;
		m.m01=xys-wzs;
		m.m02=xzs+wys;
		m.m10=xys+wzs;
		m.m11=1-xxs-zzs;
		m.m12=yzs-wxs;
		m.m20=xzs-wys;
		m.m21=yzs+wxs;
		m.m22=1-xxs-yys;
		m.m33=1;
		return m;
	}
	
	public Vector3d getVector() {
		return new Vector3d(x,y,z);
	}
	
	public Quaternion invert() {
		set(-x,-y,-z,w);
		return normalize();
	}
	
	public Quaternion getInverse() {
		return new Quaternion(this).invert();
	}
	
	public Quaternion normalize() {
		double l = length();
		if(l==0)throw new IllegalStateException("Cannot Normalize the Zero Quaternion");
		return scale(1/l);
	}
	
	public double lengthSquared() {
		return x*x+y*y+z*z+w*w;
	}
	
	public double length() {
		return Math.sqrt(lengthSquared());
	}
	
	public Quaternion rotate(double x, double y, double z, double timeStep) {
		return rotate(new Vector3d(x,y,z),timeStep);
	}
	
	public Quaternion rotate(Vector3d angvel, double timeStep) {
		double angle = angvel.length();
		Vector3d axis;
		if(angle<0.001) {
			axis = Vector3d.scale(0.5*timeStep-timeStep*timeStep*timeStep*0.020833333333*angle*angle,angvel);
		} else {
			axis = Vector3d.scale(Math.sin(0.5*angle*timeStep)/angle,angvel);
		}
		Quaternion dq = new Quaternion(axis,Math.cos(0.5*angle*timeStep));
		set(dq.mul(this));
		return normalize();
	}
	
	public Quaternion mul(Quaternion q) {
		return set(x*q.w + w*q.x + y*q.z - z*q.y,
				   y*q.w + w*q.y + z*q.x - x*q.z,
				   z*q.w + w*q.z + x*q.y - y*q.x,
				   w*q.w - x*q.x - y*q.y - z*q.z);
	}
	
	public Quaternion add(Quaternion q) {
		x+=q.z;
		y+=q.y;
		z+=q.z;
		w+=q.w;
		return this;
	}
	
	public Quaternion sub(Quaternion q) {
		x-=q.x;
		y-=q.y;
		z-=q.z;
		w-=q.w;
		return this;
	}
	
	public Quaternion scale(double d) {
		x*=d;
		y*=d;
		z*=d;
		w*=d;
		return this;
	}
	
	public static Quaternion add(Quaternion q1, Quaternion q2) {
		return new Quaternion(q1).add(q2);
	}
	
	public static Quaternion sub(Quaternion q1, Quaternion q2) {
		return new Quaternion(q1).sub(q2);
	}
	
	public static Quaternion scale(double d, Quaternion q) {
		return new Quaternion(q).scale(d);
	}
	
	public static Quaternion mul(Quaternion q1, Quaternion q2) {
		return new Quaternion(q1).mul(q2);
	}
	
	public static Quaternion slerp(Quaternion a, Quaternion b, double f) {
		a.normalize();
		b.normalize();
		double d = a.x*b.x+a.y*b.y+a.z*b.z+a.w*b.w;
		double absDot = Math.abs(d);
		double scale0 = 1-f;
		double scale1 = f;
		if(1-absDot>0.1) {
			double angle = Math.acos(absDot);
			double invSinT = 1d/Math.sin(angle);
			scale0=Math.sin((1-f)*angle)*invSinT;
			scale1=Math.sin(f*angle)*invSinT;
		}
		if(d<0)scale1=-scale1;
		double x = scale0*a.x+scale1*b.x;
		double y = scale0*a.y+scale1*b.y;
		double z = scale0*a.z+scale1*b.z;
		double w = scale0*a.w+scale1*b.w;
		return new Quaternion(x,y,z,w);
	}
	
	@Override
	public String toString() {
		return "Quaternion["+x+", "+y+", "+z+", "+w+"]";
	}
}