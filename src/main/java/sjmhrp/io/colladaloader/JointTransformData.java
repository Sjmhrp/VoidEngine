package sjmhrp.io.colladaloader;

import sjmhrp.utils.linear.Matrix4d;

public class JointTransformData {

	public final String name;
	public final Matrix4d transform;
	
	public JointTransformData(String name, Matrix4d transform) {
		this.name=name;
		this.transform=transform;
	}
}