package sjmhrp.physics.collision.narrowphase;

import sjmhrp.linear.Vector3d;

public class Triangle {

	private final int[] indicesVertices = new int[4];
	private final Edge[] edges = new Edge[3];
	private boolean isObsolete = false;
	private double det;
	private Vector3d closestPoint = new Vector3d();
	private double lambda1;
	private double lambda2;
	private double distSquare;

	public Triangle() {
		this(0,0,0);
	}

	public Triangle(int v1, int v2, int v3) {
		indicesVertices[0] = v1;
		indicesVertices[1] = v2;
		indicesVertices[2] = v3;
	}

	public Edge getAdjacentEdge(int index) {
		return edges[index];
	}

	public void setAdjacentEdge(int index, Edge e) {
		edges[index] = e;
	}

	public double getDistSquare() {
		return distSquare;
	}
	
	public void setObsolete(boolean isObsolete) {
		this.isObsolete = isObsolete;
	}

	public boolean isObsolete() {
		return isObsolete;
	}

	public Vector3d getClosestPoint() {
		return closestPoint;
	}

	public boolean isClosestPointInternal() {
		return lambda1>=0&&lambda2>=0&&(lambda1+lambda2)<=det;
	}
	
	public boolean isVisibleFromVertex(Vector3d[] vertices, int index) {
		final Vector3d closestToVertex = Vector3d.sub(vertices[index],closestPoint);
		return closestPoint.dot(closestToVertex)>0;
	}

	public Vector3d computeClosestPointOfObject(Vector3d[] supportPoints) {
		final Vector3d p0 = supportPoints[indicesVertices[0]];
		return Vector3d.add(p0,Vector3d.scale(1f/det,Vector3d.add(
		Vector3d.scale(lambda1,Vector3d.sub(supportPoints[indicesVertices[1]],p0)),
		Vector3d.scale(lambda2,Vector3d.sub(supportPoints[indicesVertices[2]],p0)))));
	}

	public int get(int index) {
		return indicesVertices[index];
	}

	public boolean computeClosestPoint(Vector3d[] vertices) {
		final Vector3d p0 = vertices[indicesVertices[0]];
		final Vector3d v1 = Vector3d.sub(vertices[indicesVertices[1]],p0);
		final Vector3d v2 = Vector3d.sub(vertices[indicesVertices[2]],p0);
		final double v1Dotv1 = v1.lengthSquared();
		final double v1Dotv2 = v1.dot(v2);
		final double v2Dotv2 = v2.lengthSquared();
		final double p0Dotv1 = p0.dot(v1);
		final double p0Dotv2 = p0.dot(v2);
		det = v1Dotv1*v2Dotv2 - v1Dotv2*v1Dotv2;
		lambda1 = p0Dotv2*v1Dotv2 - p0Dotv1*v2Dotv2;
		lambda2 = p0Dotv1*v1Dotv2 - p0Dotv2*v1Dotv1;
		if(det>0) {
			closestPoint.set(Vector3d.add(p0,Vector3d.scale(1f/det,Vector3d.add(Vector3d.scale(lambda1,v1),Vector3d.scale(lambda2,v2)))));
			distSquare = closestPoint.lengthSquared();
			return true;
		}
		return false;
	}

	public boolean computeSilhouette(Vector3d[] vertices, int indexNewVertex, TriangleStore triangleStore) {
		final int first = triangleStore.getNbTriangles();
		setObsolete(true);
		final boolean result = edges[0].computeSilhouette(vertices,indexNewVertex,triangleStore)&&
				edges[1].computeSilhouette(vertices,indexNewVertex,triangleStore)&&
				edges[2].computeSilhouette(vertices,indexNewVertex,triangleStore);
		if(!result)return false;
		for(int i = first, j = triangleStore.getNbTriangles() - 1; i != triangleStore.getNbTriangles(); j=i++) {
			final Triangle triangle = triangleStore.get(i);
			halfLink(triangle.getAdjacentEdge(1),new Edge(triangle,1));
			if(!link(new Edge(triangle,0), new Edge(triangleStore.get(j),2)))return false;
		}
		return true;
	}			
	
	public static boolean link(Edge edge0, Edge edge1) {
		final boolean possible = (edge0.getSourceVertex()==edge1.getTargetVertex())&&(edge0.getTargetVertex()==edge1.getSourceVertex());
		if(!possible)return false;
		edge0.getOwner().edges[edge0.getIndex()]=edge1;
		edge1.getOwner().edges[edge1.getIndex()]=edge0;
		return true;
	}
	
	public static void halfLink(Edge edge0, Edge edge1) {			
		edge0.getOwner().edges[edge0.getIndex()]=edge1;
	}
}