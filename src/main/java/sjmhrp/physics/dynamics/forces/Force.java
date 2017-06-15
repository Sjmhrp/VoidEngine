package sjmhrp.physics.dynamics.forces;

import sjmhrp.world.World;

public abstract class Force {

	World world;
	
	public abstract boolean update();
	
	public abstract void applyForce();
	
	public void setWorld(World w) {
		world=w;
	}
}