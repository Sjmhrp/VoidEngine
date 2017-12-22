package sjmhrp.io.colladaloader;

import java.util.ArrayList;
import java.util.Arrays;

import sjmhrp.io.xmlLoader.XMLNode;

public class SkinLoader {

	private final XMLNode skinningData;
	private final int maxWeights;
	
	public SkinLoader(XMLNode node, int maxWeights) {
		skinningData=node.getChild("controller").getChild("skin");
		this.maxWeights=maxWeights;
	}
	
	public SkinningData getSkinningData() {
		ArrayList<String> jointsList = loadJointsList();
		double[] weights = loadWeights();
		XMLNode node = skinningData.getChild("vertex_weights");
		int[] effectorJointCounts = getEffectiveJointCounts(node);
		ArrayList<VertexSkinData> vertexWeights = getSkinData(node,effectorJointCounts,weights);
		return new SkinningData(jointsList,vertexWeights);
	}

	ArrayList<String> loadJointsList() {
		return new ArrayList<String>(Arrays.asList(skinningData.getChild("source","id",skinningData.getChild("vertex_weights").getChild("input","semantic","JOINT").getAttribute("source").substring(1)).getChild("Name_array").getData().split(" ")));
	}
	
	double[] loadWeights() {
		return Arrays.stream(skinningData.getChild("source","id",skinningData.getChild("vertex_weights").getChild("input","semantic","WEIGHT").getAttribute("source").substring(1)).getChild("float_array").getData().split(" ")).mapToDouble(Double::parseDouble).toArray();
	}
	
	
	int[] getEffectiveJointCounts(XMLNode node) {
		return Arrays.stream(node.getChild("vcount").getData().split(" ")).mapToInt(Integer::parseInt).toArray();
	}
	
	ArrayList<VertexSkinData> getSkinData(XMLNode node, int[] counts, double[] weights) {
		String[] data = node.getChild("v").getData().split(" ");
		ArrayList<VertexSkinData> skinningData = new ArrayList<VertexSkinData>();
		int pointer = 0;
		for(int count : counts) {
			VertexSkinData skinData = new VertexSkinData();
			for(int i = 0; i < count; i++) {
				skinData.addJointWeight(Integer.parseInt(data[pointer++]),weights[Integer.parseInt(data[pointer++])]);
			}
			skinData.limitJointNumber(maxWeights);
			skinningData.add(skinData);
		}
		return skinningData;
	}
}