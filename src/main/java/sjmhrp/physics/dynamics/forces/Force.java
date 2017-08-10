package sjmhrp.physics.dynamics.forces;

import java.io.Serializable;

import sjmhrp.world.World;

public abstract class Force implements Serializable {
	
	private static final long serialVersionUID = 3248502653306635194L;
	
	World world;
	
	public abstract boolean update();
	
	public abstract void applyForce();
	
	public void setWorld(World w) {
		world=w;
	}
}