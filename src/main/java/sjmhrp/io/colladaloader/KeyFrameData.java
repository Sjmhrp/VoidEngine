package sjmhrp.io.colladaloader;

import java.util.ArrayList;

public class KeyFrameData {

	public final double time;
	public final ArrayList<JointTransformData> transforms = new ArrayList<JointTransformData>();
	
	public KeyFrameData(double time) {
		this.time=time;
	}
	
	public void addTransform(JointTransformData transform) {
		transforms.add(transform);
	}
}