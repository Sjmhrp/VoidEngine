package sjmhrp.io.colladaloader;

import java.util.ArrayList;

public class SkinningData {

	public final ArrayList<String> jointOrder;
	public final ArrayList<VertexSkinData> verticesSkinData;
	
	public SkinningData(ArrayList<String> jointOrder, ArrayList<VertexSkinData> verticesSkinData) {
		this.jointOrder=jointOrder;
		this.verticesSkinData=verticesSkinData;
	}
}