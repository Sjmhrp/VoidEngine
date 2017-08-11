package sjmhrp.physics.shapes;

import java.util.ArrayList;

import sjmhrp.linear.Matrix4d;
import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.collision.broadphase.AABB;
import sjmhrp.physics.collision.broadphase.Tree;
import sjmhrp.physics.dynamics.Ray;

public class StaticTriMesh extends CollisionShape{

	private static final long serialVersionUID = 6554328017488502778L;
	
	ArrayList<TriangleShape> mesh = new ArrayList<TriangleShape>();
	Tree tree = new Tree();

	public StaticTriMesh(double[] vertices, int[] indices, Transform transform) {
		for(int i = 0; i < indices.length/3; i++) {
			Vector3d v1 = new Vector3d(vertices[indices[i*3]*3],vertices[indices[i*3]*3+1],vertices[indices[i*3]*3+2]);
			Vector3d v2 = new Vector3d(vertices[indices[i*3+1]*3],vertices[indices[i*3+1]*3+1],vertices[indices[i*3+1]*3+2]);
			Vector3d v3 = new Vector3d(vertices[indices[i*3+2]*3],vertices[indices[i*3+2]*3+1],vertices[indices[i*3+2]*3+2]);
			v1.set(Transform.transform(transform,v1));
			v2.set(Transform.transform(transform,v2));
			v3.set(Transform.transform(transform,v3));
			TriangleShape t = new TriangleShape(v1,v2,v3); 
			mesh.add(t);
			tree.add(t.getBoundingBox(transform),t);
		}
	}

	public StaticTriMesh(ArrayList<TriangleShape> triangles, Transform transform) {
		mesh=triangles;
		for(TriangleShape t : triangles) {
			tree.add(t.getBoundingBox(transform),t);
		}
	}

	public ArrayList<TriangleShape> query(AABB box) {
		ArrayList<TriangleShape> triangles = new ArrayList<TriangleShape>();
		for(Object o : tree.query(box)) {
			triangles.add((TriangleShape)o);
		}
		return triangles;
	}

	public ArrayList<TriangleShape> query(Ray ray) {
		ArrayList<TriangleShape> triangles = new ArrayList<TriangleShape>();
		for(Object o : tree.query(ray)) {
			triangles.add((TriangleShape)o);
		}
		return triangles;
	}
	
	@Override
	public AABB getBoundingBox(Transform t) {
		return tree.root.getBoundingBox();
	}

	@Override
	public Vector3d calculateLocalInertia(double mass) {
		return new Vector3d();
	}

	@Override
	public String getName() {
		return "TRIMESH";
	}

	@Override
	public Matrix4d getSkewMatrix() {
		return new Matrix4d().setIdentity();
	}
}