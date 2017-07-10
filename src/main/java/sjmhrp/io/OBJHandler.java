package sjmhrp.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector2d;
import sjmhrp.linear.Vector3d;
import sjmhrp.models.RawModel;
import sjmhrp.physics.shapes.CompoundShape;
import sjmhrp.physics.shapes.ConvexHullShape;
import sjmhrp.physics.shapes.StaticTriMesh;
import sjmhrp.render.Loader;

public class OBJHandler {
	static final String RES_LOC = "/res/models/";
	
	public static ArrayList<Vector3d> parseVertices(String obj) {
		InputStreamReader in = new InputStreamReader(Class.class.getResourceAsStream(RES_LOC+obj+".obj"));
		BufferedReader reader = new BufferedReader(in);
		String line = null;
		ArrayList<Vector3d> vertices = new ArrayList<Vector3d>();
		try{
			while(true) {
				line = reader.readLine();
				if(line==null)break;
				String[] currentLine = line.split(" ");
				if(line.startsWith("v ")) {
					Vector3d vertex = new Vector3d(Double.parseDouble(currentLine[1]),Double.parseDouble(currentLine[2]),Double.parseDouble(currentLine[3]));
					vertices.add(vertex);
				}
			}
			reader.close();
		} catch(Exception e) {
			Log.printError(e);
		}
		return vertices;
	}

	public static CompoundShape parseCompoundShape(String obj) {
		InputStreamReader in = new InputStreamReader(Class.class.getResourceAsStream(RES_LOC+obj+".obj"));
		BufferedReader reader = new BufferedReader(in);
		String line = "";
		CompoundShape shape = new CompoundShape();
		try {
			while(line!=null) {
				ArrayList<Vector3d> vertices = new ArrayList<Vector3d>();
				while(true) {
					line = reader.readLine();
					if(line==null||line.startsWith("f "))break;
					String[] currentLine = line.split(" ");
					if(line.startsWith("v ")) {
						Vector3d vertex = new Vector3d(Double.parseDouble(currentLine[1]),Double.parseDouble(currentLine[2]),Double.parseDouble(currentLine[3]));
						vertices.add(vertex);
					}
				}
				while(line!=null&&line.startsWith("f "))line=reader.readLine();
				shape.add(new ConvexHullShape(vertices),new Transform());
			}
			reader.close();
		} catch(IOException e) {
			Log.printError(e);
		}
		return shape;
	}
	
