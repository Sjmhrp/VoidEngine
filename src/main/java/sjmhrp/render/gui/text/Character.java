package sjmhrp.render.gui.text;

public class Character {

	private int id;
	private double xTextureCoord;
	private double yTextureCoord;
	private double xMaxTextureCoord;
	private double yMaxTextureCoord;
	private double xOffset;
	private double yOffset;
	private double sizeX;
	private double sizeY;
	private double xAdvance;

	public Character(int id, double xTextureCoord, double yTextureCoord,
			double xTexSize, double yTexSize, double xOffset,
			double yOffset, double sizeX, double sizeY, double xAdvance) {
		this.id = id;
		this.xTextureCoord = xTextureCoord;
		this.yTextureCoord = yTextureCoord;
		this.xMaxTextureCoord = xTextureCoord+xTexSize;
		this.yMaxTextureCoord = yTextureCoord+yTexSize;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.xAdvance = xAdvance;
	}

	public int getId() {
		return id;
	}

	public double getxTextureCoord() {
		return xTextureCoord;
	}

	public double getyTextureCoord() {
		return yTextureCoord;
	}

	public double getXMaxTextureCoord() {
		return xMaxTextureCoord;
	}

	public double getYMaxTextureCoord() {
		return yMaxTextureCoord;
	}

	public double getxOffset() {
		return xOffset;
	}

	public double getyOffset() {
		return yOffset;
	}

	public double getSizeX() {
		return sizeX;
	}

	public double getSizeY() {
		return sizeY;
	}

	public double getxAdvance() {
		return xAdvance;
	}
}