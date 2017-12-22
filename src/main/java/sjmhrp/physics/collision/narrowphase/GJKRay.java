package sjmhrp.physics.collision.narrowphase;

import sjmhrp.physics.collision.RaycastResult;
import sjmhrp.physics.dynamics.Ray;
import sjmhrp.physics.shapes.ConvexShape;
import sjmhrp.utils.linear.Matrix3d;
import sjmhrp.utils.linear.Transform;
import sjmhrp.utils.linear.Vector3d;

public class GJKRay {

	public static final double EPSILON = 0.0001;
	
	ConvexShape shape;
	Transform transform;
	Ray ray;
	
	RaycastResult result;
	
	public GJKRay(Ray ray, ConvexShape shape, Transform transform, RaycastResult result) {
		this.ray = ray;
		this.shape = shape;
		this.transform = transform;
		this.result = result;
	}

	public void process() {
		double lambda = 0;
		final Vector3d x = new Vector3d(ray.getOrigin());
		final Vector3d n = new Vector3d();
		final Vector3d v = Vector3d.sub(x,transform.position);
		final Vector3d p = new Vector3d();
		final Vector3d w = new Vector3d();
		final Simplex simplex = new Simplex();
		double vDotw;
		double vDotr;
		double prevDistSquare;
		double distSquare = Float.MAX_VALUE;
		do {
			p.set(support(v));
			w.set(Vector3d.sub(x,p));
			vDotw = v.dot(w);
			if(vDotw>0) {
				vDotr = v.dot(ray.getDir());
				if(vDotr>=0)return;
				lambda-=vDotw/vDotr;
				x.set(Vector3d.scale(lambda,ray.getDir()).add(ray.getOrigin()));
				n.set(v);
			}
			simplex.addPoint(Vector3d.sub(x,p),new Vector3d(),new Vector3d());
			if(simplex.isAffinelyDependent()||!simplex.computeClosestPoint(v))return;
			prevDistSquare = distSquare;
			distSquare = v.lengthSquared();
			if(prevDistSquare-distSquare<=EPSILON*prevDistSquare)return;
		} while(!simplex.isFull()&&distSquare>EPSILON*simplex.getMaxLengthSquared());
		if(lambda>ray.getLength())return;
		result.setOutput(n.lengthSquared()==0?new Vector3d():n.getUnit(),x,lambda);
	}
	
	Vector3d support(Vector3d v) {
		Matrix3d m = transform.orientation.getRotationMatrix().to3Matrix();
		return transform.transform(shape.getLocalSupportPoint(Matrix3d.transform(m.getInverse(),v)));
	}
}