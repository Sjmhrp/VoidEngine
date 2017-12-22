package sjmhrp.utils;

import static java.lang.Math.sqrt;

import sjmhrp.utils.linear.Matrix3d;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Quaternion;
import sjmhrp.utils.linear.Vector3d;

public class MatrixUtils {

	public static double norm(Matrix3d a) {
		return sqrt((a.m00 * a.m00) + (a.m01 * a.m01) + (a.m02 * a.m02)
		+ (a.m10 * a.m10) + (a.m11 * a.m11) + (a.m12 * a.m12)
		+ (a.m20 * a.m20) + (a.m21 * a.m21) + (a.m22 * a.m22));
	}

	public static double off(Matrix3d a) {
		return sqrt((a.m01 * a.m01) + (a.m02 * a.m02) + (a.m10 * a.m10)
		+ (a.m12 * a.m12) + (a.m20 * a.m20) + (a.m21 * a.m21));
	}
	
	public static Vector3d transformSymmetric(Matrix3d a, Vector3d v) {
		Vector3d out = new Vector3d();
		out.x = (a.m00 * v.x) + (a.m01 * v.y) + (a.m02 * v.z);
		out.y = (a.m01 * v.x) + (a.m11 * v.y) + (a.m12 * v.z);
		out.z = (a.m02 * v.x) + (a.m12 * v.y) + (a.m22 * v.z);
		return out;
	}
	
	public static Matrix4d createTransform(Vector3d translation, Quaternion rotation, double scale) {
		Matrix4d matrix = new Matrix4d();
		matrix.setIdentity();
		matrix.translate(translation);
		rotation.normalize();
		matrix.mul(rotation.getRotationMatrix());
		matrix.scale(scale);
		return matrix;
	}
	
	public static Matrix4d createTransform(Vector3d translation, Quaternion rotation) {
		Matrix4d matrix = new Matrix4d();
		matrix.setIdentity();
		matrix.translate(translation);
		rotation.normalize();
		matrix.mul(rotation.getRotationMatrix());
		return matrix;
	}

	public static Matrix4d createTransform(Vector3d translation, double scale) {
		Matrix4d matrix = new Matrix4d();
		matrix.setIdentity();
		matrix.translate(translation);
		matrix.scale(scale);
		return matrix;
	}

	public static Matrix4d createTransform(Vector3d translation) {
		Matrix4d matrix = new Matrix4d();
		matrix.setIdentity();
		matrix.translate(translation);
		return matrix;
	}

	public static Matrix4d createRotation(Vector3d v) {
		Matrix4d m = new Matrix4d().setIdentity();
		m.rotate(v.x,new Vector3d(1,0,0));
		m.rotate(v.y,new Vector3d(0,1,0));
		m.rotate(v.z,new Vector3d(0,0,1));
		return m;
	}

	public static Matrix3d createSkewSymmetric(Vector3d v) {
		return new Matrix3d(0,-v.z,v.y,v.z,0,-v.x,-v.y,v.x,0);
	}
}