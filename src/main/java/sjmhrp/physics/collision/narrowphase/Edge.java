package sjmhrp.physics.collision.narrowphase;

import sjmhrp.linear.Vector3d;

public class Edge {

	private Triangle owner;
	private int index;

	public Edge() {
		this(null,0);
	}

	public Edge(Triangle owner, int index) {
		this.owner = owner;
		this.index = index;
	}

	public Edge(Edge e) {
		this.owner = e.owner;
		this.index = e.index;
	}
	
	public Triangle getOwner() {
		return owner;
	}

	public int getIndex() {
		return index;
	}

	public void set(Edge e) {
		this.owner = e.owner;
		this.index = e.index;
	}

	public int getSourceVertex() {
		return owner.get(index);
	}

	public int getTargetVertex() {
		return owner.get(nextCCW(index));
	}

	public boolean computeSilhouette(Vector3d[] vertices, int indexNewVertex, TriangleStore triangleStore) {
		if(!owner.isObsolete()) {
			if(!owner.isVisibleFromVertex(vertices,indexNewVertex)) {
				Triangle triangle = triangleStore.newTriangle(vertices,indexNewVertex,getTargetVertex(),getSourceVertex());
				if(triangle!=null) {
					Triangle.halfLink(new Edge(triangle,1),this);
					return true;
				}
				return false;
			} else {
				owner.setObsolete(true);
				int backup = triangleStore.getNbTriangles();
				if(!owner.getAdjacentEdge(nextCCW(this.index)).computeSilhouette(vertices,indexNewVertex,triangleStore)) {
					owner.setObsolete(false);
					Triangle triangle = triangleStore.newTriangle(vertices,indexNewVertex,getTargetVertex(),getSourceVertex());
					if(triangle!=null) {
						Triangle.halfLink(new Edge(triangle,1),this);
						return true;
					}
					return false;
				} else if(!owner.getAdjacentEdge(prevCCW(this.index)).computeSilhouette(vertices,indexNewVertex,triangleStore)) {
					owner.setObsolete(false);
					triangleStore.setNbTriangles(backup);
					Triangle triangle = triangleStore.newTriangle(vertices,indexNewVertex,getTargetVertex(),getSourceVertex());
					if(triangle!=null) {
						Triangle.halfLink(new Edge(triangle,1),this);
						return true;
					}
					return false;
				}
			}
		}
		return true;
	}

	static int nextCCW(int i) {
		return (i+1)%3;
	}

	static int prevCCW(int i) {
		return (i+2)%3;
	}
}