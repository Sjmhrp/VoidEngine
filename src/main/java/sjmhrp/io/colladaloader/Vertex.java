package sjmhrp.io.colladaloader;

import java.util.ArrayList;

import sjmhrp.utils.linear.Vector3d;

public class Vertex {

	private static final int NO_INDEX = -1;
	
	private Vector3d position;
	private int textureIndex = NO_INDEX;
	private int normalIndex = NO_INDEX;
	private Vertex duplicateVertex = null;
	private int index;
	private double length;
	private ArrayList<Vector3d> tangents = new ArrayList<Vector3d>();
	private Vector3d average = new Vector3d();
	private VertexSkinData weights;
	
	public Vertex(int index, Vector3d position, VertexSkinData weights) {
		this.index=index;
		this.weights=weights;
		this.position=position;
		this.length=position.length();
	}
	
	public VertexSkinData getWeights() {
		return weights;
	}
	
	public void addTangent(Vector3d tangent) {
		tangents.add(tangent);
	}
	
	public void averageTangents() {
		if(tangents.isEmpty())return;
		for(Vector3d tangent : tangents) {
			average.add(tangent);
		}
		average.normalize();
	}
	
	public Vector3d getAverage() {
		return average;
	}
	
	public int getIndex() {
		return index;
	}
	
	public double getLength() {
		return length;
	}
	
	public boolean isSet() {
		return textureIndex!=NO_INDEX&&normalIndex!=NO_INDEX;
	}
	
	public boolean hasSameTexture(int texture, int normal) {
		return this.textureIndex==texture&&this.normalIndex==normal;
	}
	
	public void setTextureIndex(int index) {
		textureIndex=index;
	}
	
	public void setNormalIndex(int index) {
		this.normalIndex=index;
	}

	public Vertex getDuplicateVertex() {
		return duplicateVertex;
	}

	public void setDuplicateVertex(Vertex duplicateVertex) {
		this.duplicateVertex = duplicateVertex;
	}

	public Vector3d getPosition() {
		return position;
	}

	public int getTextureIndex() {
		return textureIndex;
	}

	public int getNormalIndex() {
		return normalIndex;
	}
}