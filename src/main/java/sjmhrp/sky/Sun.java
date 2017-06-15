package sjmhrp.sky;

import sjmhrp.linear.Quaternion;
import sjmhrp.linear.Vector3d;
import sjmhrp.utils.MatrixUtils;

public class Sun {

	public static final double AU = 400;
	
	private final Vector3d position = new Vector3d();
	private final Quaternion orientation = new Quaternion();
	private final Vector3d colour = new Vector3d(1,1,1);
	private double size = 80;
	private Vector3d direction = new Vector3d(0,0,1);
	private double dayLength = 1200;

	public Sun(Vector3d position, Quaternion orientation) {
		this(position,orientation,new Vector3d(1,1,1));
	}

	public Sun(Vector3d position, Quaternion orientation, Vector3d colour) {
		this.position.set(position);
		this.orientation.set(orientation);
		this.colour.set(colour);
	}

	public void setSize(double size) {
		this.size=size;
	}
	
	public void setDirection(Vector3d dir) {
		direction.set(dir.getUnit());
	}
	
	public void setDayLength(double t) {
		dayLength=t;
	}
	
	public void rotate(double dt) {
		Vector3d angle = Vector3d.scale(dt*2*Math.PI/dayLength,direction);
		MatrixUtils.createRotation(angle).transform(position);
		orientation.rotate(angle,1);
	}
	
	public double getSize() {
		return size;
	}
	
	public Vector3d getDirection() {
		return direction;
	}

	public double getDayLength() {
		return dayLength;
	}

	public Vector3d getPosition() {
		return position;
	}
	
	public Quaternion getOrientation() {
		return orientation;
	}
	
	public Vector3d getColour() {
		return colour;
	}
}