package sjmhrp.io.colladaloader;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.BufferUtils;

import sjmhrp.core.Globals;
import sjmhrp.io.xmlLoader.XMLNode;
import sjmhrp.utils.linear.Matrix4d;

public class AnimationLoader {

	private XMLNode animationData;
	private XMLNode jointHierarchy;
	
	public AnimationLoader(XMLNode animationData, XMLNode jointHierarchy) {
		this.animationData=animationData;
		this.jointHierarchy=jointHierarchy;
	}
	
	public AnimationData extractAnimation() {
		String root = rootJointName();
		double[] times = getKeyTimes();
		double duration = times[times.length-1];
		KeyFrameData[] keyFrames = initKeyFrames(times);
		ArrayList<XMLNode> animationNodes = animationData.getChildren("animation");
		for(XMLNode jointNode : animationNodes) {
			loadJointTransforms(keyFrames,jointNode,root);
		}
		return new AnimationData(duration,keyFrames);
	}

	private double[] getKeyTimes() {
		XMLNode timeData = animationData.getChild("animation").getChild("source").getChild("float_array");
		String[] rawTimes = timeData.getData().split(" ");
		return Arrays.stream(rawTimes).mapToDouble(Double::parseDouble).toArray();
	}

	private KeyFrameData[] initKeyFrames(double[] times) {
		KeyFrameData[] frames = new KeyFrameData[times.length];
		for(int i = 0; i < frames.length; i++) {
			frames[i]=new KeyFrameData(times[i]);
		}
		return frames;
	}
	
	private void loadJointTransforms(KeyFrameData[] frames, XMLNode jointData, String rootNodeID) {
		String jointNameID = getJointName(jointData);
		String dataID = getDataID(jointData);
		XMLNode transformData = jointData.getChild("source","id",dataID);
		String[] rawData = transformData.getChild("float_array").getData().split(" ");
		processTransforms(jointNameID,rawData,frames,jointNameID.equals(rootNodeID));
	}
	
	private String getDataID(XMLNode jointData) {
		return jointData.getChild("sampler").getChild("input","semantic","OUTPUT").getAttribute("source").substring(1);
	}
	
	private String getJointName(XMLNode jointData) {
		XMLNode channelNode = jointData.getChild("channel");
		String data = channelNode.getAttribute("target");
		return data.split("/")[0];
	}
	
	private void processTransforms(String jointName, String[] data, KeyFrameData[] keyFrames, boolean root) {
		DoubleBuffer buffer = BufferUtils.createDoubleBuffer(16);
		double[] matrixData = new double[16];
		for(int i = 0; i < keyFrames.length; i++) {
			for(int j = 0; j <16; j++)matrixData[j]=Double.parseDouble(data[i*16+j]);
			buffer.clear();
			buffer.put(matrixData);
			buffer.flip();
			Matrix4d transform = new Matrix4d();
			transform.load(buffer);
			transform.transpose();
			if(root)transform=Matrix4d.mul(Globals.BLENDER_CORRECTION,transform);
			keyFrames[i].addTransform(new JointTransformData(jointName,transform));
		}
	}
	
	private String rootJointName() {
		XMLNode tree = jointHierarchy.getChild("visual_scene").getChild("node","id","Armature");
		return tree.getChild("node").getAttribute("id");
	}
}