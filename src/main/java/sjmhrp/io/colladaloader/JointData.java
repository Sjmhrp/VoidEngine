package sjmhrp.io.colladaloader;

import java.util.ArrayList;

import sjmhrp.utils.linear.Matrix4d;

public class JointData {

	public final int index;
	public final String name;
	public final Matrix4d transform;
	
	public final ArrayList<JointData> children = new ArrayList<JointData>();
	
	public JointData(int index, String name, Matrix4d transform) {
		this.index=index;
		this.name=name;
		this.transform=transform;
	}
	
	public void addChild(JointData joint) {
		children.add(joint);
	}
}