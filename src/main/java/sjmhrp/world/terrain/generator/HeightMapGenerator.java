package sjmhrp.world.terrain.generator;

import static sjmhrp.utils.ScalarUtils.bilerp;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import sjmhrp.core.Globals;
import sjmhrp.io.Log;
import sjmhrp.utils.linear.Vector2d;

public class HeightMapGenerator extends TerrainGenerator{

	private static final long serialVersionUID = 344866199876523651L;
	
	private final static String RES_LOC = "/res/textures/map/";
	private static final int MAX_PIXEL_COUNT = 256 * 256 * 128;
	
	transient BufferedImage image;
	String heightMap;
	
	public HeightMapGenerator(String file) {
		heightMap = file;
		try {
			image = ImageIO.read(Class.class.getResourceAsStream(RES_LOC+heightMap+".png"));
		} catch(Exception e) {
			Log.printError(e);
		}
	}

	@Override
	public double getDensity(double x, double y, double z) {
		return y-getHeight(x,z);
	}
	
	public double getHeight(double xPos, double zPos) {
		return bilerp(new Vector2d(xPos,zPos),this::getHeight);
	}
	
	public double getHeight(int x, int z) {
		x=Math.floorMod(x,image.getHeight());
		z=Math.floorMod(z,image.getWidth());
		if(x<0 || x>=image.getHeight() || z<0 || z>=image.getWidth())return 0;
		double height = image.getRGB(x,z);
		height += MAX_PIXEL_COUNT;
		height /= MAX_PIXEL_COUNT;
		height *= Globals.MAX_TERRAIN_HEIGHT;
		return height;
	}
	
	@Override
	public void reload() {
		try {
			image = ImageIO.read(Class.class.getResourceAsStream(RES_LOC+heightMap+".png"));
		} catch(Exception e) {
			Log.printError(e);
		}	
	}
}