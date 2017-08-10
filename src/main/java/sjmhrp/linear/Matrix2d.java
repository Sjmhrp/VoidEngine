package sjmhrp.linear;

import java.io.Serializable;

public class Matrix2d implements Serializable{
	
	private static final long serialVersionUID = -8210100486273200709L;
	
	public double m00,m01,m10,m11;

	public Matrix2d() {}

	public Matrix2d(double d) {
		setIdentity();
		scale(d);
	}

	public Matrix2d(Matrix2d m) {
		set(m.m00,m.m01,m.m10,m.m11);
	}

	public Matrix2d(double m00, double m01, double m10, double m11) {
		set(m00,m01,m10,m11);
	}

	public Matrix2d set(Matrix2d m) {
		m00=m.m00;
		m01=m.m01;
		m10=m.m10;
		m11=m.m11;
		return this;
	}
	
	public Matrix2d set(double m00, double m01, double m10, double m11) {
		this.m00=m00;
		this.m01=m01;
		this.m10=m10;
		this.m11=m11;
		return this;
	}

	public Matrix2d setIdentity() {
		set(1,0,0,1);
		return this;
	}

	public Matrix2d zero() {
		return set(0,0,0,0);
	}
	
	public Matrix2d scale(double d) {
		m00*=d;
		m01*=d;
		m10*=d;
		m11*=d;
		return this;
	}

	public Matrix2d add(Matrix2d m) {
		m00+=m.m00;
		m01+=m.m01;
		m10+=m.m10;
		m11+=m.m11;
		return this;
	}

	public Matrix2d sub(Matrix2d m) {
		m00-=m.m00;
		m01-=m.m01;
		m10-=m.m10;
		m11-=m.m11;
		return this;
	}

	public Matrix2d mul(Matrix2d m) {
		return set(m00*m.m00+m01*m.m10,
				   m00*m.m01+m01*m.m11,
				   m10*m.m00+m11*m.m10,
				   m10*m.m01+m11*m.m11);
	}

	public Matrix2d invert() {
		double d = determinant();
		if(d==0)throw new IllegalStateException("Matrix is Uninvertible");
		set(m11,-m01,-m10,m00);
		return scale(1/d);
	}

	public Matrix2d getInverse() {
		return new Matrix2d(this).invert();
	}

	public Matrix2d transpose() {
		return set(m00,m10,m01,m11);
	}

	public Matrix2d getTranspose() {
		return new Matrix2d(this).transpose();
	}

	public Matrix2d abs() {
		m00=Math.abs(m00);
		m01=Math.abs(m01);
		m10=Math.abs(m10);
		m11=Math.abs(m11);
		return this;
	}

	public Vector2d transform(Vector2d v) {
		return v.set(m00*v.x+m01*v.y,m10*v.x+m11*v.y);
	}

	public boolean isInvertible() {
		return determinant()!=0;
	}

	public double determinant() {
		return m00*m11-m01*m10;
	}

	public static Matrix2d add(Matrix2d a, Matrix2d b) {
		return new Matrix2d(a).add(b);
	}

	public static Matrix2d sub(Matrix2d a, Matrix2d b) {
		return new Matrix2d(a).sub(b);
	}

	public static Matrix2d mul(Matrix2d a, Matrix2d b) {
		return new Matrix2d(a).mul(b);
	}

	public static Vector2d transform(Matrix2d m, Vector2d v) {
		return m.transform(new Vector2d(v));
	}

	@Override
	public String toString() {
		return "Matrix2d["+m00+", "+m01+"\n"+m10+", "+m11+"]";
	}
}