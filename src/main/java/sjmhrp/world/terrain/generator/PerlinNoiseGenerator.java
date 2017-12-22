package sjmhrp.world.terrain.generator;

import java.util.HashMap;
import java.util.Random;

import sjmhrp.core.Globals;
import sjmhrp.utils.ScalarUtils;
import sjmhrp.utils.linear.Vector2d;

public class PerlinNoiseGenerator extends TerrainGenerator {

	private static final long serialVersionUID = -2169472997018228619L;
	
	private static final int OCTAVES = 3;
	private static final double ROUGHNESS = 0.3f;
	
	private Random r = new Random();
	transient private HashMap<Vector2d,Double> heights = new HashMap<Vector2d,Double>();
	private int seed;

	public PerlinNoiseGenerator() {
		this(new Random().nextInt());
	}
	
	public PerlinNoiseGenerator(int s) {
		this.seed = s;
	}

	@Override
	public double getDensity(double x, double y, double z) {
		return y-generateHeight(x,z);
	}
	
	public double generateHeight(double x, double z) {
		double SCALE = 1d/4d;
		double total = 0;
		double d = Math.pow(2, OCTAVES-1);
		for(int i = 0; i < OCTAVES; i++) {
			double freq = Math.pow(2,i)/d;
			double amp = Math.pow(ROUGHNESS, i) * Globals.MAX_TERRAIN_HEIGHT;
			total += getInterpolatedNoise(x*freq*SCALE,z*freq*SCALE)*amp;
		}
		return total;
	}

	private double getSmoothNoise(int x, int z) {
		Double height = heights.get(new Vector2d(x,z));
		if(height!=null)return height;
		double corners = (getNoise(x-1,z-1)+getNoise(x+1,z-1)+getNoise(x-1,z+1)+getNoise(x+1,z+1))/16f;
		double sides = (getNoise(x,z-1)+getNoise(x,z+1)+getNoise(x-1,z)+getNoise(x+1,z))/8f;
		double center = getNoise(x,z)/4f;
		height = corners+sides+center;
		heights.put(new Vector2d(x,z),height);
		return height;
	}

	private double getNoise(int x, int z) {
		r.setSeed(seed+x*49632+z*325176);
		return r.nextFloat()*2f-1f;
	}

	private double getInterpolatedNoise(double x, double z) {
		int intX = ScalarUtils.fastFloor(x);
		int intZ = ScalarUtils.fastFloor(z);
		double fracX = (double)(x-intX);
		double fracZ = (double)(z-intZ);
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
		double f = (1-Math.cos(theta))*0.5;
		return ScalarUtils.lerp(a,b,f);
	}
	
	@Override
	public void reload() {
		heights = new HashMap<Vector2d,Double>();
	}
}