	public static StaticTriMesh parseCollisionMesh(String obj, Transform t) {
		InputStreamReader in = new InputStreamReader(Class.class.getResourceAsStream(RES_LOC+obj+".obj"));
		BufferedReader reader = new BufferedReader(in);
		String line;
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Integer> indices = new ArrayList<Integer>();
		try {
			while (true) {
				line = reader.readLine();
				if (line.startsWith("v ")) {
					String[] currentLine = line.split(" ");
					Vector3d vertex = new Vector3d((double) Double.valueOf(currentLine[1]),
							(double) Double.valueOf(currentLine[2]),
							(double) Double.valueOf(currentLine[3]));
					Vertex newVertex = new Vertex(vertices.size(), vertex);
					vertices.add(newVertex); 
				} else if (line.startsWith("f "))break;
			}
			while (line != null && line.startsWith("f ")) {
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				processVertex(vertex1, vertices, indices);
				processVertex(vertex2, vertices, indices);
				processVertex(vertex3, vertices, indices);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			Log.printError(e);
		}
		removeUnusedVertices(vertices);
		double[] verticesArray = new double[vertices.size() * 3];
		double furthestPoint = 0;
		for (int i = 0; i < vertices.size(); i++) {
			Vertex currentVertex = vertices.get(i);
			if (currentVertex.getLength() > furthestPoint) {
				furthestPoint = currentVertex.getLength();
			}
			Vector3d position = currentVertex.getPosition();
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
		}
		int[] indicesArray = convertIndicesListToArray(indices);
		return new StaticTriMesh(verticesArray,indicesArray,t);
	}

	public static RawModel parseOBJ(String obj) {
		InputStreamReader in = new InputStreamReader(Class.class.getResourceAsStream(RES_LOC+obj+".obj"));
		BufferedReader reader = new BufferedReader(in);
		String line;
		List<Vertex> vertices = new ArrayList<Vertex>();
		List<Vector2d> textures = new ArrayList<Vector2d>();
		List<Vector3d> normals = new ArrayList<Vector3d>();
		List<Integer> indices = new ArrayList<Integer>();
		try {
			while (true) {
				line = reader.readLine();
				if (line.startsWith("v ")) {
					String[] currentLine = line.split(" ");
					Vector3d vertex = new Vector3d((double) Double.valueOf(currentLine[1]),
							(double) Double.valueOf(currentLine[2]),
							(double) Double.valueOf(currentLine[3]));
					Vertex newVertex = new Vertex(vertices.size(), vertex);
					vertices.add(newVertex);
				} else if (line.startsWith("vt ")) {
					String[] currentLine = line.split(" ");
					Vector2d texture = new Vector2d((double) Double.valueOf(currentLine[1]),
							(double) Double.valueOf(currentLine[2]));
					textures.add(texture);
				} else if (line.startsWith("vn ")) {
					String[] currentLine = line.split(" ");
					Vector3d normal = new Vector3d((double) Double.valueOf(currentLine[1]),
							(double) Double.valueOf(currentLine[2]),
							(double) Double.valueOf(currentLine[3]));
					normals.add(normal);
				} else if (line.startsWith("f "))break;
			}
			while (line != null && line.startsWith("f ")) {
				String[] currentLine = line.split(" ");
				String[] vertex1 = currentLine[1].split("/");
				String[] vertex2 = currentLine[2].split("/");
				String[] vertex3 = currentLine[3].split("/");
				Vertex v0 = processVertex(vertex1, vertices, indices);
				Vertex v1 = processVertex(vertex2, vertices, indices);
				Vertex v2 = processVertex(vertex3, vertices, indices);
				calculateTangents(v0, v1, v2, textures);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			Log.printError(e);
		}
		removeUnusedVertices(vertices);
		double[] verticesArray = new double[vertices.size() * 3];
		double[] texturesArray = new double[vertices.size() * 2];
		double[] normalsArray = new double[vertices.size() * 3];
		double[] tangentsArray = new double[vertices.size() * 3];
		convertDataToArrays(vertices, textures, normals, verticesArray,texturesArray, normalsArray, tangentsArray);
		int[] indicesArray = convertIndicesListToArray(indices);
		return Loader.load(verticesArray,indicesArray,normalsArray,tangentsArray,texturesArray);
	}
	
	private static void calculateTangents(Vertex v0, Vertex v1, Vertex v2,
			List<Vector2d> textures) {
		Vector3d deltaPos1 = Vector3d.sub(v1.getPosition(), v0.getPosition());
		Vector3d deltaPos2 = Vector3d.sub(v2.getPosition(), v0.getPosition());
		Vector2d uv0 = textures.get(v0.getTextureIndex());
		Vector2d uv1 = textures.get(v1.getTextureIndex());
		Vector2d uv2 = textures.get(v2.getTextureIndex());
		Vector2d deltaUv1 = Vector2d.sub(uv1, uv0);
		Vector2d deltaUv2 = Vector2d.sub(uv2, uv0); 
		double r = 1.0f / (deltaUv1.x * deltaUv2.y - deltaUv1.y * deltaUv2.x);
		deltaPos1.scale(deltaUv2.y);
		deltaPos2.scale(deltaUv1.y);
		Vector3d tangent = Vector3d.sub(deltaPos1, deltaPos2);
		tangent.scale(r);
		v0.addTangent(tangent);
		v1.addTangent(tangent);
		v2.addTangent(tangent);
	}
 
	private static Vertex processVertex(String[] vertex, List<Vertex> vertices,List<Integer> indices) {
		int index = Integer.parseInt(vertex[0]) - 1;
		Vertex currentVertex = vertices.get(index);
		int textureIndex = Integer.parseInt(vertex[1]) - 1;
		int normalIndex = Integer.parseInt(vertex[2]) - 1;
		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(textureIndex);
			currentVertex.setNormalIndex(normalIndex);
			indices.add(index);
			return currentVertex;
		} else {
			return dealWithAlreadyProcessedVertex(currentVertex, textureIndex, normalIndex, indices,vertices);
		}
	}

	private static int[] convertIndicesListToArray(List<Integer> indices) {
		int[] indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}
 
	private static double convertDataToArrays(List<Vertex> vertices, List<Vector2d> textures,List<Vector3d> normals, double[] verticesArray, double[] texturesArray,double[] normalsArray, double[] tangentsArray) {
		double furthestPoint = 0;
		for (int i = 0; i < vertices.size(); i++) {
			Vertex currentVertex = vertices.get(i);
			if (currentVertex.getLength() > furthestPoint)furthestPoint = currentVertex.getLength();
			Vector3d position = currentVertex.getPosition();
			Vector2d textureCoord = textures.get(currentVertex.getTextureIndex());
			Vector3d normalVector = normals.get(currentVertex.getNormalIndex());
			Vector3d tangent = currentVertex.getAverageTangent();
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;
			texturesArray[i * 2] = textureCoord.x;
			texturesArray[i * 2 + 1] = 1 - textureCoord.y;
			normalsArray[i * 3] = normalVector.x;
			normalsArray[i * 3 + 1] = normalVector.y;
			normalsArray[i * 3 + 2] = normalVector.z;
			tangentsArray[i * 3] = tangent.x;
			tangentsArray[i * 3 + 1] = tangent.y;
			tangentsArray[i * 3 + 2] = tangent.z; 
		}
		return furthestPoint;
	}
 
	private static Vertex dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex,int newNormalIndex, List<Integer> indices, List<Vertex> vertices) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
			return previousVertex;
		} else {
			Vertex anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex,newNormalIndex, indices, vertices);
			} else {
				Vertex duplicateVertex = previousVertex.duplicate(vertices.size());//NEW
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
				return duplicateVertex;
			}
		}
	}

	private static void removeUnusedVertices(List<Vertex> vertices) {
		for (Vertex vertex : vertices) {
			vertex.averageTangents();
			if (!vertex.isSet()) {
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}
}