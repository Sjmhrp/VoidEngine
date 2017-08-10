package sjmhrp.linear;

import java.io.Serializable;
import java.nio.FloatBuffer;

public class Matrix3d implements Serializable{
	
	private static final long serialVersionUID = 2512463293334728256L;
	
	public double m00,m01,m02,m10,m11,m12,m20,m21,m22;
	
	public Matrix3d() {}
	
	public Matrix3d(double d) {
		setIdentity();
		scale(d);
	}
	
	public Matrix3d(Matrix3d m) {
		set(m.m00,m.m01,m.m02,m.m10,m.m11,m.m12,m.m20,m.m21,m.m22);
	}
	
	public Matrix3d(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22) {
		set(m00,m01,m02,m10,m11,m12,m20,m21,m22);
	}
	
	public Matrix3d set(Matrix3d m) {
		m00=m.m00;
		m01=m.m01;
		m02=m.m02;
		m10=m.m10;
		m11=m.m11;
		m12=m.m12;
		m20=m.m20;
		m21=m.m21;
		m22=m.m22;
		return this;
	}
	
	public Matrix3d set(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22) {
		this.m00=m00;
		this.m01=m01;
		this.m02=m02;
		this.m10=m10;
		this.m11=m11;
		this.m12=m12;
		this.m20=m20;
		this.m21=m21;
		this.m22=m22;
		return this;
	}
	
	public Matrix3d setIdentity() {
		return set(1,0,0,0,1,0,0,0,1);
	}
	
	public Matrix3d zero() {
		return set(0,0,0,0,0,0,0,0,0);
	}
	
	public Matrix3d store(FloatBuffer buf) {
		buf.put((float)m00);
		buf.put((float)m10);
		buf.put((float)m20);
		buf.put((float)m01);
		buf.put((float)m11);
		buf.put((float)m21);
		buf.put((float)m02);
		buf.put((float)m12);
		buf.put((float)m22);
		return this;
	}
	
	public Matrix3d scale(double d) {
		m00*=d;
		m01*=d;
		m02*=d;
		m10*=d;
		m11*=d;
		m12*=d;
		m20*=d;
		m21*=d;
		m22*=d;
		return this;
	}
	
	public Matrix3d add(Matrix3d m) {
		m00+=m.m00;
		m01+=m.m01;
		m02+=m.m02;
		m10+=m.m10;
		m11+=m.m11;
		m12+=m.m12;
		m20+=m.m20;
		m21+=m.m21;
		m22+=m.m22;
		return this;
	}
	
	public Matrix3d sub(Matrix3d m) {
		m00-=m.m00;
		m01-=m.m01;
		m02-=m.m02;
		m10-=m.m10;
		m11-=m.m11;
		m12-=m.m12;
		m20-=m.m20;
		m21-=m.m21;
		m22-=m.m22;
		return this;
	}
	
	public Matrix3d mul(Matrix3d m) {
		return set(m00*m.m00+m01*m.m10+m02*m.m20,
				   m00*m.m01+m01*m.m11+m02*m.m21,
				   m00*m.m02+m01*m.m12+m02*m.m22,
				   m10*m.m00+m11*m.m10+m12*m.m20,
				   m10*m.m01+m11*m.m11+m12*m.m21,
				   m10*m.m02+m11*m.m12+m12*m.m22,
				   m20*m.m00+m21*m.m10+m22*m.m20,
				   m20*m.m01+m21*m.m11+m22*m.m21,
				   m20*m.m02+m21*m.m12+m22*m.m22);
	}
	
	public Matrix3d invert() {
		double d = determinant();
		if(d==0)throw new IllegalStateException("Matrix is Uninvertible");
		set(m11*m22 - m12*m21, m02*m21 - m01*m22, m01*m12 - m02*m11,
				m12*m20 - m10*m22, m00*m22 - m02*m20, m02*m10 - m00*m12,
				m10*m21 - m11*m20, m01*m20 - m00*m21, m00*m11 - m01*m10);
		return scale(1/d);
	}
	
	public Matrix3d getInverse() {
		return new Matrix3d(this).invert();
	}
	
	public Matrix3d transpose() {
		return set(m00,m10,m20,m01,m11,m21,m02,m12,m22);
	}
	
	public Matrix3d getTranspose() {
		return new Matrix3d(this).transpose();
	}
	
	public Matrix3d abs() {
		m00=Math.abs(m00);
		m01=Math.abs(m01);
		m02=Math.abs(m02);
		m10=Math.abs(m10);
		m11=Math.abs(m11);
		m12=Math.abs(m12);
		m20=Math.abs(m20);
		m21=Math.abs(m21);
		m22=Math.abs(m22);
		return this;
	}

	public Matrix4d to4Matrix() {
		Matrix4d m = new Matrix4d();
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

	public Vector3d transform(Vector3d v) {
		return v.set(m00*v.x+m01*v.y+m02*v.z,m10*v.x+m11*v.y+m12*v.z,m20*v.x+m21*v.y+m22*v.z);
	}
	
	public boolean isInvertible() {
		return determinant()!=0;
	}
	
	public double determinant() {
		return m00*(m11*m22-m12*m21)-m01*(m10*m22-m12*m20)+m02*(m10*m21-m11*m20);
	}
	
	public static Matrix3d add(Matrix3d a, Matrix3d b) {
		return new Matrix3d(a).add(b);
	}

	public static Matrix3d sub(Matrix3d a, Matrix3d b) {
		return new Matrix3d(a).sub(b);
	}

	public static Matrix3d mul(Matrix3d a, Matrix3d b) {
		return new Matrix3d(a).mul(b);
	}

	public static Vector3d transform(Matrix3d m, Vector3d v) {
		return m.transform(new Vector3d(v));
	}
	
	@Override
	public String toString() {
		return "Matrix3d["+m00+", "+m01+", "+m02+"\n"+m10+", "+m11+", "+m12+"\n"+m20+", "+m21+", "+m22+"]";
	}
}