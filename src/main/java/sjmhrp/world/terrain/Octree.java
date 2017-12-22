package sjmhrp.world.terrain;

import static sjmhrp.utils.linear.Vector3d.add;
import static sjmhrp.utils.linear.Vector3d.scale;
import static sjmhrp.utils.linear.Vector3d.sub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.BiFunction;

import sjmhrp.render.models.MeshData;
import sjmhrp.utils.VectorUtils;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.world.terrain.QEF.QEFData;
import sjmhrp.world.terrain.QEF.QEFSolver;
import sjmhrp.world.terrain.generator.TerrainGenerator;

public class Octree {
	
	static final int MAX_CROSSINGS = 6;
	static final double QEF_ERROR = 1e-6f;
	static final int QEF_SWEEPS = 4;
	
	public static final Vector3d CHILD_MIN_OFFSETS[] = {
		new Vector3d(0,0,0),
		new Vector3d(0,0,1),
		new Vector3d(0,1,0),
		new Vector3d(0,1,1),
		new Vector3d(1,0,0),
		new Vector3d(1,0,1),
		new Vector3d(1,1,0),
		new Vector3d(1,1,1),
	};

	static final int[][] edgevmap = {
			{0,4},{1,5},{2,6},{3,7}, 
			{0,2},{1,3},{4,6},{5,7},
			{0,1},{2,3},{4,5},{6,7}
		};
	
	static final int[][] faceMap = {{4, 8, 5, 9}, {6, 10, 7, 11},{0, 8, 1, 10},{2, 9, 3, 11},{0, 4, 2, 6},{1, 5, 3, 7}} ;
	static final int[][] cellProcFaceMask = {{0,4,0},{1,5,0},{2,6,0},{3,7,0},{0,2,1},{4,6,1},{1,3,1},{5,7,1},{0,1,2},{2,3,2},{4,5,2},{6,7,2}} ;
	static final int[][] cellProcEdgeMask = {{0,1,2,3,0},{4,5,6,7,0},{0,4,1,5,1},{2,6,3,7,1},{0,2,4,6,2},{1,3,5,7,2}} ;

	static final int[][][] faceProcFaceMask = {
		{{4,0,0},{5,1,0},{6,2,0},{7,3,0}},
		{{2,0,1},{6,4,1},{3,1,1},{7,5,1}},
		{{1,0,2},{3,2,2},{5,4,2},{7,6,2}}
	} ;

	static final int[][][] faceProcEdgeMask = {
		{{1,4,0,5,1,1},{1,6,2,7,3,1},{0,4,6,0,2,2},{0,5,7,1,3,2}},
		{{0,2,3,0,1,0},{0,6,7,4,5,0},{1,2,0,6,4,2},{1,3,1,7,5,2}},
		{{1,1,0,3,2,0},{1,5,4,7,6,0},{0,1,5,0,4,1},{0,3,7,2,6,1}}
	};

	static final int[][][] edgeProcEdgeMask = {
		{{3,2,1,0,0},{7,6,5,4,0}},
		{{5,1,4,0,1},{7,3,6,2,1}},
		{{6,4,2,0,2},{7,5,3,1,2}},
	};
	
	static final int[][] processEdgeMask = {{3,2,1,0},{7,5,6,4},{11,10,9,8}};
	
	OctreeNode root;
	ChunkTree world;
	
	public Octree(TerrainGenerator gen, Vector3d min, int size) {
		root = construct(gen,min,size);
	}
	
	public Octree(ArrayList<OctreeNode> leaves, TerrainGenerator gen, Vector3d min, ChunkTree world) {
		this.world=world;
		root = createTree(leaves,gen,min);
	}
	
