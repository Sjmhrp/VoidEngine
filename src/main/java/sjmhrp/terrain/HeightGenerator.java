package sjmhrp.terrain;

import java.io.Serializable;

public abstract class HeightGenerator implements Serializable {
	
	private static final long serialVersionUID = -8788970284565888334L;

	public static final int DEFAULT_VERTEX_COUNT = 128;

	public abstract double generateHeight(int x, int z);
	
	public int getVertexCount() {
		return DEFAULT_VERTEX_COUNT;
	}
	
	public void reload() {}
}