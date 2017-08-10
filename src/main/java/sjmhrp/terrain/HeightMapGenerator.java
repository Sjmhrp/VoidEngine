package sjmhrp.terrain;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import sjmhrp.io.Log;

public class HeightMapGenerator extends HeightGenerator{

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
	public double generateHeight(int x, int z) {
		if(x<0 || x>=image.getHeight() || z<0 || z>=image.getWidth()) {
			return 0;
		}
		double height = image.getRGB(x,z);
		height += MAX_PIXEL_COUNT;
		height /= MAX_PIXEL_COUNT;
		height *= Terrain.MAX_HEIGHT;
		return height;
	}
	
	@Override
	public int getVertexCount() {
		return image.getHeight();
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