package sjmhrp.physics.dynamics;

import sjmhrp.utils.linear.Vector3d;

public class Ray {

	Vector3d origin;
	Vector3d dir;
	Vector3d invdir;
	int[] sign = new int[3];
	double length;
	
	public Ray(Vector3d origin, Vector3d dir, double length) {
		this.origin = new Vector3d(origin);
		double l = dir.length();
		this.dir = l==0?new Vector3d():new Vector3d(dir).scale(1d/l);
		this.invdir = this.dir.getReciprocal();
		this.length = length;
		sign[0]=(this.dir.x*length)<0?1:0;
		sign[1]=(this.dir.y*length)<0?1:0;
		sign[2]=(this.dir.z*length)<0?1:0;
	}
	
	public Ray(Vector3d origin, Vector3d end) {
		this.origin = origin;
		Vector3d d = Vector3d.sub(end,origin);
		sign[0]=d.x<0?1:0;
		sign[1]=d.y<0?1:0;
		sign[2]=d.z<0?1:0;
		length = d.length();
		dir = d.scale(1d/length);
		invdir = dir.getReciprocal();
	}
	
	public Vector3d getOrigin() {
		return origin;
	}
	
	public Vector3d getDir() {
		return dir;
	}
	
	public double getLength() {
		return length;
	}
	
	public Vector3d getEnd() {
		return Vector3d.scale(length,dir).add(origin);
	}
	
	public int[] getSign() {
		return sign;
	}
	
	public Vector3d getInvdir() {
		return invdir;
	}
	
	@Override
	public String toString() {
		return "Ray["+origin+", "+getEnd()+"]";
	}
}