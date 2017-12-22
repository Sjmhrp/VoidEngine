package sjmhrp.render.animation;

import sjmhrp.render.models.MeshData;
import sjmhrp.render.models.RawModel;
import sjmhrp.utils.linear.Matrix4d;

public class AnimatedModel extends RawModel {

	private static final long serialVersionUID = -101173497025162530L;
	private AnimatedJoint root;
	private int jointCount;
	private Animator animator;
	
	public AnimatedModel(int vaoId, int vertexCount, MeshData meshData, AnimatedJoint root, int jointCount) {
		super(vaoId,vertexCount,meshData);
		this.root=root;
		this.jointCount=jointCount;
		animator = new Animator(this);
		root.calcInverseTransform(new Matrix4d().setIdentity());
	}
	
	public AnimatedJoint getRootJoint() {
		return root;
	}
	
	public AnimatedModel addAnimation(Animation animation) {
		animator.addAnimation(animation);
		return this;
	}
	
	public int getAnimation() {
		return animator.getAnimation();
	}
	
	public Matrix4d[] getJointTransforms() {
		Matrix4d[] jointMatrices = new Matrix4d[jointCount];
		addJointsToArray(root,jointMatrices);
		return jointMatrices;
	}
	
	void addJointsToArray(AnimatedJoint head, Matrix4d[] jointMatrices) {
		jointMatrices[head.index]=head.getAnimatedTransform();
		for(AnimatedJoint child : head.children) {
			addJointsToArray(child,jointMatrices);
		}
	}
}