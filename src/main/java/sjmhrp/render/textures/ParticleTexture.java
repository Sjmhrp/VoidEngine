package sjmhrp.render.textures;

public class ParticleTexture {

	private int texture;
	private int rows;

	public ParticleTexture(int texture) {
		this(texture,1);
	}
	
	public ParticleTexture(int texture, int rows) {
		this.texture = texture;
		this.rows = rows;
	}

	public int getTexture() {
		return texture;
	}

	public int getRows() {
		return rows;
	}
}