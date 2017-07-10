package sjmhrp.sky;

import sjmhrp.linear.Vector3d;

public abstract class CelestialBody {

	protected final Vector3d position = new Vector3d();
	protected final Vector3d colour = new Vector3d();
	protected double size;
	
	String texture;
	
	public abstract void tick(double dt);

	public Vector3d getPosition() {
		return position;
	}

	public Vector3d getColour() {
		return colour;
	}
	
	public void setSize(double size) {
		this.size=size;
	}
	
	public double getSize() {
		return size;
	}
	
	public void setTexture(String texture) {
		this.texture=texture;
	}
	
	public String getTexture() {
		return texture;
	}
}