package sjmhrp.io.colladaloader;

public class AnimationData {

	public final double length;
	public final KeyFrameData[] keyFrames;
	
	public AnimationData(double length, KeyFrameData[] keyFrames) {
		this.length=length;
		this.keyFrames=keyFrames;
	}
}