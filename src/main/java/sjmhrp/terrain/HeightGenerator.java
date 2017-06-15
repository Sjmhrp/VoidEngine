package sjmhrp.terrain;

public abstract class HeightGenerator {

	protected int gridX;
	protected int gridZ;

	public abstract double generateHeight(int x, int z);
}
