package sjmhrp.render.models;

import java.io.Serializable;

import sjmhrp.render.Loader;
import sjmhrp.utils.linear.Vector3d;

public class MeshData implements Serializable{
	
	private static final long serialVersionUID = 6123086145398086517L;
	
	int[] indices;
	int indexVbo;
	double[] vertices;
	int vertexVbo;
	double[] uvs;
	int uvVbo;
	double[] normals;
	int normalVbo;
	int[] jointIDs;
	int jointIDVbo;
	double[] weights;
	int weightsVbo;
	double furthest;
	
	public MeshData(double[] vertices, int[] indices, double[] normals) {
		this.vertices=vertices;
		this.indices=indices;
		this.normals=normals;
	}
	
	public MeshData(double[] vertices, int vertexVbo) {
		this.vertices=vertices;
		this.vertexVbo=vertexVbo;
	}
	
	public MeshData(double[] vertices, int vertexVbo, double[] uvs, int uvVbo) {
		this.vertices=vertices;
		this.vertexVbo=vertexVbo;
		this.uvs=uvs;
		this.uvVbo=uvVbo;
	}
	
	public MeshData(double[] vertices, int vertexVbo, double[] normals, int normalVbo, int[] indices, int indexVbo) {
		this.indices=indices;
		this.indexVbo=indexVbo;
		this.vertices=vertices;
		this.vertexVbo=vertexVbo;
		this.normals=normals;
		this.normalVbo=normalVbo;
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
	
	public MeshData(double[] vertices, double[] uvs, double[] normals, int[] indices, int[] jointIDs, double[] weights, double furthest) {
		this.vertices=vertices;
		this.uvs=uvs;
		this.normals=normals;
		this.indices=indices;
		this.jointIDs=jointIDs;
		this.weights=weights;
		this.furthest=furthest;
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
	
	public int[] getJointIDs() {
		return jointIDs;
	}
	
	public double[] getWeights() {
		return weights;
	}
	
	public double getFurthest() {
		return furthest;
	}
}