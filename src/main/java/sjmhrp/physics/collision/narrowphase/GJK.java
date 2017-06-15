package sjmhrp.physics.collision.narrowphase;

import sjmhrp.core.Globals;
import sjmhrp.linear.Matrix3d;
import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.collision.Contact;
import sjmhrp.physics.shapes.ConvexShape;

public class GJK{

	ConvexShape c1;
	ConvexShape c2;
	Transform t1;
	Transform t2;
	Transform b2Tob1;
	Matrix3d rotateTob2;

	Contact contact;

	EPA epa;

	public GJK(ConvexShape c1, ConvexShape c2, Transform t1, Transform t2, Contact c) {
		this.c1 = c1;
		this.c2 = c2;
		this.t1 = t1;
		this.t2 = t2;
		contact = c;
		epa = new EPA(c1,c2,t1,t2,contact);
		b2Tob1 = Transform.mul(t1.getInverse(),t2);
		rotateTob2 = t2.getInverse().orientation.getRotationMatrix().to3Matrix().mul(t1.orientation.getRotationMatrix().to3Matrix());
	}

	public void process(Vector3d axis) {
		final Vector3d supA = new Vector3d();
		final Vector3d supB = new Vector3d();
		final Vector3d w = new Vector3d();
		final Vector3d pA = new Vector3d();
		final Vector3d pB = new Vector3d();
		double vDotw;
		double prevDistSquare;
		final double margin = c1.getMargin()+c2.getMargin();
		final double marginSquared = margin*margin;
		if(margin<=0)throw new IllegalStateException("Margin cannot be negative");
		final Simplex simplex = new Simplex();
		final Vector3d v = axis;
		double distSquare = Float.MAX_VALUE;
		do {
			supA.set(supportAWithoutMargin(v));
			supB.set(supportBWithoutMargin(v));
			w.set(Vector3d.sub(supA,supB));
			vDotw = v.dot(w);
			if(vDotw > 0 && vDotw * vDotw > distSquare * marginSquared)return;			
			if(simplex.isPointInSimplex(w)||distSquare-vDotw<=distSquare*Globals.REL_ERROR_SQUARE) {
				simplex.closestPoints(pA,pB);
				final double dist = (double)Math.sqrt(distSquare);
				if(dist<=0)throw new IllegalStateException("Distance must be positive");
				pA.sub(Vector3d.scale(c1.getMargin()/dist,v));
				pB.add(Vector3d.scale(c2.getMargin()/dist,v));
				final Vector3d normal = v.getNegative().getUnit();
				final double depth = margin-dist;
				if(depth<0)return;
				output(pA,pB,normal,depth);
				return;
			}
			simplex.addPoint(w,supA,supB);
			if(simplex.isAffinelyDependent()) {
				simplex.closestPoints(pA,pB);
				final double dist = (double)Math.sqrt(distSquare);
				if(dist<=0)throw new IllegalStateException("Distance must be positive");
				pA.sub(Vector3d.scale(c1.getMargin()/dist,v));
				pB.add(Vector3d.scale(c2.getMargin()/dist,v));
				final Vector3d normal = v.getNegative().getUnit();
				final double depth = margin-dist;
				if(depth<0)return;
				output(pA,pB,normal,depth);
				return;
			}
			if(!simplex.computeClosestPoint(v)) {
				simplex.closestPoints(pA,pB);
				final double dist = (double)Math.sqrt(distSquare);
				if(dist<=0)throw new IllegalStateException("Distance must be positive");
				pA.sub(Vector3d.scale(c1.getMargin()/dist,v));
				pA.add(Vector3d.scale(c2.getMargin()/dist,v));
				final Vector3d normal = v.getNegative().getUnit();
				final double depth = margin-dist;
				if(depth<0)return;
				output(pA,pB,normal,depth);
				return;
			}
			prevDistSquare = distSquare;
			distSquare = v.lengthSquared();
			if(prevDistSquare-distSquare<=Globals.EPSILON*prevDistSquare) {
				simplex.backupClosestPoint(v);
				distSquare = v.lengthSquared();
				simplex.closestPoints(pA,pB);
				final double dist = (double)Math.sqrt(distSquare);
				if(dist<=0)throw new IllegalStateException("Distance must be positive");
				pA.add(Vector3d.scale(c1.getMargin()/dist,v));
				pB.add(Vector3d.scale(c2.getMargin()/dist,v));
				final Vector3d normal = v.getNegative().getUnit();
				final double depth = margin-dist;
				if(depth<0)return;
				output(pA,pB,normal,depth);
				return;
			}
		} while(!simplex.isFull()&&distSquare>Globals.EPSILON*simplex.getMaxLengthSquared());
		processWithoutMargin();
	}

	void processWithoutMargin() {
		final Vector3d supA = new Vector3d();
		final Vector3d supB = new Vector3d();
		final Vector3d w = new Vector3d();
		double vDotw;
		double prevDistSquare;
		final Simplex simplex = new Simplex();
		final Vector3d v = new Vector3d(1,0,0);
		double distSquare = Float.MAX_VALUE;
		do {
			supA.set(supportAWithMargin(v));
			supB.set(supportBWithMargin(v));
			w.set(Vector3d.sub(supA,supB));
			vDotw = v.dot(w);
			if(vDotw > 0)return;
			simplex.addPoint(w,supA,supB);
			if(simplex.isAffinelyDependent()||!simplex.computeClosestPoint(v))return;
			prevDistSquare = distSquare;
			distSquare = v.lengthSquared();
			if(prevDistSquare-distSquare<=Globals.EPSILON*prevDistSquare)return;
		} while(!simplex.isFull()&&distSquare>Globals.EPSILON*simplex.getMaxLengthSquared());
		epa.process(simplex);
	}

	void output(Vector3d pA, Vector3d pB, Vector3d normal, double depth) {
		Vector3d globalA = Transform.transform(t1,pA);
		Vector3d globalB = Transform.transform(t1,pB);
		t1.orientation.getRotationMatrix().to3Matrix().transform(normal);
		Vector3d localB = Transform.transform(t2.getInverse(),globalB);
		contact.setOutput(true,pA,localB,globalA,globalB,normal,depth);
	}

	Vector3d supportAWithoutMargin(Vector3d v) {
		return c1.getLocalSupportPointWithoutMargin(v.getNegative());
	}

	Vector3d supportBWithoutMargin(Vector3d v) {
		return Transform.transform(b2Tob1,c2.getLocalSupportPointWithoutMargin(Matrix3d.transform(rotateTob2,v)));
	}

	Vector3d supportAWithMargin(Vector3d v) {
		return c1.getLocalSupportPoint(v.getNegative());
	}

	Vector3d supportBWithMargin(Vector3d v) {
		return Transform.transform(b2Tob1,c2.getLocalSupportPoint(Matrix3d.transform(rotateTob2,v)));
	}
}