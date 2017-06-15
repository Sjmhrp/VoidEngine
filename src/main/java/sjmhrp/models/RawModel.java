package sjmhrp.models;

public class RawModel {

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