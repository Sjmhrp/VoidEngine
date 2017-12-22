package sjmhrp.particle;

import static java.lang.Math.floor;
import static java.lang.Math.random;

import sjmhrp.core.Globals;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.render.textures.ParticleTexture;
import sjmhrp.render.textures.TexturePool;
import sjmhrp.utils.VectorUtils;
import sjmhrp.utils.linear.Vector3d;

public class Trail extends ParticleEmitter {

	Vector3d offset = new Vector3d();
	Vector3d prevPos = new Vector3d();
	
	public Trail(Vector3d centre, double ttl, Vector3d colour) {
		this(centre,ttl,colour,new Vector3d());
	}
	
	public Trail(Vector3d centre, double ttl, Vector3d colour, Vector3d offset) {
		super(centre,600,0,ttl,0.1,new ParticleTexture(TexturePool.getColour(colour).getAlbedoID()));
		setGravity(new Vector3d());
		this.offset.set(offset);
		prevPos.set(centre);
	}

	@Override
	public void tick() {
		if(PhysicsEngine.isPaused()||!isActive)return;
		double particles = rate*PhysicsEngine.getTimeStep();
		double fract = particles%1;
		double n = 0;
		for(int i = 0; i < floor(particles); i++)emitParticle(n++);
		if(random()<fract)emitParticle(n++);
		prevPos.set(centre);
	}

	protected void emitParticle(double n) {
		Vector3d vel = null;
		if(direction==null) {
			vel=genRandom();
		} else {
			vel=genWithinCone(direction,directionError);
		}
		if(vel.lengthSquared()!=0)vel.normalize();
		vel.scale(genValue(avgSpeed,speedError));
		double scale = genValue(avgScale,scaleError);
		double ttl = genValue(avgTTL,lifeError);
		new Particle(VectorUtils.lerp(prevPos,centre,n/rate/PhysicsEngine.getTimeStep()).add(offset),vel,Globals.PARTICLE_MASS,ttl,texture,scale) {
			@Override
			public double getAge() {
				return 0;
			}
		}.setGravity(gravity);
	}
}
