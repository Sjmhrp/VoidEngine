package sjmhrp.linear;

import java.io.Serializable;
import java.nio.FloatBuffer;

public class Matrix4d implements Serializable{
	
	private static final long serialVersionUID = 8242379281366068810L;
	
	public double m00,m01,m02,m03,m10,m11,m12,m13,m20,m21,m22,m23,m30,m31,m32,m33;
	
	public Matrix4d() {}
	
	public Matrix4d(double d) {
		setIdentity();
		scale(d);
	}
	
	public Matrix4d(Matrix4d m) {
		set(m.m00,m.m01,m.m02,m.m03,m.m10,m.m11,m.m12,m.m13,m.m20,m.m21,m.m22,m.m23,m.m30,m.m31,m.m32,m.m33);
	}
	
	public Matrix4d(Vector3d v) {
		setIdentity();
		scale(v);
	}
	
	public Matrix4d(double m00, double m01, double m02, double m03, double m10, double m11, double m12, double m13, double m20, double m21, double m22, double m23, double m30, double m31, double m32, double m33) {
		set(m00,m01,m02,m03,m10,m11,m12,m13,m20,m21,m22,m23,m30,m31,m32,m33);
	}
	
	public Matrix4d set(Matrix4d m) {
		m00=m.m00;
		m01=m.m01;
		m02=m.m02;
		m03=m.m03;
		m10=m.m10;
		m11=m.m11;
		m12=m.m12;
		m13=m.m13;
		m20=m.m20;
		m21=m.m21;
		m22=m.m22;
		m23=m.m23;
		m30=m.m30;
		m31=m.m31;
		m32=m.m32;
		m33=m.m33;
		return this;
	}
	
	public Matrix4d set(double m00, double m01, double m02, double m03, double m10, double m11, double m12, double m13, double m20, double m21, double m22, double m23, double m30, double m31, double m32, double m33) {
		this.m00=m00;
		this.m01=m01;
		this.m02=m02;
		this.m03=m03;
		this.m10=m10;
		this.m11=m11;
		this.m12=m12;
		this.m13=m13;
		this.m20=m20;
		this.m21=m21;
		this.m22=m22;
		this.m23=m23;
		this.m30=m30;
		this.m31=m31;
		this.m32=m32;
		this.m33=m33;
		return this;
	}
	
	public Matrix4d setIdentity() {
		return set(1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1);
	}
	
	public Matrix4d zero() {
		return set(0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);
	}
	
	public Matrix4d store(FloatBuffer buf) {
		buf.put((float)m00);
		buf.put((float)m10);
		buf.put((float)m20);
		buf.put((float)m30);
		buf.put((float)m01);
		buf.put((float)m11);
		buf.put((float)m21);
		buf.put((float)m31);
		buf.put((float)m02);
		buf.put((float)m12);
		buf.put((float)m22);
		buf.put((float)m32);
		buf.put((float)m03);
		buf.put((float)m13);
		buf.put((float)m23);
		buf.put((float)m33);
		return this;
	}

	public Matrix4d translate(Vector3d v) {
		m03+=m00*v.x+m01*v.y+m02*v.z;
		m13+=m10*v.x+m11*v.y+m12*v.z;
		m23+=m20*v.x+m21*v.y+m22*v.z;
		return this;
	}

	public Matrix4d rotate(double angle, Vector3d axis) {
		Quaternion q = new Quaternion();
		q.setAxis(axis,angle);
		return mul(q.getRotationMatrix());
	}

	public Matrix4d scale(double d) {
		m00*=d;
		m01*=d;
		m02*=d;
		m10*=d;
		m11*=d;
		m12*=d;
		m20*=d;
		m21*=d;
		m22*=d;
		m30*=d;
		m31*=d;
		m32*=d;
		return this;
	}

	public Matrix4d scale(Vector3d v) {
		m00*=v.x;
		m10*=v.x;
		m20*=v.x;
		m30*=v.x;
		m01*=v.y;
		m11*=v.y;
		m21*=v.y;
		m31*=v.y;
		m02*=v.z;
		m12*=v.z;
		m22*=v.z;
		m32*=v.z;
		return this;
	}

