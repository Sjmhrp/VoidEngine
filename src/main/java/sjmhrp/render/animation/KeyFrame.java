package sjmhrp.render.animation;

import java.io.Serializable;
import java.util.HashMap;

import sjmhrp.io.colladaloader.JointTransformData;
import sjmhrp.io.colladaloader.KeyFrameData;
import sjmhrp.utils.linear.Transform;

public class KeyFrame implements Serializable {

	private static final long serialVersionUID = 3496376405881568473L;

	private final double timeStamp;
	private final HashMap<String,Transform> transforms;
	
	public KeyFrame(double timeStamp, HashMap<String,Transform> transforms) {
		this.timeStamp=timeStamp;
		this.transforms=transforms;
	}
	
	public KeyFrame(KeyFrameData data) {
		transforms = new HashMap<String,Transform>();
		for(JointTransformData j : data.transforms) {
			transforms.put(j.name,new Transform(j.transform));
		}
		timeStamp=data.time;
	}
	
	public double getTimeStamp() {
		return timeStamp;
	}
	
	public HashMap<String,Transform> getTransforms() {
		return transforms;
	}
}