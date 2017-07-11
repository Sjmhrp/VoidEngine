 package sjmhrp.terrain;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import sjmhrp.io.Log;
import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector2d;
import sjmhrp.linear.Vector3d;
import sjmhrp.models.RawModel;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.physics.shapes.StaticTriMesh;
import sjmhrp.render.Loader;
import sjmhrp.textures.TerrainTexture;
import sjmhrp.utils.GeometryUtils;

public class Terrain {
	
	public static final int SIZE = 800;
	
	public static final int VERTEX_COUNT = 128;
	private static final int MAX_HEIGHT = 40;
	private static final int MAX_PIXEL_COUNT = 256 * 256 * 128;
	
	private int x;
	private int z;
	private RawModel model;
	private TerrainTexture texture;
	private HeightGenerator gen;
	private CollisionBody mesh;

	private double[][] heights;

	public Terrain() {}

	public Terrain(int gridX, int gridZ, TerrainTexture texture, String heightMap) {
		this.texture = texture;
		x = gridX*SIZE;
		z = gridZ*SIZE;
		model = generateTerrain(heightMap);
	}
	
	public Terrain(int gridX, int gridZ, TerrainTexture texture, HeightGenerator gen) {
		this.texture = texture;
		x = gridX*SIZE;
		z = gridZ*SIZE;
		this.gen=gen;
		model = generateTerrain();
	}
	
	public double getX() {
		return x;
	}

	public double getZ() {
		return z;
	}

	public RawModel getModel() {
		return model;
	}

	public TerrainTexture getTexture() {
		return texture;
	}

	public CollisionBody getMesh() {
		return mesh;
	}

	public double getHeight(double x, double z) {
		double wx = x-this.x;
		double wz = z-this.z;
		double size = SIZE / ((double)heights.length-1);
		int gridX = (int)(wx/size);
		int gridZ = (int)(wz/size);
		if(gridX >= heights.length-1 || gridZ >= heights.length-1 || gridX<0||gridZ<0) {
			return 0;
		}
		double xCoord = (wx%size)/size;
		double zCoord = (wz%size)/size;
		double height;
		if (xCoord <= (1-zCoord)) {
			height = GeometryUtils.barycentric(new Vector3d(0, heights[gridX][gridZ], 0), new Vector3d(1,
							heights[gridX + 1][gridZ], 0), new Vector3d(0,
							heights[gridX][gridZ + 1], 1), new Vector2d(xCoord, zCoord));
		} else {
			height = GeometryUtils.barycentric(new Vector3d(1, heights[gridX + 1][gridZ], 0), new Vector3d(1,
							heights[gridX + 1][gridZ + 1], 1), new Vector3d(0,
							heights[gridX][gridZ + 1], 1), new Vector2d(xCoord, zCoord));
		}
		return height;
	}
	
	private RawModel generateTerrain(){
		heights = new double[VERTEX_COUNT][VERTEX_COUNT];
		int count = VERTEX_COUNT * VERTEX_COUNT;
		double[] vertices = new double[count * 3];
		double[] normals = new double[count * 3];
		double[] textureCoords = new double[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				double height = getHeight(j,i,gen);
				heights[j][i] = height;
				vertices[vertexPointer*3] = (double)j/((double)VERTEX_COUNT - 1) * SIZE;
				vertices[vertexPointer*3+1] = height;
				vertices[vertexPointer*3+2] = (double)i/((double)VERTEX_COUNT - 1) * SIZE;
				Vector3d normal = getNormal(j,i,gen);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (double)j/((double)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (double)i/((double)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		double[] wVertices = new double[vertices.length];
		for(int i = 0; i < vertices.length/3; i++) {
			wVertices[i*3]=vertices[i*3]+x;
			wVertices[i*3+1]=vertices[i*3+1];
			wVertices[i*3+2]=vertices[i*3+2]+z;
		}
		mesh = new CollisionBody(new Vector3d(x,0,z),new StaticTriMesh(vertices,indices,new Transform(new Vector3d(x,0,z))));
		return Loader.load(vertices, indices, normals, textureCoords);
	}

	private RawModel generateTerrain(String heightMap){
		BufferedImage image = null;
		try {
			image = ImageIO.read(Class.class.getResourceAsStream("/res/textures/map/"+heightMap+".png"));
		} catch(Exception e) {
			Log.printError(e);
		}
		int VERTEX_COUNT = image.getHeight();
		heights = new double[VERTEX_COUNT][VERTEX_COUNT];
		int count = VERTEX_COUNT * VERTEX_COUNT;
		double[] vertices = new double[count * 3];
		double[] normals = new double[count * 3];
		double[] textureCoords = new double[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				double height = getHeight(j,i,image);
				heights[j][i] = height;
				vertices[vertexPointer*3] = (double)j/((double)VERTEX_COUNT - 1) * SIZE;
				vertices[vertexPointer*3+1] = height;
				vertices[vertexPointer*3+2] = (double)i/((double)VERTEX_COUNT - 1) * SIZE;
				Vector3d normal = getNormal(j,i,image);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (double)j/((double)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (double)i/((double)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		double[] wVertices = new double[vertices.length];
		for(int i = 0; i < vertices.length/3; i++) {
			wVertices[i*3]=vertices[i*3]+x;
			wVertices[i*3+1]=vertices[i*3+1];
			wVertices[i*3+2]=vertices[i*3+2]+z;
		}
		mesh = new CollisionBody(new Vector3d(x,0,z),new StaticTriMesh(vertices,indices,new Transform(new Vector3d(x,0,z))));
		return Loader.load(vertices, indices, normals, textureCoords);
	}

	public Vector3d getNormal(int x, int z, HeightGenerator gen) {
		double heightL = getHeight(x-1,z,gen);
		double heightR = getHeight(x+1,z,gen);
		double heightD = getHeight(x,z-1,gen);
		double heightU = getHeight(x,z+1,gen);
		Vector3d normal = new Vector3d(heightL-heightR,2f,heightD-heightU);
		normal.normalize();
		return normal;
	}

	public Vector3d getNormal(double x, double z) {
		double heightL = getHeight(x-0.1f,z);
		double heightR = getHeight(x+0.1f,z);
		double heightD = getHeight(x,z-0.1f);
		double heightU = getHeight(x,z+0.1f);
		Vector3d normal = new Vector3d(heightL-heightR,2f,heightD-heightU);
		normal.normalize();
		return normal;
	}
	
	public Vector3d getNormal(int x, int z, BufferedImage image) {
		double heightL = getHeight(x-1,z,image);
		double heightR = getHeight(x+1,z,image);
		double heightD = getHeight(x,z-1,image);
		double heightU = getHeight(x,z+1,image);
		Vector3d normal = new Vector3d(heightL-heightR,2f,heightD-heightU);
		normal.normalize();
		return normal;
	}

	private double getHeight(int x, int z, HeightGenerator h) {
		return h.generateHeight(x,z);
	}

	private double getHeight(int x, int z, BufferedImage image) {
		if(x<0 || x>=image.getHeight() || z<0 || z>=image.getWidth()) {
			return 0;
		}
		double height = image.getRGB(x, z);
		height += MAX_PIXEL_COUNT;
		height /= MAX_PIXEL_COUNT;
		height *= MAX_HEIGHT;
		return height;
	}
}