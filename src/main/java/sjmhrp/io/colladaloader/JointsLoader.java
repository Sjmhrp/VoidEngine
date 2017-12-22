package sjmhrp.io.colladaloader;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.BufferUtils;

import sjmhrp.core.Globals;
import sjmhrp.io.xmlLoader.XMLNode;
import sjmhrp.utils.linear.Matrix4d;

public class JointsLoader {

	private XMLNode armatureData;
	private ArrayList<String> order;
	private int jointCount = 0;
	
	public JointsLoader(XMLNode sceneNode, ArrayList<String> order) {
		armatureData = sceneNode.getChild("visual_scene").getChild("node","id","Armature");
		this.order = order;
	}
	
	public JointsData extractBoneData() {
		XMLNode headNode = armatureData.getChild("node");
		JointData headJoint = loadJointData(headNode,true);
		return new JointsData(jointCount,headJoint);
	}
	
	private JointData loadJointData(XMLNode jointNode, boolean isRoot) {
		JointData joint = extractMainJointData(jointNode,isRoot);
		for(XMLNode child : jointNode.getChildren("node")) {
			joint.addChild(loadJointData(child,false));
		}
		return joint;
	}
	
	private JointData extractMainJointData(XMLNode jointNode, boolean isRoot) {
		String nameID = jointNode.getAttribute("id");
		int index = order.indexOf(nameID);
		String[] matrixData = jointNode.getChild("matrix").getData().split(" ");
		Matrix4d matrix = new Matrix4d().load(convert(matrixData));
		matrix.transpose();
		if(isRoot)matrix=Matrix4d.mul(Globals.BLENDER_CORRECTION,matrix);
		jointCount++;
		return new JointData(index,nameID,matrix);
	}
	
	private DoubleBuffer convert(String[] data) {
		DoubleBuffer buffer = BufferUtils.createDoubleBuffer(16);
		buffer.put(Arrays.stream(data).mapToDouble(Double::parseDouble).toArray());
		buffer.flip();
		return buffer;
	}
}