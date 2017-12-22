package sjmhrp.world.terrain.generator;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static sjmhrp.utils.linear.Vector3d.add;
import static sjmhrp.utils.linear.Vector3d.sub;

import sjmhrp.physics.collision.broadphase.AABB;
import sjmhrp.utils.GeometryUtils;
import sjmhrp.utils.VectorUtils;
import sjmhrp.utils.linear.Vector2d;
import sjmhrp.utils.linear.Vector3d;

public abstract class TerrainShape extends TerrainGenerator {

	private static final long serialVersionUID = -4793114101360088768L;
	
	public boolean positive = true;

	public abstract AABB getBounds();
	
	public static class SphereTerrain extends TerrainShape {
		
		private static final long serialVersionUID = -8510499785496890094L;
		
		Vector3d origin;
		double radius;
		
		public SphereTerrain(Vector3d origin, double radius) {
			this(true,origin,radius);
		}
		
		public SphereTerrain(boolean positive, Vector3d origin, double radius)  {
			this.positive=positive;
			this.origin=origin;
			this.radius=radius;
		}

		@Override
		public AABB getBounds() {
			return new AABB(sub(origin,new Vector3d(radius)),add(origin,new Vector3d(radius)));
		}

		@Override
		public double getDensity(double x, double y, double z) {
			return GeometryUtils.sphereSDF(new Vector3d(x,y,z),origin,radius);
		}
	}
	
	public static class CubeTerrain extends TerrainShape {
		
		private static final long serialVersionUID = -8090458861472758439L;
		
		Vector3d origin;
		Vector3d extent;
		
		public CubeTerrain(boolean positive, Vector3d origin, Vector3d extent) {
			this.positive=positive;
			this.origin=origin;
			this.extent=extent;
		}
		
		@Override
		public AABB getBounds() {
			return new AABB(extent.getNegative().add(origin),new Vector3d(extent).add(origin));
		}
		
		@Override
		public double getDensity(double x, double y, double z) {
			Vector3d d = new Vector3d(x,y,z).sub(origin).abs().sub(extent);
			return Math.min(VectorUtils.max(d),VectorUtils.max(d,new Vector3d()).length());
		}
	}
	
	public static class CylinderTerrain extends TerrainShape {

		private static final long serialVersionUID = 7228830821497969372L;
		
		Vector3d origin;
		double radius;
		double height;
		
		public CylinderTerrain(boolean positive, Vector3d origin, double radius, double height) {
			this.positive=positive;
			this.origin=origin;
			this.radius=radius;
			this.height=height;
		}
		
		@Override
		public AABB getBounds() {
			return new AABB(new Vector3d(-2*radius,-2*height,-2*radius).add(origin),new Vector3d(2*radius,2*height,2*radius).add(origin));
		}

		@Override
		public double getDensity(double x, double y, double z) {
			double d = pow((x-origin.x),2)+pow((z-origin.z),2)-radius*radius;
			Vector2d v = new Vector2d(d,abs(y-origin.y)-height);
			return Math.min(VectorUtils.max(v),VectorUtils.max(v,new Vector2d()).length());
		}
	}
}