	public Matrix4d add(Matrix4d m) {
		m00+=m.m00;
		m01+=m.m01;
		m02+=m.m02;
		m03+=m.m03;
		m10+=m.m10;
		m11+=m.m11;
		m12+=m.m12;
		m13+=m.m13;
		m20+=m.m20;
		m21+=m.m21;
		m22+=m.m22;
		m23+=m.m23;
		m30+=m.m30;
		m31+=m.m31;
		m32+=m.m32;
		m33+=m.m33;
		return this;
	}
	
	public Matrix4d sub(Matrix4d m) {
		m00-=m.m00;
		m01-=m.m01;
		m02-=m.m02;
		m03-=m.m03;
		m10-=m.m10;
		m11-=m.m11;
		m12-=m.m12;
		m13-=m.m13;
		m20-=m.m20;
		m21-=m.m21;
		m22-=m.m22;
		m23-=m.m23;
		m30-=m.m30;
		m31-=m.m31;
		m32-=m.m32;
		m33-=m.m33;
		return this;
	}

	public Matrix4d mul(Matrix4d m) {
		return set(
			m00*m.m00 + m01*m.m10 + m02*m.m20 + m03*m.m30,
			m00*m.m01 + m01*m.m11 + m02*m.m21 + m03*m.m31,
			m00*m.m02 + m01*m.m12 + m02*m.m22 + m03*m.m32,
			m00*m.m03 + m01*m.m13 + m02*m.m23 + m03*m.m33,
			m10*m.m00 + m11*m.m10 + m12*m.m20 + m13*m.m30,
			m10*m.m01 + m11*m.m11 + m12*m.m21 + m13*m.m31,
			m10*m.m02 + m11*m.m12 + m12*m.m22 + m13*m.m32,
			m10*m.m03 + m11*m.m13 + m12*m.m23 + m13*m.m33,
			m20*m.m00 + m21*m.m10 + m22*m.m20 + m23*m.m30,
			m20*m.m01 + m21*m.m11 + m22*m.m21 + m23*m.m31,
			m20*m.m02 + m21*m.m12 + m22*m.m22 + m23*m.m32,
			m20*m.m03 + m21*m.m13 + m22*m.m23 + m23*m.m33,
			m30*m.m00 + m31*m.m10 + m32*m.m20 + m33*m.m30,
			m30*m.m01 + m31*m.m11 + m32*m.m21 + m33*m.m31,
			m30*m.m02 + m31*m.m12 + m32*m.m22 + m33*m.m32,
			m30*m.m03 + m31*m.m13 + m32*m.m23 + m33*m.m33);
	}

	public Matrix4d invert() {
		double d = determinant();
		if(d==0)throw new IllegalStateException("Matrix is Uninvertible");
		double t00 =  new Matrix3d(m11,m12,m13,m21,m22,m23,m31,m32,m33).determinant();
		double t01 = -new Matrix3d(m10,m12,m13,m20,m22,m23,m30,m32,m33).determinant();
		double t02 =  new Matrix3d(m10,m11,m13,m20,m21,m23,m30,m31,m33).determinant();
		double t03 = -new Matrix3d(m10,m11,m12,m20,m21,m22,m30,m31,m32).determinant();
		double t10 = -new Matrix3d(m01,m02,m03,m21,m22,m23,m31,m32,m33).determinant();
		double t11 =  new Matrix3d(m00,m02,m03,m20,m22,m23,m30,m32,m33).determinant();
		double t12 = -new Matrix3d(m00,m01,m03,m20,m21,m23,m30,m31,m33).determinant();
		double t13 =  new Matrix3d(m00,m01,m02,m20,m21,m22,m30,m31,m32).determinant();
		double t20 =  new Matrix3d(m01,m02,m03,m11,m12,m13,m31,m32,m33).determinant();
		double t21 = -new Matrix3d(m00,m02,m03,m10,m12,m13,m30,m32,m33).determinant();
		double t22 =  new Matrix3d(m00,m01,m03,m10,m11,m13,m30,m31,m33).determinant();
		double t23 = -new Matrix3d(m00,m01,m02,m10,m11,m12,m30,m31,m32).determinant();
		double t30 = -new Matrix3d(m01,m02,m03,m11,m12,m13,m21,m22,m23).determinant();
		double t31 =  new Matrix3d(m00,m02,m03,m10,m12,m13,m20,m22,m23).determinant();
		double t32 = -new Matrix3d(m00,m01,m03,m10,m11,m13,m20,m21,m23).determinant();
		double t33 =  new Matrix3d(m00,m01,m02,m10,m11,m12,m20,m21,m22).determinant();
		set(t00,t10,t20,t30,t01,t11,t21,t31,t02,t12,t22,t32,t03,t13,t23,t33);
		return scale(1/d);
	}

