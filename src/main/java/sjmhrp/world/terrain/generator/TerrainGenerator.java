package sjmhrp.world.terrain.generator;

import java.io.Serializable;

import sjmhrp.utils.linear.Vector3d;

public abstract class TerrainGenerator implements Serializable {
	
	private static final long serialVersionUID = -8788970284565888334L;

	double normalResolution = 0.001;
	
	public double getDensity(Vector3d p) {
		return getDensity(p.x,p.y,p.z);
	}
	
	public abstract double getDensity(double x, double y, double z);

	public Vector3d getNormal(Vector3d p) {
		return getNormal(p.x,p.y,p.z);
	}
	
	public Vector3d getNormal(double x, double y, double z) {
		return new Vector3d(getDensity(x+normalResolution,y,z)-getDensity(x-normalResolution,y,z),getDensity(x,y+normalResolution,z)-getDensity(x,y-normalResolution,z),getDensity(x,y,z+normalResolution)-getDensity(x,y,z-normalResolution)).normalize();
	}
	
	public void reload() {}
}