package sjmhrp.physics.collision.broadphase;

import java.io.Serializable;
import java.util.ArrayList;

import sjmhrp.physics.dynamics.Ray;
import sjmhrp.utils.GeometryUtils;
import sjmhrp.utils.linear.Vector3d;

public class Tree<T> implements Serializable {
	
	private static final long serialVersionUID = -5947015951035884776L;
	
	public Node<T> root;
	int leaves = 0;

	public ArrayList<T> getAll() {
		ArrayList<Node<T>> nodes = root.getAll();
		ArrayList<T> o = new ArrayList<T>();
		for(Node<T> n : nodes) {
			if(n.data!=null)o.add(n.getData());
		}
		return o;
	}
	
	public int getHeight() {
		return root.height;
	}

	public ArrayList<T> query(AABB box) {
		if(root==null)return new ArrayList<T>();
		return root.query(box);
	}

	public ArrayList<T> query(Vector3d point) {
		if(root==null)return new ArrayList<T>();
		return root.query(point);
	}
	
	public ArrayList<T> query(Ray ray) {
		if(root==null)return new ArrayList<T>();
		return root.query(ray);
	}
	
	public void translate(Vector3d t) {
		root.translate(t);
	}

	public void add(AABB aabb, T data) {
		Node<T> n = new Node<T>(aabb,data);
		insertLeaf(n);
		leaves++;
	}

	public void insertLeaf(Node<T> leaf) {
		if(root==null) {
			root = leaf;
			leaf.parent = null;
			return;
		}
		AABB leafAABB = leaf.boundingBox;
		Node<T> node = root;
		while(!node.isLeaf()) {
			Node<T> left = node.left;
			Node<T> right = node.right;
			AABB combined = GeometryUtils.combine(left.boundingBox, right.boundingBox);
			double cost = 2 * combined.getArea();
			double inheritanceCost = 2 * (combined.getArea()-node.boundingBox.getArea());
			double costLeft;
			if(left.isLeaf()) {
				costLeft = GeometryUtils.combine(leafAABB, left.boundingBox).getArea()+inheritanceCost;
			} else {
				AABB aabb = GeometryUtils.combine(leafAABB, left.boundingBox);
				costLeft = aabb.getArea() - left.boundingBox.getArea() + inheritanceCost;
			}
			double costRight;
			if(right.isLeaf()) {
				costRight = GeometryUtils.combine(leafAABB, right.boundingBox).getArea()+inheritanceCost;
			} else {
				AABB aabb = GeometryUtils.combine(leafAABB, right.boundingBox);
				costRight = aabb.getArea() - right.boundingBox.getArea() + inheritanceCost;
			}
			if(cost < costLeft && cost < costRight) {
				break;
			}
			node = costLeft<costRight?left:right;
		}
		Node<T> oldParent = node.parent;
		AABB a = GeometryUtils.combine(leafAABB, node.boundingBox);
		Node<T> newParent = new Node<T>(a,null);
		newParent.parent = oldParent;
		newParent.height = node.height+1;
		if(oldParent!=null) {
			if(oldParent.left==node) {
				oldParent.left=newParent;
			} else {
				oldParent.right=newParent;
			}
			newParent.left = node;
			newParent.right = leaf;
			node.parent = newParent;
			leaf.parent = newParent;
		} else {
			newParent.left = node;
			newParent.right = leaf;
			node.parent = newParent;
			leaf.parent = newParent;
			root = newParent;
		}
		syncHierarchy(leaf.parent);
	}

	public Node<T> balance(Node<T> a) {
		if(a.isLeaf()||a.height<2) {
			return a;
		}
		Node<T> b = a.left;
		Node<T> c = a.right;
		int balance = c.height-b.height;
		if(balance>1) {
			Node<T> f = c.left;
			Node<T> g = c.right;
			c.left=a;
			c.parent=a.parent;
			a.parent=c;
			if(c.parent!=null) {
				if(c.parent.left==a) {
					c.parent.left=c;
				} else {
					c.parent.right=c;
				}
			} else {
				root = c;
			}
			if(f.height>g.height) {
				c.right=f;
				a.right=g;
				g.parent=a;
				a.boundingBox=GeometryUtils.combine(b.boundingBox, g.boundingBox);
				c.boundingBox=GeometryUtils.combine(a.boundingBox, f.boundingBox);
				a.height=1+Math.max(b.height, g.height);
				c.height=1+Math.max(a.height, f.height);
			} else {
				c.right=g;
				a.right=f;
				f.parent=a;
				a.boundingBox=GeometryUtils.combine(b.boundingBox, f.boundingBox);
				c.boundingBox=GeometryUtils.combine(a.boundingBox, g.boundingBox);
				a.height=1+Math.max(b.height, f.height);
				c.height=1+Math.max(a.height, g.height);
			}
			return c;
		}
		if(balance>-1) {
			Node<T> d = b.left;
			Node<T> e = b.right;
			b.left=a;
			b.parent=a.parent;
			a.parent=b;
			if(b.parent!=null) {
				if(b.parent.left==a) {
					b.parent.left=b;
				} else {
					b.parent.right=b;
				}
			} else {
				root = b;
			}
			if(d.height>e.height) {
				b.right=d;
				a.left=e;
				e.parent=a;
				a.boundingBox=GeometryUtils.combine(c.boundingBox, e.boundingBox);
				b.boundingBox=GeometryUtils.combine(a.boundingBox, d.boundingBox);
				a.height=1+Math.max(c.height, e.height);
				b.height=1+Math.max(a.height, d.height);
			} else {
				b.right=e;
				a.left=d;
				d.parent=a;
				a.boundingBox=GeometryUtils.combine(c.boundingBox, d.boundingBox);
				b.boundingBox=GeometryUtils.combine(a.boundingBox, e.boundingBox);
				a.height=1+Math.max(c.height, d.height);
				b.height=1+Math.max(a.height, e.height);
			}
			return b;
		}
		return a;
	}

