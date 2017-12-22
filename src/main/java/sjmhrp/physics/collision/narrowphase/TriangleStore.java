package sjmhrp.physics.collision.narrowphase;

import sjmhrp.utils.linear.Vector3d;

public class TriangleStore {

	public static final int MAX_TRIANGLES = 200;
	private final Triangle[] triangles = new Triangle[MAX_TRIANGLES];
	private int nbTriangles;
	
	public void clear() {
		nbTriangles = 0;
	}

	public int getNbTriangles() {
		return nbTriangles;
	}

	public void setNbTriangles(int backup) {
		nbTriangles = backup;
	}

	public Triangle last() {
		return triangles[nbTriangles-1];
	}

	public Triangle newTriangle(Vector3d[] vertices, int v0, int v1, int v2) {
		Triangle newTriangle = null;
		if(nbTriangles<MAX_TRIANGLES) {
			newTriangle = new Triangle(v0,v1,v2);
			triangles[nbTriangles++] = newTriangle;
			if(!newTriangle.computeClosestPoint(vertices)) {
				nbTriangles--;
				newTriangle=null;
				triangles[nbTriangles] = null;
			}
		}
		return newTriangle;
	}
	
	public Triangle get(int index) {
		return triangles[index];
	}
}