package sjmhrp.terrain;

import java.util.Random;

public class PerlinNoiseGenerator extends HeightGenerator{

	private static final long serialVersionUID = -2169472997018228619L;
	
	private static final int OCTAVES = 3;
	private static final double ROUGHNESS = 0.3f;
	
	private int gridX;
	private int gridZ;
	private Random r = new Random();
	private int seed;

	public PerlinNoiseGenerator(int x, int z, int s) {
		this.seed = s;
		this.gridX = x * (getVertexCount()-1);
		this.gridZ = z * (getVertexCount()-1);
	}

	public double generateHeight(int x, int z) {
		double total = 0;
		double d = (double) Math.pow(2, OCTAVES-1);
		for(int i = 0; i < OCTAVES; i++) {
			double freq = (double) (Math.pow(2, i)/d);
			double amp = (double) (Math.pow(ROUGHNESS, i) * Terrain.MAX_HEIGHT);
			total += getInterpolatedNoise((x+gridX)*freq,(z+gridZ)*freq)*amp;
		}
		return total;
	}

	private double getSmoothNoise(int x, int z) {
		double corners = (getNoise(x-1,z-1)+getNoise(x+1,z-1)+getNoise(x-1,z+1)+getNoise(x+1,z+1))/16f;
		double sides = (getNoise(x,z-1)+getNoise(x,z+1)+getNoise(x-1,z)+getNoise(x+1,z))/8f;
		double center = getNoise(x,z)/4f;
		return corners+sides+center;
	}

	private double getNoise(int x, int z) {
		r.setSeed(seed+x*49632+z*325176);
		return r.nextFloat()*2f-1f;
	}

	private double getInterpolatedNoise(double x, double z) {
		int intX = (int) x;
		int intZ = (int) z;
		double fracX = (double) (x-intX);
		double fracZ = (double) (z-intZ);
		double v1 = getSmoothNoise(intX,intZ);
		double v2 = getSmoothNoise(intX+1,intZ);
		double v3 = getSmoothNoise(intX,intZ+1);
		double v4 = getSmoothNoise(intX+1,intZ+1);
		double i1 = interpolate(v1,v2,fracX);
		double i2 = interpolate(v3,v4,fracX);
		return interpolate(i1,i2,fracZ);
	}

	private double interpolate(double a, double b, double blend) {
		double theta = Math.PI*blend;
		double f = (double) (1f-Math.cos(theta))*0.5f;
		return a*(1-f)+b*f;
	}
}