package sjmhrp.render.animation;

import java.io.Serializable;
import java.util.ArrayList;

import sjmhrp.io.colladaloader.JointData;
import sjmhrp.utils.linear.Matrix4d;

public class AnimatedJoint implements Serializable {

	private static final long serialVersionUID = -1688082371921229493L;

	public final int index;
	public final String name;
	public final ArrayList<AnimatedJoint> children = new ArrayList<AnimatedJoint>();
	
	private final Matrix4d localTransform;
	private Matrix4d inverseTransform;
	private Matrix4d animatedTransform = new Matrix4d().setIdentity();
	
	public AnimatedJoint(int index, String name, Matrix4d localTransform) {
		this.index = index;
		this.name = name;
		this.localTransform = localTransform;
	}
	
	public AnimatedJoint(JointData data) {
		this(data.index,data.name,data.transform);
		for(JointData child : data.children) {
			addChild(new AnimatedJoint(child));
		}
	}
	
	public void addChild(AnimatedJoint child) {
		children.add(child);
	}
	
	public Matrix4d getInverseTransform() {
		return inverseTransform;
	}
	
	public Matrix4d getAnimatedTransform() {
		return animatedTransform;
	}
	
	public void setAnimatedTransform(Matrix4d matrix) {
		animatedTransform=matrix;
	}
	
	void calcInverseTransform(Matrix4d parentTransform) {
		Matrix4d transform = Matrix4d.mul(parentTransform,localTransform);
		inverseTransform = transform.getInverse();
		for(AnimatedJoint j : children) {
			j.calcInverseTransform(transform);
		}
	}
}