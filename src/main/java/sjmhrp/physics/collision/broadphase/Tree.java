package sjmhrp.physics.collision.broadphase;

import java.io.Serializable;
import java.util.ArrayList;

import sjmhrp.linear.Vector3d;
import sjmhrp.physics.dynamics.Ray;
import sjmhrp.utils.GeometryUtils;

public class Tree implements Serializable {
	
	private static final long serialVersionUID = -5947015951035884776L;
	
	public Node root;
	int leaves = 0;

	public ArrayList<Object> getAll() {
		ArrayList<Node> nodes = root.getAll();
		ArrayList<Object> o = new ArrayList<Object>();
		for(Node n : nodes) {
			if(n.data!=null)o.add(n.getData());
		}
		return o;
	}
	
	public int getHeight() {
		return root.height;
	}

	public ArrayList<Object> query(AABB box) {
		return root.query(box);
	}

	public ArrayList<Object> query(Ray ray) {
		return root.query(ray);
	}
	
	public void translate(Vector3d t) {
		root.translate(t);
	}

	public void add(AABB aabb, Object data) {
		Node n = new Node(aabb, data);
		insertLeaf(n);
		leaves++;
	}

	public void insertLeaf(Node leaf) {
		if(root==null) {
			root = leaf;
			leaf.parent = null;
			return;
		}
		AABB leafAABB = leaf.boundingBox;
		Node node = root;
		while(!node.isLeaf()) {
			Node left = node.left;
			Node right = node.right;
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
		Node oldParent = node.parent;
		AABB a = GeometryUtils.combine(leafAABB, node.boundingBox);
		Node newParent = new Node(a,null);
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

	public Node balance(Node a) {
		if(a.isLeaf()||a.height<2) {
			return a;
		}
		Node b = a.left;
		Node c = a.right;
		int balance = c.height-b.height;
		if(balance>1) {
			Node f = c.left;
			Node g = c.right;
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
			Node d = b.left;
			Node e = b.right;
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

	public void syncHierarchy(Node node) {
		Node n = node;
		while(n!=null) {
			n = balance(n);
			Node left = n.left;
			Node right = n.right;
			n.height = 1+Math.max(left.height, right.height);
			n.boundingBox = GeometryUtils.combine(left.boundingBox, right.boundingBox);
			n = n.parent;
		}
	}

	public void rebuild() {
		ArrayList<Node> nodes = root.getAll();
		for(Node n : nodes) {
			n.boundingBox.update();
			if(n.parent!=null&&!GeometryUtils.contains(n.parent.boundingBox, n.boundingBox)) {
				remove(n);
				insertLeaf(n);
			}
		}
	}

	public void cull(Node node) {
		Node n = node;
		while(n.data==null&&n.left==null&&n.right==null) {
			remove(n);
			n=n.parent;
		}
	}

	public void remove(Node leaf) {
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
}