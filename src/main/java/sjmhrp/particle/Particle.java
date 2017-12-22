package sjmhrp.particle;

import java.util.ArrayList;
import java.util.Iterator;

import sjmhrp.event.TickListener;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.physics.dynamics.forces.Force;
import sjmhrp.render.RenderHandler;
import sjmhrp.render.RenderRegistry;
import sjmhrp.render.textures.ParticleTexture;
import sjmhrp.render.textures.TexturePool;
import sjmhrp.utils.linear.Vector2d;
import sjmhrp.utils.linear.Vector3d;

public class Particle implements TickListener {

	Vector3d position;
	Vector3d velocity;
	double invmass = 1;
	double lifeLength;
	double ttl;
	final Vector3d totalForce = new Vector3d();
	final Vector3d gravity = new Vector3d();
	ArrayList<Force> forces = new ArrayList<Force>();
	
	ParticleTexture texture;
	double scale;
	
	Vector3d prevVelocity;
	
	{PhysicsEngine.addTickListener(this);}
	
	public Particle(Vector3d position, double ttl, String texture, int rows) {
		this(position,new Vector3d(),ttl,texture,rows,1);
	}
	
	public Particle(Vector3d position, Vector3d velocity, double ttl, String texture, int rows) {
		this(position,velocity,ttl,texture,rows,1);
	}
	
	public Particle(Vector3d position, Vector3d velocity, double ttl, String texture, int rows, double scale) {
		this(position,velocity,1,ttl,TexturePool.getParticleTexture(texture,rows),scale);
	}
	
	public Particle(Vector3d position, Vector3d velocity, double mass, double ttl, ParticleTexture texture, double scale) {
		this.position=position;
		this.velocity=velocity;
		this.invmass=mass==0?0:1d/mass;
		lifeLength=ttl;
		this.ttl=ttl;
		this.texture=texture;
		this.scale=scale;
		RenderHandler.addTask(()->RenderRegistry.registerParticle(this));
	}
	
	public Particle setGravity(Vector3d g) {
		gravity.set(g);
		return this;
	}
	
	public Vector3d getGravity() {
		return gravity;
	}
	
	public Particle addForce(Force f) {
		forces.add(f);
		return this;
	}
	
	public Vector3d getPosition() {
		return position;
	}

	public Vector3d getVelocity() {
		return velocity;
	}

	public double getTtl() {
		return ttl;
	}

	public double getLifeLength() {
		return lifeLength;
	}
	
	public double getAge() {
		return 1-ttl/lifeLength;
	}
	
	public Vector3d getTotalForce() {
		return totalForce;
	}

	public ParticleTexture getTexture() {
		return texture;
	}

	public double getScale() {
		return scale;
	}
	
	public double getInvMass() {
		return invmass;
	}

	public void setInvMass(double mass) {
		this.invmass = mass;
	}
	
	public double getTextureXOffset(int i) {
		int col = i%getTexture().getRows();
		return (double)col/getTexture().getRows();
	}
	
	public double getTextureYOffset(int i) {
		int row = i/getTexture().getRows();
		return (double)row/getTexture().getRows();
	}

	public Vector2d getTextureOffset(int i) {
		return new Vector2d(getTextureXOffset(i),getTextureYOffset(i));
	}
	
	@Override
	public void tick() {
		if(ttl<=0)PhysicsEngine.removeTickListener(this);
		if(PhysicsEngine.isPaused()||ttl<=0)return;
		ttl-=PhysicsEngine.getTimeStep();
		if(gravity.lengthSquared()==0&&forces.size()==0)return;
		totalForce.add(gravity);
		Iterator<Force> i = forces.iterator();
		while(i.hasNext()) {
			Force f = i.next();
			if(f.update()) {
				f.applyForce();
			} else {
				i.remove();
			}
		}
		prevVelocity = new Vector3d(velocity);
		velocity.add(totalForce.scale(PhysicsEngine.getTimeStep()));
		position.add(prevVelocity.add(velocity).scale(0.5*PhysicsEngine.getTimeStep()));
		totalForce.zero();
	}

	@Override
	public boolean keepOnLoad() {
		return false;
	}
}