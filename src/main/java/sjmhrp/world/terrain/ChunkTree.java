package sjmhrp.world.terrain;

import static sjmhrp.utils.linear.Vector3d.add;
import static sjmhrp.utils.linear.Vector3d.scale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;

import sjmhrp.physics.collision.broadphase.AABB;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.physics.shapes.StaticTriMesh;
import sjmhrp.render.Loader;
import sjmhrp.render.models.MeshData;
import sjmhrp.render.models.RawModel;
import sjmhrp.utils.GeometryUtils;
import sjmhrp.utils.VectorUtils;
import sjmhrp.utils.linear.Transform;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.world.terrain.Octree.OctreeNode;
import sjmhrp.world.terrain.generator.TerrainGenerator;

public class ChunkTree {

	public static final int VOXEL_COUNT = 32;
	static final Vector3d OFFSETS[] = {
			new Vector3d(0,0,0),new Vector3d(1,0,0),new Vector3d(0,0,1),new Vector3d(1,0,1),
			new Vector3d(0,1,0),new Vector3d(1,1,0),new Vector3d(0,1,1),new Vector3d(1,1,1)
	};
	
	ChunkNode root;
	
	public void addNode(ChunkNode n) {
		if(root==null) {
			root=n;
			return;
		}
		root.addNode(n);
		while(root.parent!=null)root=root.parent;
	}

	public void createModels() {
		if(root!=null)root.createModel();
	}
	
	public ArrayList<ChunkNode> allContaining(int size, Vector3d p) {
		return root.allContaining(size,p);
	}
	
	public ChunkNode smallestContaining(Vector3d p) {
		return root.smallestContaining(p);
	}
	
	public ArrayList<ChunkNode> getAll() {
		return root==null?new ArrayList<ChunkNode>():root.getAll();
	}
	
	public ArrayList<OctreeNode> findSeamNodes(ChunkNode chunk) {
		Vector3d seamValues = new Vector3d(chunk.getSize()).add(chunk.getMin());
		ArrayList<OctreeNode> nodes = new ArrayList<OctreeNode>();
		ArrayList<BiFunction<Vector3d,Vector3d,Boolean>> funcs = new ArrayList<BiFunction<Vector3d,Vector3d,Boolean>>();
		funcs.add((min,max)->(max.x == seamValues.x||max.y == seamValues.y||max.z == seamValues.z));
		funcs.add((min,max)->(min.x == seamValues.x));
		funcs.add((min,max)->(min.z == seamValues.z));
		funcs.add((min,max)->(min.x == seamValues.x && min.z == seamValues.z));
		funcs.add((min,max)->(min.y == seamValues.y));
		funcs.add((min,max)->(min.x == seamValues.x && min.y == seamValues.y));
		funcs.add((min,max)->(min.y == seamValues.y && min.z == seamValues.z));
		funcs.add((min,max)->(min.x == seamValues.x && min.y == seamValues.y && min.z == seamValues.z));
		for(int i = 0; i < 8; i++) {
			Vector3d max = add(chunk.getMin(),scale(chunk.getSize(),OFFSETS[i]));
			ArrayList<ChunkNode> ns = this.allContaining(chunk.getSize(),max);
			if(ns==null)continue;
			for(ChunkNode c : ns) {
				if(c!=null)nodes.addAll(c.getTree().findNodes(funcs.get(i),i,seamValues));	
			}
		}
		return nodes;
	}
	
	@Override
	public String toString() {
		return root.toString();
	}
	
	public static class ChunkNode {
		boolean leaf;
		int size;
		Vector3d min;
		ChunkNode parent;
		ChunkNode[] children = new ChunkNode[8];
		DrawInfo drawInfo;
		
		public ChunkNode(Vector3d min, int size, ChunkNode parent, boolean leaf) {
			this.min=min;
			this.size=size;
			this.parent=parent;
			this.leaf=leaf;
		}
		
		public ChunkNode generate(TerrainGenerator gen) {
			leaf=true;
			drawInfo=new DrawInfo(gen,min,size);
			return this;
		}
		
		public ChunkNode addSeam(ArrayList<OctreeNode> nodes, TerrainGenerator gen, ChunkTree world) {
			if(drawInfo!=null)drawInfo.createSeam(nodes,gen,min,world);
			return this;
		}
		
		public void addNode(ChunkNode n) {
			if(n.size==0)throw new IllegalStateException(n.toString());
			if(n.size<size) {
				if(!contains(n.min)) {
					while(n.size<this.size) {
						n=n.createParent();
					}
					addNode(n);
					return;
				}
				ChunkNode node = this;
				while(n.size<node.size/2) {
					for(int i = 0; i < 8; i++) {
						Vector3d v = Vector3d.scale(node.size/2,Octree.CHILD_MIN_OFFSETS[i]).add(node.min);
						if(GeometryUtils.contains(v,node.size/2,n.min)) {
							ChunkNode c = node.children[i];
							if(c==null) {
								c=new ChunkNode(v,node.size/2,node,false);
								node.children[i]=c;
							}
							node=c;
							break;
						}
					}
				}
				node.addChild(n);
				return;
			}
			if(n.size==size) {
				ChunkNode n1 = this;
				ChunkNode n2 = n;
				Vector3d chunkMin1 = VectorUtils.chunkMin(n1.size*2,n1.min);
				Vector3d chunkMin2 = VectorUtils.chunkMin(n2.size*2,n2.min);
				while(!chunkMin1.equals(chunkMin2)) {
					n1=n1.createParent();
					n2=n2.createParent();
					chunkMin1 = VectorUtils.chunkMin(n1.size*2,n1.min);
					chunkMin2 = VectorUtils.chunkMin(n2.size*2,n2.min);
				}
				ChunkNode p = n1.createParent();
				p.addChild(n2);
				return;
			}
			n.addNode(this);
		}

