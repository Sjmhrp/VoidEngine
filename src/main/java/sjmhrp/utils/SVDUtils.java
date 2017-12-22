package sjmhrp.utils;

import static java.lang.Math.*;

import sjmhrp.utils.linear.Matrix3d;
import sjmhrp.utils.linear.Vector2d;
import sjmhrp.utils.linear.Vector3d;

public class SVDUtils {

	
	static double pinv(double x, double tol) {
		return (abs(x)<tol||abs(1/x)<tol)?0:(1/x);
	}
	
	static Matrix3d pseudoInverse(Matrix3d d, Matrix3d v, double tol) {
		double d0 = pinv(d.m00,tol),d1=pinv(d.m11,tol),d2=pinv(d.m22,tol);
		Matrix3d out = new Matrix3d();
		out.set(v.m00 * d0 * v.m00 + v.m01 * d1 * v.m01 + v.m02 * d2 * v.m02,
				v.m00 * d0 * v.m10 + v.m01 * d1 * v.m11 + v.m02 * d2 * v.m12,
				v.m00 * d0 * v.m20 + v.m01 * d1 * v.m21 + v.m02 * d2 * v.m22,
				v.m10 * d0 * v.m00 + v.m11 * d1 * v.m01 + v.m12 * d2 * v.m02,
				v.m10 * d0 * v.m10 + v.m11 * d1 * v.m11 + v.m12 * d2 * v.m12,
				v.m10 * d0 * v.m20 + v.m11 * d1 * v.m21 + v.m12 * d2 * v.m22,
				v.m20 * d0 * v.m00 + v.m21 * d1 * v.m01 + v.m22 * d2 * v.m02,
				v.m20 * d0 * v.m10 + v.m21 * d1 * v.m11 + v.m22 * d2 * v.m12,
				v.m20 * d0 * v.m20 + v.m21 * d1 * v.m21 + v.m22 * d2 * v.m22);
		return out;
	}
	
	static Matrix3d rot01_post(Matrix3d m, double c, double s) {
		return m.set(c*m.m00-s*m.m01,s*m.m00+c*m.m01,m.m02,c*m.m10-s*m.m11,
					 s*m.m10+c*m.m11,m.m12,c*m.m20-s*m.m21,s*m.m20+c*m.m21,m.m22);
	}
	
	static Matrix3d rot02_post(Matrix3d m, double c, double s) {
		return m.set(c*m.m00-s*m.m02,m.m01,s*m.m00+c*m.m02,c*m.m10-s*m.m12,m.m11,
					 s*m.m10+c*m.m12,c*m.m20-s*m.m22,m.m21,s*m.m20+c*m.m22);
	}

	static Matrix3d rot12_post(Matrix3d m, double c, double s) {
		return m.set(m.m00,c*m.m01-s*m.m02,s*m.m01+c*m.m02,m.m10,c*m.m11-s*m.m12,
					 s*m.m11+c*m.m12,m.m20,c*m.m21-s*m.m22,s*m.m21+c*m.m22);
	}
	
	static Vector2d calcSymmetric(Vector2d v, double a_pp, double a_pq, double a_qq) {
		if(a_pq==0)return new Vector2d(1,0);
		double tau = (a_qq-a_pp)/(2*a_pq);
		double stt = sqrt(1+tau*tau);
		double tan = 1d/((tau>=0)?(tau+stt):(tau-stt));
		double c = 1d/sqrt(1+tan*tan);
		double s = tan*c;
		return v.set(c,s);
	}

	static Matrix3d rot01(Matrix3d m, Vector2d v) {
		calcSymmetric(v,m.m00,m.m01,m.m11);
		double c = v.x;
		double s = v.y;
		double cc = c*c;
		double ss = s*s;
		double mix = 2*c*s*m.m01;
		return m.setSymmetric(cc*m.m00-mix+ss*m.m11,0,c*m.m02-s*m.m12,
                ss*m.m00+mix+cc*m.m11,s*m.m02+c*m.m12,m.m22);
	}
	
	static Matrix3d rot02(Matrix3d m, Vector2d v) {
		calcSymmetric(v,m.m00,m.m02,m.m22);
		double c = v.x;
		double s = v.y;
		double cc = c*c;
		double ss = s*s;
		double mix = 2*c*s*m.m02;
		return m.setSymmetric(cc*m.m00-mix+ss*m.m22,c*m.m01-s*m.m12,0,
				m.m11,s*m.m01+c*m.m12,ss*m.m00+mix+cc*m.m22);
	}

	static Matrix3d rot12(Matrix3d m, Vector2d v) {
		calcSymmetric(v,m.m11,m.m12,m.m22);
		double c = v.x;
		double s = v.y;
		double cc = c*c;
		double ss = s*s;
		double mix = 2*c*s*m.m02;
		return m.setSymmetric(m.m00,c*m.m01-s*m.m02,s*m.m01+c*m.m02,
				cc*m.m11-mix+ss*m.m22,0,ss*m.m11+mix+cc*m.m22);
	}
	
	static void rotate01(Matrix3d vtav, Matrix3d v) {
		if (vtav.m01==0)return;
		Vector2d cs = new Vector2d();
		rot01(vtav,cs);
		rot01_post(v,cs.x,cs.y);
	}
	
	static void rotate02(Matrix3d vtav, Matrix3d v) {
		if (vtav.m02==0)return;
		Vector2d cs = new Vector2d();
		rot02(vtav,cs);
		rot02_post(v,cs.x,cs.y);
	}

	static void rotate12(Matrix3d vtav, Matrix3d v) {
		if (vtav.m12==0)return;
		Vector2d cs = new Vector2d();
		rot12(vtav,cs);
		rot12_post(v,cs.x,cs.y);
	}
	
	static void getSymmetricSVD(Matrix3d a, Matrix3d vtav, Matrix3d v, double tol, int max_sweeps) {
		vtav.setSymmetric(a);
		v.setIdentity();
		double delta = tol*MatrixUtils.norm(vtav);
		for(int i = 0; i < max_sweeps && MatrixUtils.off(vtav)>delta;i++) {
			rotate01(vtav,v);
			rotate02(vtav,v);
			rotate12(vtav,v);
		}
	}
	
	static double calcError(Matrix3d a, Vector3d x, Vector3d b) {
		Vector3d tmp = Matrix3d.transform(a,x);
		tmp.sub(b);
		return tmp.lengthSquared();
	}
	
	public static double solveSymmetric(Matrix3d a, Vector3d b, Vector3d x, double svd_tol, int svd_sweeps, double pinv_tol) {
		Matrix3d pinv = new Matrix3d();
		Matrix3d v = new Matrix3d();
		Matrix3d vtav = new Matrix3d();
		getSymmetricSVD(a,vtav,v,svd_tol,svd_sweeps);
		pinv.set(pseudoInverse(vtav,v,pinv_tol));
		x=Matrix3d.transform(pinv,b);
		return calcError(a,x,b);
	}
}