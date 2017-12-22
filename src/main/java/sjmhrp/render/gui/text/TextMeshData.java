package sjmhrp.render.gui.text;

public class TextMeshData {
    
    private double[] vertexPositions;
    private double[] textureCoords;
     
    protected TextMeshData(double[] vertexPositions, double[] textureCoords){
        this.vertexPositions = vertexPositions;
        this.textureCoords = textureCoords;
    }
 
    public double[] getVertexPositions() {
        return vertexPositions;
    }
 
    public double[] getTextureCoords() {
        return textureCoords;
    }
 
    public int getVertexCount() {
        return vertexPositions.length/2;
    }
}