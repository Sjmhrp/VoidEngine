package sjmhrp.render.light;

import java.io.Serializable;

import sjmhrp.render.RenderRegistry;
import sjmhrp.utils.linear.Vector3d;

public class Light implements Serializable {
	
	private static final long serialVersionUID = -8059310806061877049L;

	static double minLight = 51.2;
	
	private Vector3d pos;
	private Vector3d colour;
	private Vector3d attenuation;

	public Light() {}

	public Light(Vector3d pos, Vector3d colour) {
		this(pos,colour,new Vector3d(1,0,0));
	}
	
	public Light(Vector3d pos, Vector3d colour, Vector3d at) {
		this.pos = pos;
		this.colour = colour;
		this.attenuation = at;
		RenderRegistry.registerLight(this);
	}
	
	public void remove() {
		RenderRegistry.removeLight(this);
	}
	
	public Vector3d getAttenuation() {
		return attenuation;
	}

	public Vector3d getPos() {
		return pos;
	}
	
	public Vector3d getColour() {
		return colour;
	}

	public void setPos(Vector3d pos) {
		this.pos = pos;
	}
	
	public double getSize() {
		double maxIntensity = colour.length();
		double a = attenuation.x;
		double b = attenuation.y;
		double c = attenuation.z;
		double s = 4f*c*(a-minLight*maxIntensity);
		s = b*b-s;
		s = (double) (-b+Math.sqrt(s));
		s /= 2f*c;
		return s;
	}
}