	public static OctreeNode construct(TerrainGenerator gen, Vector3d min, int size) {
		int scale = size/ChunkTree.VOXEL_COUNT;
		double[][][] density = new double[ChunkTree.VOXEL_COUNT+1][ChunkTree.VOXEL_COUNT+1][ChunkTree.VOXEL_COUNT+1];
		for(int i = 0; i <= ChunkTree.VOXEL_COUNT; i++) {
			for(int j = 0; j <= ChunkTree.VOXEL_COUNT; j++) {
				for(int k = 0; k <= ChunkTree.VOXEL_COUNT; k++) {
					density[i][j][k]=gen.getDensity(min.x+i*scale,min.y+j*scale,min.z+k*scale);
				}
			}
		}
		ArrayList<OctreeNode> nodes = new ArrayList<OctreeNode>();
		for(int i = 0; i < size; i+=scale) {
			for(int j = 0; j < size; j+=scale) {
				for(int k = 0; k < size; k+=scale) {
					OctreeNode node = new OctreeNode(OctreeType.LEAF);
					node.size=scale;
					node.min.set(min).add(new Vector3d(i,j,k));
					node=constructLeaf(gen,node,density,min);
					if(node!=null)nodes.add(node);
				}
			}
		}
		while(nodes.size()>0&&nodes.get(0).size<size) {
			HashMap<Vector3d,OctreeNode> parents = new HashMap<Vector3d,OctreeNode>();
			for(OctreeNode n : nodes) {
				Vector3d pos = VectorUtils.chunkMin(n.size*2,n.min);
				OctreeNode p = parents.get(pos);
				if(p==null) {
					p = new OctreeNode(OctreeType.INTERNAL);
					p.min=pos;
					p.size=n.size*2;
					parents.put(pos,p);
				}
				for(int i = 0; i < 8; i++) {
					Vector3d childMin = new Vector3d(pos).add(scale(n.size,CHILD_MIN_OFFSETS[i]));
					if(childMin.equals(n.min)) {
						p.children[i]=n;
						break;
					}
				}
			}
			nodes.clear();
			nodes.addAll(parents.values());
		}
		return nodes.size()>0?nodes.get(0):null;
	}
	
	static OctreeNode createTree(ArrayList<OctreeNode> leaves, TerrainGenerator gen, Vector3d chunkMin) {
		if(leaves.size()==0)return null;
		HashMap<Integer,HashMap<Vector3d,OctreeNode>> nodes = new HashMap<Integer,HashMap<Vector3d,OctreeNode>>();
		int minSize = Integer.MAX_VALUE;
		for(OctreeNode n : leaves) {
			minSize=Math.min(n.size,minSize);
			HashMap<Vector3d,OctreeNode> ns = nodes.get(n.size);
			if(ns==null) {
				ns = new HashMap<Vector3d,OctreeNode>();
				nodes.put(n.size,ns);
			}
			ns.put(n.min,n);
		}
		HashMap<Vector3d,OctreeNode> ns = nodes.get(minSize);
		while(nodes.size()>1||ns.size()>1) {
			if(ns.size()==0) {
				nodes.remove(minSize);
				minSize*=2;
				ns = nodes.get(minSize);
				continue;
			}
			Iterator<OctreeNode> it = ns.values().iterator();
			while(it.hasNext()) {
				OctreeNode n = it.next();
				Vector3d v = sub(n.min,chunkMin).mod(n.size*2);
				Vector3d pos = sub(n.min,v);
				HashMap<Vector3d,OctreeNode> ns2 = nodes.get(n.size*2);
				if(ns2==null) {
					ns2 = new HashMap<Vector3d,OctreeNode>();
					nodes.put(n.size*2,ns2);
				}
				OctreeNode p = ns2.get(pos);
				if(p==null) {
					p = new OctreeNode(OctreeType.INTERNAL);
					p.min=pos;
					p.size=n.size*2;
					ns2.put(pos,p);
				}
				for(int i = 0; i < 8; i++) {
					Vector3d childMin = new Vector3d(pos).add(scale(n.size,CHILD_MIN_OFFSETS[i]));
					if(childMin.equals(n.min)) {
						p.children[i]=n;
						break;
					}
				}
				it.remove();
			}
			ns = nodes.get(minSize);
		}
		return nodes.values().iterator().next().values().iterator().next();
	}
	
