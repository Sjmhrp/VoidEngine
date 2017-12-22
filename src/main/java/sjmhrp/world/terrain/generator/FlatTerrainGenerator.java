package sjmhrp.world.terrain.generator;

public class FlatTerrainGenerator extends TerrainGenerator{

	private static final long serialVersionUID = -8203013069500406233L;

	@Override
	public double getDensity(double x, double y, double z) {
		return Math.abs(y+20)-20;
	}
}