package sjmhrp.physics.collision.narrowphase;

import java.util.PriorityQueue;
import java.util.Queue;

import sjmhrp.core.Globals;
import sjmhrp.physics.collision.Contact;
import sjmhrp.physics.shapes.ConvexShape;
import sjmhrp.utils.GeometryUtils;
import sjmhrp.utils.linear.Matrix3d;
import sjmhrp.utils.linear.Quaternion;
import sjmhrp.utils.linear.Transform;
import sjmhrp.utils.linear.Vector3d;

public class EPA {

	static final int MAX_SUPPORT_POINTS = 100;
	static final int MAX_FACETS = 200;

	ConvexShape c1;
	ConvexShape c2;
	Transform t1;
	Transform t2;
	Transform b2Tob1;
	Matrix3d rotateTob2;

	Contact contact;

	public EPA(ConvexShape c1, ConvexShape c2, Transform t1, Transform t2, Contact c) {
		this.c1 = c1;
		this.c2 = c2;
		this.t1 = t1;
		this.t2 = t2;
		contact = c;
		b2Tob1 = Transform.mul(t1.getInverse(),t2);
		rotateTob2 = t2.getInverse().orientation.getRotationMatrix().to3Matrix().mul(t1.orientation.getRotationMatrix().to3Matrix());
	}