	public static OctreeNode constructLeaf(TerrainGenerator gen, OctreeNode leaf, double[][][] density, Vector3d chunkMin) {
		int corners = 0;
		for(int i = 0; i < 8; i++) {
			if(density!=null) {
				Vector3d p = sub(leaf.min,chunkMin).scale(1d/leaf.size).add(CHILD_MIN_OFFSETS[i]);
				corners|=(density[(int)p.x][(int)p.y][(int)p.z]<0?1:0)<<i;
			} else {
				Vector3d p = add(leaf.min,CHILD_MIN_OFFSETS[i]);
				corners|=(gen.getDensity(p)<0?1:0)<<i;
			}
		}
		if(corners==0||corners==255)return null;
		int edgeCount = 0;
		Vector3d avgNormal = new Vector3d();
		QEFSolver qef = new QEFSolver();
		for(int i = 0; i < 12 && edgeCount<MAX_CROSSINGS; i++) {
			int c1 = edgevmap[i][0];
			int c2 = edgevmap[i][1];
			int m1 = (corners>>c1)&1;
			int m2 = (corners>>c2)&1;
			if(m1==m2)continue;
			Vector3d p1 = add(leaf.min,CHILD_MIN_OFFSETS[c1]);
			Vector3d p2 = add(leaf.min,CHILD_MIN_OFFSETS[c2]);
			Vector3d p = approximateZeroCrossing(gen,p1,p2);
			Vector3d n = gen.getNormal(p);
			qef.add(p,n);
			avgNormal.add(n);
			edgeCount++;
		}
		avgNormal.normalize();
		Vector3d pos = new Vector3d();
		qef.solve(pos,QEF_ERROR,QEF_SWEEPS,QEF_ERROR);
		DrawInfo drawInfo = new DrawInfo();
		drawInfo.position=pos;
		drawInfo.qef=qef.getData();

		Vector3d min = leaf.min;
		Vector3d max = add(leaf.min,new Vector3d(leaf.size));
		if (drawInfo.position.x < min.x || drawInfo.position.x > max.x ||
			drawInfo.position.y < min.y || drawInfo.position.y > max.y ||
			drawInfo.position.z < min.z || drawInfo.position.z > max.z) {
			drawInfo.position=new Vector3d(qef.massPoint);
		}
		drawInfo.avgNormal=avgNormal;
		drawInfo.corners=corners;
		leaf.type=OctreeType.LEAF;
		leaf.drawInfo=drawInfo;
		return leaf;
	}
	
	static Vector3d approximateZeroCrossing(TerrainGenerator gen, Vector3d p1, Vector3d p2) {
		double increment = 0.125;
		double min = 0;
		Vector3d p = null;
		Vector3d delta = sub(p2,p1);
		for(double i = 0; i <= 1; i+=increment) {
			Vector3d point = add(p1,scale(i,delta));
			double d = Math.abs(gen.getDensity(point));
			if(p==null||d<min) {
				min=d;
				p=point;
			}
		}
		return p;
	}
	
	public ArrayList<OctreeNode> findNodes(BiFunction<Vector3d,Vector3d,Boolean> func, int i, Vector3d v) {
		ArrayList<OctreeNode> nodes = new ArrayList<OctreeNode>();
		return findNodes(root,func,nodes);
	}
	
	ArrayList<OctreeNode> findNodes(OctreeNode node, BiFunction<Vector3d,Vector3d,Boolean> func, ArrayList<OctreeNode> nodes) {
		if(node==null)return nodes;
		Vector3d max = new Vector3d(node.size).add(node.min);
		if(!func.apply(node.min,max))return nodes;
		if(node.type==OctreeType.LEAF) {
			nodes.add(node);
		} else {
			for(OctreeNode n : node.children) {
				findNodes(n,func,nodes);
			}
		}
		return nodes;
	}

	void ContourProcessEdge(OctreeNode[] node, int dir, ArrayList<Integer> indexBuffer) {
		if(node.length!=4)return;
		int minSize = 1000000;
		int minIndex = 0;
		int indices[] = { -1, -1, -1, -1 };
		boolean flip = false;
		boolean signChange[] = { false, false, false, false };

		for (int i = 0; i < 4; i++)
		{
			int edge = processEdgeMask[dir][i];
			int c1 = edgevmap[edge][0];
			int c2 = edgevmap[edge][1];

			int m1 = (node[i].drawInfo.corners >> c1) & 1;
			int m2 = (node[i].drawInfo.corners >> c2) & 1;

			if (node[i].size < minSize)
			{
				minSize = node[i].size;
				minIndex = i;
				flip = m1 != 0; 
			}

			indices[i] = node[i].drawInfo.index;

			signChange[i] = 
				(m1 == 0 && m2 != 0) ||
				(m1 != 0 && m2 == 0);
		}

		if (signChange[minIndex])
		{
			if (!flip)
			{
				indexBuffer.add(indices[0]);
				indexBuffer.add(indices[1]);
				indexBuffer.add(indices[3]);

				indexBuffer.add(indices[0]);
				indexBuffer.add(indices[3]);
				indexBuffer.add(indices[2]);
			}
			else
			{
				indexBuffer.add(indices[0]);
				indexBuffer.add(indices[3]);
				indexBuffer.add(indices[1]);

				indexBuffer.add(indices[0]);
				indexBuffer.add(indices[2]);
				indexBuffer.add(indices[3]);
			}
		}
	}
	
