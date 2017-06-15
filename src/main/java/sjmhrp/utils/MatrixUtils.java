package sjmhrp.utils;

import sjmhrp.linear.Matrix3d;
import sjmhrp.linear.Matrix4d;
import sjmhrp.linear.Quaternion;
import sjmhrp.linear.Vector3d;

public class MatrixUtils {

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