	public Matrix4d getInverse() {
		return new Matrix4d(this).invert();
	}
	
	public Matrix4d transpose() {
		return set(m00,m10,m20,m30,m01,m11,m21,m31,m02,m12,m22,m32,m03,m13,m23,m33);
	}
	
	public Matrix4d getTranspose() {
		return new Matrix4d(this).transpose();
	}
	
	public Matrix4d abs() {
		m00=Math.abs(m00);
		m01=Math.abs(m01);
		m02=Math.abs(m02);
		m03=Math.abs(m03);
		m10=Math.abs(m10);
		m11=Math.abs(m11);
		m12=Math.abs(m12);
		m13=Math.abs(m13);
		m20=Math.abs(m20);
		m21=Math.abs(m21);
		m22=Math.abs(m22);
		m23=Math.abs(m23);
		m30=Math.abs(m30);
		m31=Math.abs(m31);
		m32=Math.abs(m32);
		m33=Math.abs(m33);
		return this;
	}
	
	public Matrix3d to3Matrix() {
		Matrix3d m = new Matrix3d();
		m.m00=m00;
		m.m01=m01;
		m.m02=m02;
		m.m10=m10;
		m.m11=m11;
		m.m12=m12;
		m.m20=m20;
		m.m21=m21;
		m.m22=m22;
		return m;
	}
	
	public Vector4d transform(Vector4d v) {
		return v.set(m00*v.x+m01*v.y+m02*v.z+m03*v.w,
				m10*v.x+m11*v.y+m12*v.z+m13*v.w,
				m20*v.x+m21*v.y+m22*v.z+m23*v.w,
				m30*v.x+m31*v.y+m32*v.z+m33*v.w);
	}
	
	public Vector3d transform(Vector3d v) {
		return v.set(m00*v.x+m01*v.y+m02*v.z+m03,
				m10*v.x+m11*v.y+m12*v.z+m13,
				m20*v.x+m21*v.y+m22*v.z+m23);
	}
	
	public double determinant() {
		double f;
		f = m00 * (m11*m22*m33
					+m12*m23*m31
					+m13*m21*m32
					-m13*m22*m31
					-m11*m23*m32
					-m12*m21*m33);
		f-= m01 * (m10*m22*m33
					+m12*m23*m30
					+m13*m20*m32
					-m13*m22*m30
					-m10*m23*m32
					-m12*m20*m33);
		f+= m02 * (m10*m21*m33
					+m11*m23*m30
					+m13*m20*m31
					-m13*m21*m30
					-m10*m23*m31
					-m11*m20*m33);
		f-= m03 * (m10*m21*m32
					+m11*m22*m30
					+m12*m20*m31
					-m12*m21*m30
					-m10*m22*m31
					-m11*m20*m32);
		return f;
				
	}
	
	public static Matrix4d add(Matrix4d m1, Matrix4d m2) {
		return new Matrix4d(m1).add(m2);
	}
	
	public static Matrix4d sub(Matrix4d m1, Matrix4d m2) {
		return new Matrix4d(m1).sub(m2);
	}
	
	public static Matrix4d mul(Matrix4d m1, Matrix4d m2) {
		return new Matrix4d(m1).mul(m2);
	}
	
	public static Vector4d transform(Matrix4d m, Vector4d v) {
		return m.transform(new Vector4d(v));
	}
	
	public static Vector3d transform(Matrix4d m, Vector3d v) {
		return m.transform(new Vector3d(v));
	}
	
	@Override
	public String toString() {
		return "Matrix4d["+m00+", "+m01+", "+m02+", "+m03+"\n"
				+m10+", "+m11+", "+m12+", "+m13+"\n"
				+m20+", "+m21+", "+m22+", "+m23+"\n"
				+m30+", "+m31+", "+m32+", "+m33+"]";
	}
}