	public void process(Simplex simplex) {
		final Vector3d[] supPointsA = new Vector3d[MAX_SUPPORT_POINTS];
		final Vector3d[] supPointsB = new Vector3d[MAX_SUPPORT_POINTS];
		final Vector3d[] points = new Vector3d[MAX_SUPPORT_POINTS];
		final TriangleStore triangleStore = new TriangleStore();
		final Queue<Triangle> triangleHeap = new PriorityQueue<Triangle>(MAX_FACETS,(Triangle t1, Triangle t2)->t1.getDistSquare()==t2.getDistSquare()?0:t1.getDistSquare()>t2.getDistSquare()?1:-1);
		int nbVertices = simplex.getSimplex(supPointsA,supPointsB,points);
		final double tolerance = Globals.EPSILON*simplex.getMaxLengthSquared();
		int nbTriangles = 0;
		triangleStore.clear();
		switch(nbVertices) {
			case 1:
				return;
			case 2: {
				final Vector3d d = Vector3d.sub(points[1], points[0]).getUnit();
				final Vector3d minAxis = GeometryUtils.minAxis(d);
				final double sin60 = 0.86602540378443864676372317075294;
				final Quaternion rotQuat = new Quaternion(d.x*sin60,d.y*sin60,d.z*sin60,0.5);
				final Matrix3d rotMat = rotQuat.getRotationMatrix().to3Matrix();
				final Vector3d v1 = Vector3d.cross(d,minAxis);
				final Vector3d v2 = Matrix3d.transform(rotMat,v1);
				final Vector3d v3 = Matrix3d.transform(rotMat,v2);
				supPointsA[2] = supportA(v1);
				supPointsB[2] = supportB(v1);
				points[2] = Vector3d.sub(supPointsA[2],supPointsB[2]);
				supPointsA[3] = supportA(v2);
				supPointsB[3] = supportB(v2);
				points[3] = Vector3d.sub(supPointsA[3],supPointsB[3]);
				supPointsA[4] = supportA(v3);
				supPointsB[4] = supportB(v3);
				points[4] = Vector3d.sub(supPointsA[4],supPointsB[4]);
				if(isOriginInTetrahedron(points[0],points[2],points[3],points[4])==0) {
					supPointsA[1].set(supPointsA[4]);
					supPointsB[1].set(supPointsB[4]);
					points[1].set(points[4]);
				} else if(isOriginInTetrahedron(points[1],points[2],points[3],points[4])==0) {
					supPointsA[0].set(supPointsA[4]);
					supPointsB[0].set(supPointsB[4]);
					points[0].set(points[4]);
				} else {
					return;
				}
				nbVertices = 4;
			}
			case 4: {
				final int badVertex = isOriginInTetrahedron(points[0],points[1],points[2],points[3]);
				if(badVertex==0) {
					final Triangle face0 = triangleStore.newTriangle(points,0,1,2);
					final Triangle face1 = triangleStore.newTriangle(points,0,3,1);
					final Triangle face2 = triangleStore.newTriangle(points,0,2,3);
					final Triangle face3 = triangleStore.newTriangle(points,1,3,2);
					if(face0==null||face1==null||face2==null||face3==null||face0.getDistSquare()<=0||face1.getDistSquare()<=0||face2.getDistSquare()<=0||face3.getDistSquare()<=0)return;
					Triangle.link(new Edge(face0,0),new Edge(face1,2));
					Triangle.link(new Edge(face0,1),new Edge(face3,2));
					Triangle.link(new Edge(face0,2),new Edge(face2,0));
					Triangle.link(new Edge(face1,0),new Edge(face2,2));
					Triangle.link(new Edge(face1,1),new Edge(face3,0));
					Triangle.link(new Edge(face2,1),new Edge(face3,1));
					nbTriangles = addFaceCandidate(face0,triangleHeap,nbTriangles,Float.MAX_VALUE);
					nbTriangles = addFaceCandidate(face1,triangleHeap,nbTriangles,Float.MAX_VALUE);
					nbTriangles = addFaceCandidate(face2,triangleHeap,nbTriangles,Float.MAX_VALUE);
					nbTriangles = addFaceCandidate(face3,triangleHeap,nbTriangles,Float.MAX_VALUE);
					break;
				}
				if(badVertex<4) {
					supPointsA[badVertex-1].set(supPointsA[3]);
					supPointsB[badVertex-1].set(supPointsB[3]);
					points[badVertex-1].set(points[3]);
				}
				nbVertices = 3;
			}
			case 3: {
				final Vector3d v1 = Vector3d.sub(points[1],points[0]);
				final Vector3d v2 = Vector3d.sub(points[2],points[0]);
				final Vector3d n = Vector3d.cross(v1,v2);
				supPointsA[3] = supportA(n);
				supPointsB[3] = supportB(n);
				points[3] = Vector3d.sub(supPointsA[3],supPointsB[3]);
				supPointsA[4] = supportA(n.getNegative());
				supPointsB[4] = supportB(n.getNegative());
				points[4] = Vector3d.sub(supPointsA[4],supPointsB[4]);
				final Triangle face0 = triangleStore.newTriangle(points,0,1,3);
				final Triangle face1 = triangleStore.newTriangle(points,1,2,3);
				final Triangle face2 = triangleStore.newTriangle(points,2,0,3);
				final Triangle face3 = triangleStore.newTriangle(points,0,2,4);
				final Triangle face4 = triangleStore.newTriangle(points,2,1,4);
				final Triangle face5 = triangleStore.newTriangle(points,1,0,4);
				if(face0==null||face1==null||face2==null||face3==null||face4==null||face5==null||face0.getDistSquare()<=0||face1.getDistSquare()<=0||face2.getDistSquare()<=0||face3.getDistSquare()<=0||face4.getDistSquare()<=0||face5.getDistSquare()<=0)return;
				Triangle.link(new Edge(face0,1),new Edge(face1,2));
				Triangle.link(new Edge(face1,1),new Edge(face2,2));
				Triangle.link(new Edge(face2,1),new Edge(face0,2));
				Triangle.link(new Edge(face0,0),new Edge(face5,0));
				Triangle.link(new Edge(face1,0),new Edge(face4,0));
				Triangle.link(new Edge(face2,0),new Edge(face3,0));
				Triangle.link(new Edge(face3,1),new Edge(face4,2));
				Triangle.link(new Edge(face4,1),new Edge(face5,2));
				Triangle.link(new Edge(face5,1),new Edge(face3,2));
				nbTriangles = addFaceCandidate(face0,triangleHeap,nbTriangles,Float.MAX_VALUE);
				nbTriangles = addFaceCandidate(face1,triangleHeap,nbTriangles,Float.MAX_VALUE);
				nbTriangles = addFaceCandidate(face2,triangleHeap,nbTriangles,Float.MAX_VALUE);
				nbTriangles = addFaceCandidate(face3,triangleHeap,nbTriangles,Float.MAX_VALUE);
				nbTriangles = addFaceCandidate(face4,triangleHeap,nbTriangles,Float.MAX_VALUE);
				nbTriangles = addFaceCandidate(face5,triangleHeap,nbTriangles,Float.MAX_VALUE);
				nbVertices = 5;
			}
			break;
		}
		if(nbTriangles==0)return;
		Triangle triangle;
		double upperBoundSquarePenDepth = Float.MAX_VALUE;
		do {
			triangle = triangleHeap.remove();
			nbTriangles--;
			if(!triangle.isObsolete()) {
				if(nbVertices == MAX_SUPPORT_POINTS)break;
				supPointsA[nbVertices] = supportA(triangle.getClosestPoint());
				supPointsB[nbVertices] = supportB(triangle.getClosestPoint());
				points[nbVertices] = Vector3d.sub(supPointsA[nbVertices],supPointsB[nbVertices]);
				final int indexNewVertex = nbVertices;
				nbVertices++;
				final double wDotv = points[indexNewVertex].dot(triangle.getClosestPoint());
				if(wDotv<=-Globals.EPSILON)throw new IllegalStateException("wDotv must be greater than zero");
				final double wDotvSquare = wDotv*wDotv/triangle.getDistSquare();
				if(wDotvSquare<upperBoundSquarePenDepth)upperBoundSquarePenDepth=wDotvSquare;
				final double error = wDotv - triangle.getDistSquare();
				if(error<Math.max(tolerance,Globals.REL_ERROR_SQUARE*wDotv)||points[indexNewVertex].equals(points[triangle.get(0)])||points[indexNewVertex].equals(points[triangle.get(1)])||points[indexNewVertex].equals(points[triangle.get(2)]))break;
				int i = triangleStore.getNbTriangles();
				if(!triangle.computeSilhouette(points,indexNewVertex,triangleStore))break;
				while(i!=triangleStore.getNbTriangles()) {
					final Triangle newTriangle = triangleStore.get(i);
					nbTriangles = addFaceCandidate(newTriangle,triangleHeap,nbTriangles,upperBoundSquarePenDepth);
					i++;
				}
			}
		} while(nbTriangles>0&&triangleHeap.element().getDistSquare()<=upperBoundSquarePenDepth);
		Vector3d normal = Matrix3d.transform(t1.orientation.getRotationMatrix().to3Matrix(),triangle.getClosestPoint());
		final double depth = normal.length();
		final Vector3d pA = triangle.computeClosestPointOfObject(supPointsA);
		final Vector3d pB = Transform.transform(b2Tob1.getInverse(),triangle.computeClosestPointOfObject(supPointsB));
		final Vector3d globalA = Transform.transform(t1,pA);
		final Vector3d globalB = Transform.transform(t2,pB);
		normal.normalize();
		contact.setOutput(true,pA,pB,globalA,globalB,normal,depth);
	}

