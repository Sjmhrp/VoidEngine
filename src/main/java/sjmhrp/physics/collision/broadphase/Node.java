package sjmhrp.physics.collision.broadphase;

import java.util.ArrayList;

import sjmhrp.linear.Vector3d;
import sjmhrp.utils.GeometryUtils;

public class Node {

	Node parent;
	Node left;
	Node right;
	AABB boundingBox;
	int height;
	Object data;

	public Node(AABB box, Object d) {
		boundingBox = box;
		data = d;
	}

	public Object getData() {
		return data;
	}
	
	public ArrayList<Object> query(AABB box) {
		ArrayList<Object> o = new ArrayList<Object>();
		if(GeometryUtils.intersects(box, boundingBox)) {
			if(isLeaf()&&boundingBox!=box)o.add(data);
			if(left!=null)o.addAll(left.query(box));
			if(right!=null)o.addAll(right.query(box));
		}
		return o;
	}
	
	public boolean isLeaf() {
		return right==null;
	}

	public int getSize() {
		return 1 + (left==null?0:left.getSize()) + (right==null?0:right.getSize());
	}

	public ArrayList<Node> getAll() {
		ArrayList<Node> n = new ArrayList<Node>();
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