	public void syncHierarchy(Node<T> node) {
		Node<T> n = node;
		while(n!=null) {
			n = balance(n);
			Node<T> left = n.left;
			Node<T> right = n.right;
			n.height = 1+Math.max(left.height, right.height);
			n.boundingBox = GeometryUtils.combine(left.boundingBox, right.boundingBox);
			n = n.parent;
		}
	}

	public void rebuild() {
		ArrayList<Node<T>> nodes = root.getAll();
		for(Node<T> n : nodes) {
			n.boundingBox.update();
			if(n.parent!=null&&!GeometryUtils.contains(n.parent.boundingBox, n.boundingBox)) {
				remove(n);
				insertLeaf(n);
			}
		}
	}

	public void cull(Node<T> node) {
		Node<T> n = node;
		while(n.data==null&&n.left==null&&n.right==null) {
			remove(n);
			n=n.parent;
		}
	}

	public void remove(Node<T> leaf) {
		if(leaf.isLeaf()) {
			if(leaf.parent.left==leaf) {
				leaf.parent.left=leaf.left;
				leaf.left.parent=leaf.parent;
			} else {
				leaf.parent.right=null;
			}
		}
		cull(leaf.parent);
	}
	
	public static class Node<T> implements Serializable {
		
		private static final long serialVersionUID = -393595206105143426L;
		
		Node<T> parent;
		Node<T> left;
		Node<T> right;
		AABB boundingBox;
		int height;
		T data;

		public Node(AABB box, T d) {
			boundingBox = box;
			data = d;
		}

		public T getData() {
			return data;
		}
		
		public ArrayList<T> query(AABB box) {
			ArrayList<T> o = new ArrayList<T>();
			if(GeometryUtils.intersects(box, boundingBox)) {
				if(isLeaf()&&boundingBox!=box)o.add(data);
				if(left!=null)o.addAll(left.query(box));
				if(right!=null)o.addAll(right.query(box));
			}
			return o;
		}
		
		public ArrayList<T> query(Vector3d point) {
			ArrayList<T> o = new ArrayList<T>();
			if(GeometryUtils.contains(boundingBox,point)) {
				if(isLeaf())o.add(data);
				if(left!=null)o.addAll(left.query(point));
				if(right!=null)o.addAll(right.query(point));
			}
			return o;
		}
		
		public ArrayList<T> query(Ray ray) {
			ArrayList<T> o = new ArrayList<T>();
			if(GeometryUtils.intersects(ray,boundingBox)) {
				if(isLeaf())o.add(data);
				if(left!=null)o.addAll(left.query(ray));
				if(right!=null)o.addAll(right.query(ray));
			}
			return o;
		}
		
		public boolean isLeaf() {
			return right==null;
		}

		public int getSize() {
			return 1 + (left==null?0:left.getSize()) + (right==null?0:right.getSize());
		}

		public ArrayList<Node<T>> getAll() {
			ArrayList<Node<T>> n = new ArrayList<Node<T>>();
			if(isLeaf())n.add(this);
			if(left!=null)n.addAll(left.getAll());
			if(right!=null)n.addAll(right.getAll());
			return n;
		}
		
		public int getDepth() {
			return height;
		}

		public AABB getBoundingBox() {
			return boundingBox;
		}

		public void translate(Vector3d t) {
			boundingBox.getCenter().add(t);
			boundingBox.update();
			if(left!=null)left.translate(t);
			if(right!=null)right.translate(t);
		}

		@Override
		public String toString() {
			return "{"+boundingBox+","+left+","+right+"}";
		}
	}
}