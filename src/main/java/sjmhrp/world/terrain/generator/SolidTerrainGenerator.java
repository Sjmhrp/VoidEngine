package sjmhrp.world.terrain.generator;

public class SolidTerrainGenerator extends TerrainGenerator {

	private static final long serialVersionUID = -4462452924990777092L;

	double density = -10;
	
	public SolidTerrainGenerator() {}
	
	public SolidTerrainGenerator(double density) {
		this.density=density;
	}
	
	@Override
	public double getDensity(double x, double y, double z) {
		return density;
	}
}