package sjmhrp.sky;

import sjmhrp.linear.Vector3d;

public class Star {

	private final Vector3d position = new Vector3d();
	private final Vector3d colour = new Vector3d();
	private double radius;
	
	public Star(Vector3d position, Vector3d colour) {
		this(position,colour,1);
	}
	
	public Star(Vector3d position, Vector3d colour, double radius) {
		this.position.set(position);
		this.colour.set(colour);
		this.radius=radius;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public Vector3d getPosition() {
		return position;
	}

	public Vector3d getColour() {
		return colour;
	}
}