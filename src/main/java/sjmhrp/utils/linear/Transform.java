package sjmhrp.utils.linear;

import java.io.Serializable;

import sjmhrp.utils.VectorUtils;

public class Transform implements Serializable{
	
	private static final long serialVersionUID = -1374557009741140780L;
	
	public final Vector3d position = new Vector3d();
	public final Quaternion orientation = new Quaternion();
	
	public Transform() {}
	
	public Transform(Vector3d position, Quaternion orientation) {
		this.position.set(position);
		this.orientation.set(orientation);
	}

	public Transform(Vector3d position) {
		this(position,new Quaternion());
	}
	
	public Transform(Vector3d position, Matrix3d rotation) {
		this(position,new Quaternion(rotation));
	}

	public Transform(Vector3d position, Matrix4d rotation) {
		this(position,new Quaternion(rotation.to3Matrix()));
	}
	
	public Transform(Matrix4d transform) {
		this(new Vector3d(transform.m03,transform.m13,transform.m23),new Quaternion(transform.to3Matrix()));
	}
	
	public Matrix3d getBasis() {
		return orientation.getRotationMatrix().to3Matrix();
	}
	
	public Matrix4d getMatrix() {
		Matrix4d m = new Matrix4d().setIdentity();
		m.translate(position);
		m.mul(orientation.getRotationMatrix());
		return m;
	}

	public Transform getInverse() {
		Vector3d p = new Vector3d(position);
		p.negate();
		Matrix3d r = orientation.getRotationMatrix().to3Matrix().transpose();
		r.transform(p);
		return new Transform(p,r);
	}

	public Vector3d transform(Vector3d v) {
		orientation.getRotationMatrix().to3Matrix().transform(v);
		v.add(position);
		return v;
	}

	@Override
	public String toString() {
		return getMatrix().toString();
	}

	public static Vector3d transform(Transform t, Vector3d v) {
		return t.transform(new Vector3d(v));
	}

	public static Transform mul(Transform t1, Transform t2) {
		Vector3d p = Vector3d.add(t1.position,Matrix3d.transform(t1.orientation.getRotationMatrix().to3Matrix(),t2.position));
		Matrix3d r = Matrix3d.mul(t1.orientation.getRotationMatrix().to3Matrix(),t2.orientation.getRotationMatrix().to3Matrix());
		return new Transform(p,r);
	}
	
	public static Transform lerp(Transform a, Transform b, double f) {
		Vector3d pos = VectorUtils.lerp(a.position,b.position,f);
		Quaternion ori = Quaternion.slerp(a.orientation,b.orientation,f);
		return new Transform(pos,ori);
	}
}