		ChunkNode createParent() {
			Vector3d chunkMin = VectorUtils.chunkMin(size*2,min);
			Vector3d offset = Vector3d.sub(min,chunkMin).scale(1d/size);
			ChunkNode p = new ChunkNode(chunkMin,size*2,null,false);
			for(int i = 0; i < 8; i++) {
				if(offset.equals(Octree.CHILD_MIN_OFFSETS[i])) {
					p.children[i]=this;
					this.parent=p;
					return p;
				}
			}
			return null;
		}

		void addChild(ChunkNode n) {
			if(n.size!=size/2)return;
			Vector3d offset = Vector3d.sub(n.min,min).scale(1d/n.size);
			for(int i = 0; i < 8; i++) {
				if(offset.equals(Octree.CHILD_MIN_OFFSETS[i])) {
					children[i]=n;
					n.parent=this;
					return;
				}
			}
		}
		
		public ArrayList<ChunkNode> getAll() {
			ArrayList<ChunkNode> n = new ArrayList<ChunkNode>();
			if(leaf) {
				n.add(this);
				return n;
			}
			for(ChunkNode c : children) {
				if(c!=null)n.addAll(c.getAll());
			}
			return n;
		}
		
		ArrayList<ChunkNode> allContaining(int size, Vector3d p) {
			if(!contains(p))return null;
			if(this.size<=size||leaf)return getAll();
			for(ChunkNode c : children) {
				if(c==null)continue;
				ArrayList<ChunkNode> n = c.allContaining(size,p);
				if(n!=null)return n;
			}
			return null;
		}
		
		ChunkNode smallestContaining(Vector3d p) {
			if(!contains(p))return null;
			if(leaf)return this;
			for(ChunkNode c : children) {
				if(c==null)continue;
				ChunkNode n = c.smallestContaining(p);
				if(n!=null)return n;
			}
			return null;
		}
		
		public void createModel() {
			if(isLeaf()) {
				if(drawInfo!=null)drawInfo.createModel();
			} else {
				for(ChunkNode c : children) {
					if(c!=null)c.createModel();
				}
			}
		}
		
		public boolean contains(Vector3d p) {
			return GeometryUtils.contains(min,size,p);
		}

		public RawModel getModel() {
			return drawInfo.model;
		}
		
		public RawModel getSeamModel() {
			return drawInfo.seamModel;
		}
		
		public CollisionBody getMesh() {
			return drawInfo.body;
		}
		
		public CollisionBody getSeamMesh() {
			return drawInfo.seamBody;
		}
		
		public boolean hasSeam() {
			return drawInfo!=null&&drawInfo.seamMesh!=null;
		}
		
		public boolean hasChanged() {
			return drawInfo!=null&&drawInfo.changed;
		}
		
		public boolean isLeaf() {
			return leaf;
		}

		public int getSize() {
			return size;
		}

		public Vector3d getMin() {
			return min;
		}
		
		public Octree getTree() {
			return drawInfo.tree;
		}
		
		public AABB getBounds() {
			return new AABB(min,new Vector3d(size).add(min));
		}
		
		@Override
		public String toString() {
			return "ChunkNode[Min: "+min+", Size: "+size+", Children: "+Arrays.toString(Arrays.stream(children).filter((x)->x!=null).toArray())+"]";
		}
	}
	
	static class DrawInfo {
		boolean changed = true;
		Octree tree;
		CollisionBody body;
		CollisionBody seamBody;
		MeshData mesh;
		MeshData seamMesh;
		RawModel model;
		RawModel seamModel;
		
		public DrawInfo(TerrainGenerator gen, Vector3d min, int size) {
			tree = new Octree(gen,min,size);
			mesh=tree.generateMesh();
			if(mesh!=null)body=new CollisionBody(new StaticTriMesh(mesh,new Transform()));
		}
		
		public void createSeam(ArrayList<OctreeNode> nodes, TerrainGenerator gen, Vector3d min, ChunkTree world) {
			changed=false;
			if(nodes==null||nodes.size()==0)return;
			Octree tree = new Octree(nodes,gen,min,world);
			seamMesh=tree.generateSeamMesh(min);
			if(seamMesh!=null)seamBody=new CollisionBody(new StaticTriMesh(seamMesh,new Transform()));
		}
		
		public void createModel() {
			if(mesh!=null)model=Loader.load(mesh.getVertices(),mesh.getIndices(),mesh.getNormals());
			if(seamMesh!=null)seamModel=Loader.load(seamMesh.getVertices(),seamMesh.getIndices(),seamMesh.getNormals());
		}
	}
}