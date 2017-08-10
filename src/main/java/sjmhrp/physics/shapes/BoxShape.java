package sjmhrp.physics.shapes;

import sjmhrp.linear.Matrix4d;
import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.collision.broadphase.AABB;

public class BoxShape extends ConvexShape{
	
	private static final long serialVersionUID = -4565044717259974755L;

	public BoxShape(Vector3d extent) {
		implicitShapeDimensions.set(Vector3d.scale(localScaling,extent));
		implicitShapeDimensions.sub(new Vector3d(getMargin()));
	}
	
	public BoxShape(double x, double y, double z) {
		this(new Vector3d(x,y,z));
	}

	public Vector3d getExtentWithMargin() {
		Vector3d extent = getExtentWithoutMargin();
		Vector3d margin = new Vector3d(getMargin());
		extent.add(margin);
		return extent;
	}

	public Vector3d getExtentWithoutMargin() {
		return new Vector3d(implicitShapeDimensions).scale(localScaling);
	}

	@Override
	public Vector3d getLocalSupportPoint(Vector3d d) {
		Vector3d extent = getExtentWithMargin();
		return new Vector3d(d.x>=0?extent.x:-extent.x,d.y>=0?extent.y:-extent.y,d.z>=0?extent.z:-extent.z);
	}

	@Override
	public Vector3d getLocalSupportPointWithoutMargin(Vector3d d) {
		Vector3d extent = getExtentWithoutMargin();
		return new Vector3d(d.x>=0?extent.x:-extent.x,d.y>=0?extent.y:-extent.y,d.z>=0?extent.z:-extent.z);
	}

	@Override
	public void setMargin(double margin) {
		Vector3d oldMargin = new Vector3d(getMargin());
		implicitShapeDimensions.add(oldMargin);
		super.setMargin(margin);
		Vector3d newMargin = new Vector3d(getMargin());
		implicitShapeDimensions.sub(newMargin);
	}

	@Override
	public AABB getBoundingBox(Transform t) {
		Vector3d position = t.position;
		Vector3d radius = getExtentWithMargin();
		t.orientation.getRotationMatrix().to3Matrix().abs().transform(radius);
		Vector3d min = Vector3d.sub(position,radius);
		Vector3d max = Vector3d.add(position,radius);
		return new AABB(min,max);
	}

	@Override
	public Vector3d calculateLocalInertia(double mass) {
		Vector3d extent = getExtentWithoutMargin();
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
		return "BOX";
	}

	@Override
	public Matrix4d getSkewMatrix() {
		return new Matrix4d(getExtentWithMargin());
	}
}