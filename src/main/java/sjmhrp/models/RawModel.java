package sjmhrp.models;

import java.io.Serializable;

public class RawModel implements Serializable{
	
	private static final long serialVersionUID = -7351602918362249908L;
	
	int vaoId;
	int vertexCount;
	MeshData meshData;
	
	public RawModel(int vaoId, int vertexCount, MeshData meshData) {
		this.vaoId=vaoId;
		this.vertexCount=vertexCount;
		this.meshData=meshData;
	}

	public int getVaoId() {
		return vaoId;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public MeshData getMeshData() {
		return meshData;
	}
}