	void ContourEdgeProc(OctreeNode node[], int dir, ArrayList<Integer> indexBuffer, boolean seam)
	{
		if (node[0]==null || node[1]==null || node[2]==null || node[3]==null)
		{
			return;
		}

		if(seam) {
			
			Vector3d v = world.smallestContaining(node[0].min).min;
			
			for(int i = 1; i < 4; i++) {
				if(!v.equals(world.smallestContaining(node[i].min).min))break;
				if(i==3)return;
			}
		}
		
		if (node[0].type != OctreeType.INTERNAL &&
			node[1].type != OctreeType.INTERNAL &&
			node[2].type != OctreeType.INTERNAL &&
			node[3].type != OctreeType.INTERNAL)
		{
			ContourProcessEdge(node, dir, indexBuffer);
		}
		else
		{
			for (int i = 0; i < 2; i++)
			{
				OctreeNode edgeNodes[] = new OctreeNode[4];
				int c[] = 
				{
					edgeProcEdgeMask[dir][i][0],
					edgeProcEdgeMask[dir][i][1],
					edgeProcEdgeMask[dir][i][2],
					edgeProcEdgeMask[dir][i][3],
				};

				for (int j = 0; j < 4; j++)
				{
					if (node[j].type == OctreeType.LEAF || node[j].type == OctreeType.PSEUDO)
					{
						edgeNodes[j] = node[j];
					}
					else
					{
						edgeNodes[j] = node[j].children[c[j]];
					}
				}

				ContourEdgeProc(edgeNodes, edgeProcEdgeMask[dir][i][4], indexBuffer,seam);
			}
		}
	}

	void ContourFaceProc(OctreeNode node[], int dir, ArrayList<Integer> indexBuffer, boolean seam)
	{
		if (node[0]==null || node[1]==null)return;

		if (node[0].type == OctreeType.INTERNAL || 
			node[1].type == OctreeType.INTERNAL)
		{
			for (int i = 0; i < 4; i++)
			{
				OctreeNode faceNodes[] = new OctreeNode[2];
				int c[] = 
				{
					faceProcFaceMask[dir][i][0], 
					faceProcFaceMask[dir][i][1], 
				};

				for (int j = 0; j < 2; j++)
				{
					if (node[j].type != OctreeType.INTERNAL)
					{
						faceNodes[j] = node[j];
					}
					else
					{
						faceNodes[j] = node[j].children[c[j]];
					}
				}

				ContourFaceProc(faceNodes, faceProcFaceMask[dir][i][2], indexBuffer,seam);
			}
			
			int orders[][] =
			{
				{ 0, 0, 1, 1 },
				{ 0, 1, 0, 1 },
			};
			for (int i = 0; i < 4; i++)
			{
				OctreeNode edgeNodes[] = new OctreeNode[4];
				int c[] =
				{
					faceProcEdgeMask[dir][i][1],
					faceProcEdgeMask[dir][i][2],
					faceProcEdgeMask[dir][i][3],
					faceProcEdgeMask[dir][i][4],
				};

				int[] order = orders[faceProcEdgeMask[dir][i][0]];
				for (int j = 0; j < 4; j++)
				{
					if (node[order[j]].type == OctreeType.LEAF ||
						node[order[j]].type == OctreeType.PSEUDO)
					{
						edgeNodes[j] = node[order[j]];
					}
					else
					{
						edgeNodes[j] = node[order[j]].children[c[j]];
					}
				}

				ContourEdgeProc(edgeNodes, faceProcEdgeMask[dir][i][5], indexBuffer,seam);
			}
		}
	}

	void contourCellProc(OctreeNode node, ArrayList<Integer> indexBuffer, boolean seam)
	{
		if (node == null)return;
		if (node.type == OctreeType.INTERNAL)
		{
			for (int i = 0; i < 8; i++)
			{
				contourCellProc(node.children[i], indexBuffer,seam);
			}

			for (int i = 0; i < 12; i++)
			{
				OctreeNode faceNodes[] = new OctreeNode[2];
				int c[] = { cellProcFaceMask[i][0], cellProcFaceMask[i][1] };
				
				faceNodes[0] = node.children[c[0]];
				faceNodes[1] = node.children[c[1]];

				ContourFaceProc(faceNodes, cellProcFaceMask[i][2], indexBuffer,seam);
			}

			for (int i = 0; i < 6; i++)
			{
				OctreeNode edgeNodes[] = new OctreeNode[4];
				int c[] = 
				{
					cellProcEdgeMask[i][0],
					cellProcEdgeMask[i][1],
					cellProcEdgeMask[i][2],
					cellProcEdgeMask[i][3],
				};

				for (int j = 0; j < 4; j++)
				{
					edgeNodes[j] = node.children[c[j]];
				}

				ContourEdgeProc(edgeNodes, cellProcEdgeMask[i][4], indexBuffer,seam);
			}
		}
	}
	
