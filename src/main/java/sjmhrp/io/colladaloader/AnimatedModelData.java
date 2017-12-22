package sjmhrp.io.colladaloader;

import sjmhrp.render.models.MeshData;

public class AnimatedModelData {

	private final JointsData joints;
	private final MeshData mesh;
	
	public AnimatedModelData(MeshData mesh, JointsData joints) {
		this.joints=joints;
		this.mesh=mesh;
	}
	
	public JointsData getJointsData() {
		return joints;
	}
	
	public MeshData getMeshData() {
		return mesh;
	}
}