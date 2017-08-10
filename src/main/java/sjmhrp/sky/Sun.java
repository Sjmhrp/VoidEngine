package sjmhrp.sky;

import sjmhrp.linear.Vector3d;
import sjmhrp.utils.MatrixUtils;

public class Sun extends CelestialBody{

	private static final long serialVersionUID = 6515103644630075102L;
	
	private final Vector3d direction = new Vector3d(0,0,1);
	private double dayLength = 1200;

	@Override
	public void tick(double dt) {
		Vector3d angle = Vector3d.scale(dt*2*Math.PI/dayLength,direction);
		MatrixUtils.createRotation(angle).transform(position);
	}

	public double getDayLength() {
		return dayLength;
	}

	public void setDayLength(double dayLength) {
		this.dayLength = dayLength;
	}

	public Vector3d getDirection() {
		return direction;
	}
}