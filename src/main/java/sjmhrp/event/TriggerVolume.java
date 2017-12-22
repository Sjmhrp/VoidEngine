package sjmhrp.event;

import sjmhrp.physics.PhysicsEngine;
import sjmhrp.physics.collision.broadphase.AABB;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.utils.GeometryUtils;
import sjmhrp.world.World;

public abstract class TriggerVolume implements TickListener {

	AABB box;
	World world;

	public TriggerVolume(AABB box, World world) {
		this.box=box;
		this.world=world;
		PhysicsEngine.addTickListener(this);
	}
	
	@Override
	public void tick() {
		for(CollisionBody b : world.getCollisionBodies()) {
			if(GeometryUtils.intersects(b.getBoundingBox(),box))trigger(b);
		}
	}
	
	public abstract void trigger(CollisionBody source);
}