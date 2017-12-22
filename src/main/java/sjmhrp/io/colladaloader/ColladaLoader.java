package sjmhrp.io.colladaloader;

import sjmhrp.io.xmlLoader.XMLNode;
import sjmhrp.io.xmlLoader.XMLParser;
import sjmhrp.render.models.MeshData;

public class ColladaLoader {

	public static AnimatedModelData loadColladaModel(String filePath, int maxWeights) {
		XMLNode node = XMLParser.loadXMLFile(filePath);
		SkinLoader skinLoader = new SkinLoader(node.getChild("library_controllers"),maxWeights);
		SkinningData skinningData = skinLoader.getSkinningData();
		JointsLoader jointsLoader = new JointsLoader(node.getChild("library_visual_scenes"),skinningData.jointOrder);
		JointsData jointsData = jointsLoader.extractBoneData();
		GeometryLoader geometryLoader = new GeometryLoader(node.getChild("library_geometries"),skinningData.verticesSkinData);
		MeshData meshData = geometryLoader.extractModelData();
		return new AnimatedModelData(meshData,jointsData);
	}
	
	public static AnimationData loadColladaAnimation(String filePath) {
		XMLNode node = XMLParser.loadXMLFile(filePath);
		XMLNode animNode = node.getChild("library_animations");
		XMLNode jointsNode = node.getChild("library_visual_scenes");
		AnimationLoader loader = new AnimationLoader(animNode,jointsNode);
		return loader.extractAnimation();
	}
}