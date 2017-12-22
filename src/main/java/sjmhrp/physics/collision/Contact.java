package sjmhrp.physics.collision;

import java.io.Serializable;

import sjmhrp.utils.GeometryUtils;
import sjmhrp.utils.linear.Vector3d;

public class Contact implements Serializable {
	
	private static final long serialVersionUID = -7381187263508490745L;
	
	public final Vector3d localPointA = new Vector3d();
	public final Vector3d localPointB = new Vector3d();
	public final Vector3d globalPointA = new Vector3d();
	public final Vector3d globalPointB = new Vector3d();
	public final Vector3d normal = new Vector3d();
	public final Vector3d originalNormal = new Vector3d();
	public final Vector3d tangent1 = new Vector3d();
	public final Vector3d tangent2 = new Vector3d();

	public double depth = 0;
	public double normalImpulse = 0;
	public double frictionImpluse1 = 0;
	public double frictionImpluse2 = 0;

	public double normalMass;
	public double frictionMass1;
	public double frictionMass2;
	public double bias;

	public boolean collides = false;

	public void setOutput(boolean b, Vector3d localA, Vector3d localB, Vector3d globalA, Vector3d globalB, Vector3d normal, double depth) {
		this.collides = b;
		this.localPointA.set(localA);
		this.localPointB.set(localB);
		this.globalPointA.set(globalA);
		this.globalPointB.set(globalB);
		this.normal.set(normal);
		this.originalNormal.set(normal);
		this.depth = depth;
		GeometryUtils.computeBasis(normal,tangent1,tangent2);
	}

	public void updateNormal(Vector3d n) {
		normal.set(n.getUnit());
		GeometryUtils.computeBasis(normal,tangent1,tangent2);
	}

	@Override
	public String toString() {
		return "Contact[A: "+globalPointA+", B: "+globalPointB+", Normal: "+normal+", Depth: "+depth+", Tangent1: "+tangent1+", Tangent2: "+tangent2+"]";
	}
}