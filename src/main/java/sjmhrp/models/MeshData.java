package sjmhrp.models;

import sjmhrp.linear.Vector3d;
import sjmhrp.render.Loader;

public class MeshData {

	int[] indices;
	int indexVbo;
	double[] vertices;
	int vertexVbo;
	double[] uvs;
	int uvVbo;
	double[] normals;
	int normalVbo;
	
	public MeshData(double[] vertices, int vertexVbo) {
		this.vertices=vertices;
		this.vertexVbo=vertexVbo;
	}
	
	public MeshData(double[] vertices, int vertexVbo, double[] uvs, int uvVbo, double[] normals, int normalVbo, int[] indices, int indexVbo) {
		this.indices=indices;
		this.indexVbo=indexVbo;
		this.vertices=vertices;
		this.vertexVbo=vertexVbo;
		this.uvs=uvs;
		this.uvVbo=uvVbo;
		this.normals=normals;
		this.normalVbo=normalVbo;
	}
	
	public void updateNormals() {
		for(int i = 0; i < indices.length/3; i++) {
			Vector3d v1 = new Vector3d(vertices[indices[i*3]*3],vertices[indices[i*3]*3+1],vertices[indices[i*3]*3+2]);
			Vector3d v2 = new Vector3d(vertices[indices[i*3+1]*3],vertices[indices[i*3+1]*3+1],vertices[indices[i*3+1]*3+2]);
			Vector3d v3 = new Vector3d(vertices[indices[i*3+2]*3],vertices[indices[i*3+2]*3+1],vertices[indices[i*3+2]*3+2]);
			Vector3d normal = Vector3d.cross(Vector3d.sub(v2,v1),Vector3d.sub(v3,v1));
			normal.normalize();
			normals[indices[i*3]*3] = normal.x;
			normals[indices[i*3+1]*3+1] = normal.y;
			normals[indices[i*3+2]*3+2] = normal.z;
		}
		Loader.updateVbo(normalVbo, 2, 3, normals);
	}

	public int[] getIndices() {
		return indices;
	}

	public int getIndexVbo() {
		return indexVbo;
	}

	public double[] getVertices() {
		return vertices;
	}

	public int getVertexVbo() {
		return vertexVbo;
	}

	public double[] getUvs() {
		return uvs;
	}

	public int getUvVbo() {
		return uvVbo;
	}

	public double[] getNormals() {
		return normals;
	}

	public int getNormalVbo() {
		return normalVbo;
	}
}