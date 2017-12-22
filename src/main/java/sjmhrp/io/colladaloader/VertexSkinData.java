package sjmhrp.io.colladaloader;

import java.util.ArrayList;

public class VertexSkinData {

	public final ArrayList<Integer> ids = new ArrayList<Integer>();
	public final ArrayList<Double> weights = new ArrayList<Double>();
	
	public void addJointWeight(int id, double weight) {
		for(int i = 0; i < weights.size(); i++) {
			if(weight>weights.get(i)) {
				ids.add(i,id);
				weights.add(i,weight);
				return;
			}
		}
		ids.add(id);
		weights.add(weight);
	}
	
	public void limitJointNumber(int max) {
		if(ids.size()>max) {
			double[] top = new double[max];
			double total = 0;
			for(int i = 0; i < top.length; i++) {
				top[i]=weights.get(i);
				total+=top[i];
			}
			weights.clear();
			for(int i = 0; i < top.length; i++) {
				weights.add(Math.min(top[i]/total,1));
			}
			while(ids.size()>max) {
				ids.remove(ids.size()-1);
			}
		} else {
			while(ids.size()<max) {
				ids.add(0);
				weights.add(0d);
			}
		}
	}
}