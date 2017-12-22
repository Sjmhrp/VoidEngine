package sjmhrp.physics.shapes;

import sjmhrp.core.Globals;
import sjmhrp.utils.linear.Vector3d;

public abstract class ConvexShape extends CollisionShape {

	private static final long serialVersionUID = 7338077926552688815L;
	
	protected final Vector3d localScaling = new Vector3d(1,1,1);
	protected final Vector3d implicitShapeDimensions = new Vector3d();
	protected double collisionMargin = Globals.MARGIN;

	public Vector3d getLocalSupportPoint(Vector3d d) {
		Vector3d v = getLocalSupportPointWithoutMargin(d);
		if(getMargin()!=0) {
			if(d.lengthSquared()==0)return v;
			v.add(d.getUnit().scale(getMargin()));
		}
		return v;
	}

	public abstract Vector3d getLocalSupportPointWithoutMargin(Vector3d d);

	public double getMargin() {
		return collisionMargin;
	}

	public void setMargin(double margin) {
		this.collisionMargin = margin;
	}
}