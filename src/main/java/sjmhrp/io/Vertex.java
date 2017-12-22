package sjmhrp.io;
 
import java.util.ArrayList;
import java.util.List;

import sjmhrp.utils.linear.Vector3d;
 
public class Vertex {
     
    private static final int NO_INDEX = -1;
     
    private Vector3d position;
    private int textureIndex = NO_INDEX;
    private int normalIndex = NO_INDEX;
    private Vertex duplicateVertex = null;
    private int index;
    private double length;
    private List<Vector3d> tangents = new ArrayList<Vector3d>();
    private Vector3d averagedTangent = new Vector3d(0, 0, 0);
     
    protected Vertex(int index, Vector3d position){
        this.index = index;
        this.position = position;
        this.length = position.length();
    }
     
    protected void addTangent(Vector3d tangent){
        tangents.add(tangent);
    }
     
    protected Vertex duplicate(int newIndex){
        Vertex vertex = new Vertex(newIndex, position);
        vertex.tangents = this.tangents;
        return vertex;
    }
     
    protected void averageTangents(){
        if(tangents.isEmpty()){
            return;
        }
        for(Vector3d tangent : tangents){
        	averagedTangent.add(tangent);
        }
        averagedTangent.normalize();
    }

    protected Vector3d getAverageTangent(){
        return averagedTangent;
    }
     
    protected int getIndex(){
        return index;
    }
     
    protected double getLength(){
        return length;
    }
     
    protected boolean isSet(){
        return textureIndex!=NO_INDEX && normalIndex!=NO_INDEX;
    }
     
    protected boolean hasSameTextureAndNormal(int textureIndexOther,int normalIndexOther){
        return textureIndexOther==textureIndex && normalIndexOther==normalIndex;
    }
     
    protected void setTextureIndex(int textureIndex){
        this.textureIndex = textureIndex;
    }
     
    protected void setNormalIndex(int normalIndex){
        this.normalIndex = normalIndex;
    }
 
    protected Vector3d getPosition() {
        return position;
    }
 
    protected int getTextureIndex() {
        return textureIndex;
    }
 
    protected int getNormalIndex() {
        return normalIndex;
    }
 
    protected Vertex getDuplicateVertex() {
        return duplicateVertex;
    }
 
    protected void setDuplicateVertex(Vertex duplicateVertex) {
        this.duplicateVertex = duplicateVertex;
    }
 
}