package sjmhrp.io.colladaloader;

import java.util.ArrayList;

import sjmhrp.core.Globals;
import sjmhrp.io.xmlLoader.XMLNode;
import sjmhrp.render.models.MeshData;
import sjmhrp.utils.linear.Vector2d;
import sjmhrp.utils.linear.Vector3d;

public class GeometryLoader {

	private final XMLNode meshData;
	
	private final ArrayList<VertexSkinData> vertexWeights;
	
	private double[] verticesArray;
	private double[] normalsArray;
	private double[] texturesArray;
	private int[] indicesArray;
	private int[] jointIDsArray;
	private double[] weightsArray;
	
	ArrayList<Vertex> vertices = new ArrayList<Vertex>();
	ArrayList<Vector2d> textures = new ArrayList<Vector2d>();
	ArrayList<Vector3d> normals = new ArrayList<Vector3d>();
	ArrayList<Integer> indices = new ArrayList<Integer>();
	
	public GeometryLoader(XMLNode node, ArrayList<VertexSkinData> weights) {
		vertexWeights=weights;
		meshData=node.getChild("geometry").getChild("mesh");
	}
	
	public MeshData extractModelData() {
		readData();
		assembleVertices();
		removeUnused();
		initArrays();
		convertData();
		convertIndices();
		return new MeshData(verticesArray,texturesArray,normalsArray,indicesArray,jointIDsArray,weightsArray,1);
	}
	
	private void readData() {
		readPositions();
		readNormals();
		readTextureCoords();
	}
	
	private void readPositions() {
		String positionsID = meshData.getChild("vertices").getChild("input").getAttribute("source").substring(1);
		XMLNode positionsData = meshData.getChild("source","id",positionsID).getChild("float_array");
		int count = Integer.parseInt(positionsData.getAttribute("count"));
		String[] posData = positionsData.getData().split(" ");
		for(int i = 0; i < count/3; i++) {
			double x = Double.parseDouble(posData[i*3]);
			double y = Double.parseDouble(posData[i*3+1]);
			double z = Double.parseDouble(posData[i*3+2]);
			Vector3d pos = new Vector3d(x,y,z);
			Globals.BLENDER_CORRECTION.transform(pos);
			vertices.add(new Vertex(vertices.size(),pos,vertexWeights.get(vertices.size())));
		}
	}
	
	private void readNormals() {
		String normalsID = meshData.getChild("polylist").getChild("input","semantic","NORMAL").getAttribute("source").substring(1);
		XMLNode normalsData = meshData.getChild("source","id",normalsID).getChild("float_array");
		int count = Integer.parseInt(normalsData.getAttribute("count"));
		String[] normData = normalsData.getData().split(" ");
		for(int i = 0; i < count/3; i++) {
			double x = Double.parseDouble(normData[i*3]);
			double y = Double.parseDouble(normData[i*3+1]);
			double z = Double.parseDouble(normData[i*3+2]);
			Vector3d norm = new Vector3d(x,y,z);
			Globals.BLENDER_CORRECTION.transform(norm);
			normals.add(norm);
		}
	}
	
	private void readTextureCoords() {
		String texCoordsID = meshData.getChild("polylist").getChild("input","semantic","TEXCOORD").getAttribute("source").substring(1);
		XMLNode texCoordsData = meshData.getChild("source","id",texCoordsID).getChild("float_array");
		int count = Integer.parseInt(texCoordsData.getAttribute("count"));
		String[] texData = texCoordsData.getData().split(" ");
		for(int i = 0; i < count/2; i++) {
			double s = Double.parseDouble(texData[i*2]);
			double t = Double.parseDouble(texData[i*2+1]);
			textures.add(new Vector2d(s,t));
		}
	}
	
	private void assembleVertices() {
		XMLNode poly = meshData.getChild("polylist");
		int typeCount = poly.getChildren("input").size();
		String[] indexData = poly.getChild("p").getData().split(" ");
		for(int i = 0; i < indexData.length/typeCount; i++) {
			int positionIndex = Integer.parseInt(indexData[i*typeCount]);
			int normalIndex = Integer.parseInt(indexData[i*typeCount+1]);
			int texCoordIndex = Integer.parseInt(indexData[i*typeCount+2]);
			processVertex(positionIndex,normalIndex,texCoordIndex);
		}
	}
	
	private Vertex processVertex(int posIndex, int normIndex, int texIndex) {
		Vertex vertex = vertices.get(posIndex);
		if(!vertex.isSet()) {
			vertex.setTextureIndex(texIndex);
			vertex.setNormalIndex(normIndex);
			indices.add(posIndex);
			return vertex;
		} else {
			return reprocessVertex(vertex,texIndex,normIndex);
		}
	}
	
	private int[] convertIndices() {
		indicesArray = new int[indices.size()];
		for(int i = 0; i < indicesArray.length; i++) {
			indicesArray[i]=indices.get(i);
		}
		return indicesArray;
	}
	
	private double convertData() {
		double furthestPoint = 0;
		for(int i = 0; i < vertices.size(); i++) {
			Vertex vertex = vertices.get(i);
			if(vertex.getLength()>furthestPoint)furthestPoint=vertex.getLength();
			Vector3d pos = vertex.getPosition();
			Vector2d texCoord = textures.get(vertex.getTextureIndex());
			Vector3d normal = normals.get(vertex.getNormalIndex());
			verticesArray[i*3]=pos.x;
			verticesArray[i*3+1]=pos.y;
			verticesArray[i*3+2]=pos.z;
			texturesArray[i*2]=texCoord.x;
			texturesArray[i*2+1]=1-texCoord.y;
			normalsArray[i*3]=normal.x;
			normalsArray[i*3+1]=normal.y;
			normalsArray[i*3+2]=normal.z;
			VertexSkinData weights = vertex.getWeights();
			jointIDsArray[i*3]=weights.ids.get(0);
			jointIDsArray[i*3+1]=weights.ids.get(1);
			jointIDsArray[i*3+2]=weights.ids.get(2);
			weightsArray[i*3]=weights.weights.get(0);
			weightsArray[i*3+1]=weights.weights.get(1);
			weightsArray[i*3+2]=weights.weights.get(2);
		}
		return furthestPoint;
	}

	private Vertex reprocessVertex(Vertex vertex, int texture, int normal) {
		if(vertex.hasSameTexture(texture,normal)) {
			indices.add(vertex.getIndex());
			return vertex;
		} else {
			Vertex vertex1 = vertex.getDuplicateVertex();
			if(vertex1!=null)return reprocessVertex(vertex1,texture,normal);
			Vertex vertex2 = new Vertex(vertices.size(),vertex.getPosition(),vertex.getWeights());
			vertex2.setTextureIndex(texture);
			vertex2.setNormalIndex(normal);
			vertices.add(vertex2);
			indices.add(vertex2.getIndex());
			return vertex2;
		}
	}
	
	private void initArrays() {
		verticesArray = new double[vertices.size()*3];
		texturesArray = new double[vertices.size()*2];
		normalsArray = new double[vertices.size()*3];
		jointIDsArray = new int[vertices.size()*3];
		weightsArray = new double[vertices.size()*3];
	}
	
	private void removeUnused() {
		for(Vertex v : vertices) {
			v.averageTangents();
			if(!v.isSet()) {
				v.setTextureIndex(0);
				v.setNormalIndex(0);
			}
		}
	}
}