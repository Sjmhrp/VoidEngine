package sjmhrp.physics.shapes;

import java.util.ArrayList;

import sjmhrp.linear.Matrix4d;
import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.collision.broadphase.AABB;
import sjmhrp.utils.GeometryUtils;

public class ConvexHullShape extends ConvexShape{

	ArrayList<Vector3d> vertices = new ArrayList<Vector3d>();
	
	public ConvexHullShape(ArrayList<Vector3d> vertices) {
		this.vertices = vertices;
	}

	public ArrayList<Vector3d> getVertices() {
		return vertices;
	}
	
	@Override
	public Vector3d getLocalSupportPointWithoutMargin(Vector3d d) {
		Vector3d bestVertex = null;
		double dot = 0;
		for(Vector3d v : vertices) {
			double n = v.dot(d);
			if(bestVertex==null||n>dot) {
				bestVertex=v;
				dot=n;
			}
		}
		return new Vector3d(bestVertex);
	}

	@Override
	public AABB getBoundingBox(Transform t) {
		AABB aabb = new AABB(Transform.transform(t,vertices.get(0)),Transform.transform(t,vertices.get(0)));
		for(int i = 1; i < vertices.size(); i++) {
			Vector3d v = vertices.get(i);
			Vector3d p = Transform.transform(t,v);
			AABB a = new AABB(p,p);
			aabb = GeometryUtils.combine(aabb,a);
		}
		return aabb;
	}

	@Override
	public Vector3d calculateLocalInertia(double mass) {
		Vector3d extent = getBoundingBox(new Transform()).getRadius();
		double lx = 2f*extent.x;
		double ly = 2f*extent.y;
		double lz = 2f*extent.z;
		return new Vector3d(
				mass / 12f * (ly * ly + lz * lz),
				mass / 12f * (lx * lx + lz * lz),
				mass / 12f * (lx * lx + ly * ly));
	}

	@Override
	public String getName() {
		return "CONVEX_HULL";
	}

	@Override
	public Matrix4d getSkewMatrix() {
		return new Matrix4d().setIdentity();
	}
}