	Vector3d supportA(Vector3d v) {
		return c1.getLocalSupportPoint(v);
	}

	Vector3d supportB(Vector3d v) {
		return Transform.transform(b2Tob1,c2.getLocalSupportPoint(Matrix3d.transform(rotateTob2,v.getNegative())));
	}

	static int isOriginInTetrahedron(Vector3d p1, Vector3d p2, Vector3d p3, Vector3d p4) {
		final Vector3d normal1 = Vector3d.cross(Vector3d.sub(p2,p1),Vector3d.sub(p3,p1));
		if(normal1.dot(p1)+Globals.EPSILON>0==normal1.dot(p4)+Globals.EPSILON>0)return 4;
		final Vector3d normal2 = Vector3d.cross(Vector3d.sub(p4,p2),Vector3d.sub(p3,p2));
		if(normal2.dot(p2)+Globals.EPSILON>0==normal2.dot(p1)+Globals.EPSILON>0)return 1;;
		final Vector3d normal3 = Vector3d.cross(Vector3d.sub(p4,p3),Vector3d.sub(p1,p3));
		if(normal3.dot(p3)+Globals.EPSILON>0==normal3.dot(p2)+Globals.EPSILON>0)return 2;
		final Vector3d normal4 = Vector3d.cross(Vector3d.sub(p2,p4),Vector3d.sub(p1,p4));
		if(normal4.dot(p4)+Globals.EPSILON>0==normal4.dot(p3)+Globals.EPSILON>0)return 3;
		return 0;
	}

	static int addFaceCandidate(Triangle triangle, Queue<Triangle> heap, int nbTriangles, double upperBoundSquarePenDepth) {
		if(triangle.isClosestPointInternal()&&triangle.getDistSquare()<=upperBoundSquarePenDepth) {
			heap.add(triangle);
			nbTriangles++;
		}
		return nbTriangles;
	}
}