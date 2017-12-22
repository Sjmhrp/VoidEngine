package sjmhrp.world.terrain.generator;

import sjmhrp.physics.collision.broadphase.Tree;
import sjmhrp.utils.linear.Vector3d;

public class CompoundTerrainGenerator extends TerrainGenerator {

	private static final long serialVersionUID = -4169587396296103321L;

	TerrainGenerator gen;
	Tree<TerrainShape> exceptions;
	
	public CompoundTerrainGenerator(TerrainGenerator gen) {
		this.gen=gen;
		exceptions = new Tree<TerrainShape>();
	}

	@Override
	public double getDensity(double x, double y, double z) {
		double d = gen.getDensity(x,y,z);
		for(TerrainShape s : exceptions.query(new Vector3d(x,y,z))) {
			if(s.positive) {
				d=Math.min(d,s.getDensity(x,y,z));
			} else {
				d=Math.max(d,-s.getDensity(x,y,z));
			}
		}
		return d;
	}
	
	public CompoundTerrainGenerator place(TerrainShape s) {
		exceptions.add(s.getBounds(),s);
		return this;
	}
	
	@Override
	public void reload() {
		gen.reload();
	}
}