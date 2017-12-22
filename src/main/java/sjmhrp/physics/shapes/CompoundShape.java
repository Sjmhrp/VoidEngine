package sjmhrp.physics.shapes;

import java.util.ArrayList;
import java.util.HashMap;

import sjmhrp.physics.collision.broadphase.AABB;
import sjmhrp.utils.GeometryUtils;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Transform;
import sjmhrp.utils.linear.Vector3d;

public class CompoundShape extends CollisionShape {

	private static final long serialVersionUID = 1250997425370556342L;
	
	ArrayList<ConvexShape> shapes = new ArrayList<ConvexShape>();
	HashMap<CollisionShape,Transform> localPositions = new HashMap<CollisionShape,Transform>();

	public CompoundShape add(ConvexShape shape, Transform t) {
		shapes.add(shape);
		localPositions.put(shape,t);
		return this;
	}

	public ArrayList<ConvexShape> getShapes() {
		return shapes;
	}

	public Transform getOffset(ConvexShape s) {
		return localPositions.get(s);
	}
	
	@Override
	public AABB getBoundingBox(Transform t) {
		if(shapes.size()==0)return new AABB(t.position,t.position);
		AABB aabb = shapes.get(0).getBoundingBox(t);
		for(int i = 1; i < shapes.size(); i++) {
			ConvexShape s = shapes.get(i);
			Transform trans = Transform.mul(t,localPositions.get(s));
			aabb = GeometryUtils.combine(aabb,s.getBoundingBox(trans));
		}
		return aabb;
	}

	@Override
	public Vector3d calculateLocalInertia(double mass) {
		Vector3d total = new Vector3d();
		for(CollisionShape s : shapes) {
			total.add(s.calculateLocalInertia(mass));
		}
		return total;
	}

	@Override
	public Matrix4d getSkewMatrix() {
		return new Matrix4d().setIdentity();
	}

	@Override
	public String getName() {
		return "COMPOUND";
	}
}