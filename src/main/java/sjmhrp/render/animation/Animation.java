package sjmhrp.render.animation;

import java.io.Serializable;

public class Animation implements Serializable {

	private static final long serialVersionUID = -3236590898021465631L;

	private final double length;
	private final KeyFrame[] keyFrames;
	
	public Animation(double length, KeyFrame[] keyFrames) {
		this.length=length;
		this.keyFrames=keyFrames;
	}
	
	public double getLength() {
		return length;
	}
	
	public KeyFrame[] getKeyFrames() {
		return keyFrames;
	}
}