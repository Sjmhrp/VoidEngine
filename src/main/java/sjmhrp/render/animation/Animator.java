package sjmhrp.render.animation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import sjmhrp.event.TickListener;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Transform;

public class Animator implements TickListener,Serializable {

	private static final long serialVersionUID = -3631049134665193383L;

	static final double ANIMATION_TRANSITION_TIME = 1;
	
	private final AnimatedModel model;
	private double time = 0;
	private ArrayList<Animation> animations;
	private int currentAnimation;
	private double transitionTime;
	private KeyFrame oldPose;
	
	{PhysicsEngine.addTickListener(this);}
	
	public Animator(AnimatedModel model) {
		this.model=model;
		animations = new ArrayList<Animation>();
	}
	
	public void addAnimation(Animation animation) {
		time=0;
		if(!animations.contains(animation))animations.add(animation);
		currentAnimation=animations.indexOf(animation);
	}
	
	public void doAnimation(int i) {
		time=0;
		HashMap<String,Matrix4d> pose = calcCurrentPose();
		HashMap<String,Transform> transforms = new HashMap<String,Transform>();
		for(Entry<String,Matrix4d> e : pose.entrySet()) {
			transforms.put(e.getKey(),new Transform(e.getValue()));
		}
		oldPose = new KeyFrame(time,transforms);
		transitionTime=ANIMATION_TRANSITION_TIME;
	}
	
	public int getAnimation() {
		return currentAnimation;
	}
	
	@Override
	public void tick() {
		if(PhysicsEngine.isPaused()||currentAnimation<0||currentAnimation>animations.size()-1||animations.get(currentAnimation)==null)return;
		increaseTime(PhysicsEngine.getTimeStep());
		HashMap<String,Matrix4d> pose = calcCurrentPose();
		applyPose(pose,model.getRootJoint(),new Matrix4d().setIdentity());
	}
	
	void increaseTime(double dt) {
		time+=dt;
		time%=animations.get(currentAnimation).getLength();
		if(transitionTime>0)transitionTime-=dt;
		if(transitionTime==0)transitionTime=0;
	}
	
	void applyPose(HashMap<String,Matrix4d> pose, AnimatedJoint joint, Matrix4d parentTransform) {
		Matrix4d localTransform = pose.get(joint.name);
		Matrix4d transform = Matrix4d.mul(parentTransform,localTransform);
		for(AnimatedJoint child : joint.children) {
			applyPose(pose,child,transform);
		}
		transform.mul(joint.getInverseTransform());
		joint.setAnimatedTransform(transform);
	}
	
	HashMap<String,Matrix4d> calcCurrentPose() {
		KeyFrame[] frames = getPrevNextFrames();
		double f = calcProgression(frames[0],frames[1]);
		return interpolate(frames[0],frames[1],f);
	}
	
	KeyFrame[] getPrevNextFrames() {
		KeyFrame prev = null;
		KeyFrame next = null;
		for(KeyFrame frame : animations.get(currentAnimation).getKeyFrames()) {
			if(frame.getTimeStamp()>time) {
				next=frame;
				break;
			}
			prev=frame;
		}
		prev=prev==null?next:prev;
		next=next==null?prev:next;
		if(transitionTime!=0)prev=oldPose;
		return new KeyFrame[]{prev,next};
	}
	
	double calcProgression(KeyFrame a, KeyFrame b) {
		return (time-a.getTimeStamp())/(b.getTimeStamp()-a.getTimeStamp());
	}
	
	HashMap<String,Matrix4d> interpolate(KeyFrame a, KeyFrame b, double f) {
		HashMap<String,Matrix4d> pose = new HashMap<String,Matrix4d>();
		for(String name : a.getTransforms().keySet()) {
			Transform prev = a.getTransforms().get(name);
			Transform next = b.getTransforms().get(name);
			pose.put(name,Transform.lerp(prev,next,f).getMatrix());
		}
		return pose;
	}

	@Override
	public boolean keepOnLoad() {
		return true;
	}
}