	void generateVertexIndices(OctreeNode node, ArrayList<Vector3d> vertexBuffer, ArrayList<Vector3d> normalBuffer) {
		if (node==null)return;
		if (node.type != OctreeType.LEAF) {
			for (int i = 0; i < 8; i++) {
				generateVertexIndices(node.children[i], vertexBuffer, normalBuffer);
			}
		}
		if (node.type != OctreeType.INTERNAL) {
			DrawInfo d = node.drawInfo;
			if (d==null)throw new IllegalStateException("Could Not Add Vertex");
			d.index = vertexBuffer.size();
			vertexBuffer.add(d.position);
			normalBuffer.add(d.avgNormal);
		}
	}
	
	public MeshData generateSeamMesh(Vector3d min) {
		if(root==null)return null;
		ArrayList<Vector3d> vertices = new ArrayList<Vector3d>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<Vector3d> normals = new ArrayList<Vector3d>();
		generateVertexIndices(root,vertices,normals);
		contourCellProc(root,indices,true);
		if(indices.size()==0)return null;
		double[] vertex = new double[vertices.size()*3];
		double[] normal = new double[normals.size()*3];
		int[] index = new int[indices.size()];
		for(int i = 0; i < vertices.size(); i++) {
			Vector3d v = vertices.get(i);
			Vector3d n = normals.get(i);
			vertex[i*3]=v.x;
			vertex[i*3+1]=v.y;
			vertex[i*3+2]=v.z;
			normal[i*3]=n.x;
			normal[i*3+1]=n.y;
			normal[i*3+2]=n.z;
		}
		for(int i = 0; i < indices.size(); i++) {
			index[i]=indices.get(i);
		}
		return new MeshData(vertex,index,normal);
	}
	
	public MeshData generateMesh() {
		if(root==null)return null;
		ArrayList<Vector3d> vertices = new ArrayList<Vector3d>();
		ArrayList<Integer> indices = new ArrayList<Integer>();
		ArrayList<Vector3d> normals = new ArrayList<Vector3d>();
		generateVertexIndices(root,vertices,normals);
		contourCellProc(root,indices,false);
		if(indices.size()==0)return null;
		double[] vertex = new double[vertices.size()*3];
		double[] normal = new double[normals.size()*3];
		int[] index = new int[indices.size()];
		for(int i = 0; i < vertices.size(); i++) {
			Vector3d v = vertices.get(i);
			Vector3d n = normals.get(i);
			vertex[i*3]=v.x;
			vertex[i*3+1]=v.y;
			vertex[i*3+2]=v.z;
			normal[i*3]=n.x;
			normal[i*3+1]=n.y;
			normal[i*3+2]=n.z;
		}
		for(int i = 0; i < indices.size(); i++) {
			index[i]=indices.get(i);
		}
		return new MeshData(vertex,index,normal);
	}
	
	@Override
	public String toString() {
		return root==null?null:root.toString();
	}
	
	public static class OctreeNode {
		OctreeType type;
		Vector3d min;
		int size;
		OctreeNode[] children = new OctreeNode[8];
		DrawInfo drawInfo = null;
	
		public OctreeNode() {
			this(OctreeType.NONE);
		}
		
		public OctreeNode(OctreeType type) {
			this.type=type;
			min=new Vector3d();
		}
		
		@Override
		public String toString() {
			return "OctreeNode[Min: "+min+", Size: "+size+", Children: "+Arrays.toString(Arrays.stream(children).filter((x)->x!=null).toArray())+"]";
		}
	}

	static class DrawInfo {
		int index;
		int corners;
		Vector3d position;
		Vector3d avgNormal;
		QEFData qef;
		
		public DrawInfo() {
			index=-1;
		}
	}
	
	enum OctreeType {
		NONE,
		INTERNAL,
		PSEUDO,
		LEAF
	}
}