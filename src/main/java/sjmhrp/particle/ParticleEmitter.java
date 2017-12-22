package sjmhrp.particle;

import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.floor;
import static java.lang.Math.random;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.util.Random;

import sjmhrp.core.Globals;
import sjmhrp.event.TickListener;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.render.textures.ParticleTexture;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.utils.linear.Vector4d;

public class ParticleEmitter implements TickListener{

	double rate,avgSpeed,avgTTL,avgScale;
	double speedError,lifeError,scaleError,directionError;
	boolean isActive = true;
	Vector3d centre;
	Vector3d direction;
	Vector3d gravity;
	ParticleTexture texture;
	
	Random random = new Random();
	
	{PhysicsEngine.addTickListener(this);}
	
	public ParticleEmitter(Vector3d centre, double rate, double speed, double ttl, double scale, ParticleTexture texture) {
		this.centre=centre;
		this.rate=rate;
		avgSpeed=speed;
		avgTTL=ttl;
		avgScale=scale;
		this.texture=texture;
	}
	
	public ParticleEmitter setRate(double rate) {
		this.rate=rate;
		return this;
	}
	
	public ParticleEmitter setDirection(Vector3d dir, double error) {
		direction=dir;
		directionError=error*PI;
		return this;
	}
	
	public ParticleEmitter setSpeedError(double error) {
		speedError=error;
		return this;
	}
	
	public ParticleEmitter setLifeError(double error) {
		lifeError=error;
		return this;
	}
	
	public ParticleEmitter setScaleError(double error) {
		scaleError=error;
		return this;
	}
	
	public ParticleEmitter setActive(boolean b) {
		isActive=b;
		return this;
	}
	
	public double getAvgSpeed() {
		return avgSpeed;
	}

	public ParticleEmitter setAvgSpeed(double avgSpeed) {
		this.avgSpeed = avgSpeed;
		return this;
	}

	public double getAvgTTL() {
		return avgTTL;
	}

	public ParticleEmitter setAvgTTL(double avgTTL) {
		this.avgTTL = avgTTL;
		return this;
	}

	public double getAvgScale() {
		return avgScale;
	}

	public ParticleEmitter setAvgScale(double avgScale) {
		this.avgScale = avgScale;
		return this;
	}

	public Vector3d getGravity() {
		return gravity;
	}
	
	public ParticleEmitter setGravity(Vector3d gravity) {
		this.gravity=gravity;
		return this;
	}
	
	public Vector3d getCentre() {
		return centre;
	}

	public ParticleEmitter setCentre(Vector3d centre) {
		this.centre = centre;
		return this;
	}

	public boolean isActive() {
		return isActive;
	}
	
	@Override
	public void tick() {
		if(PhysicsEngine.isPaused()||!isActive)return;
		double particles = rate*PhysicsEngine.getTimeStep();
		double fract = particles%1;
		for(int i = 0; i < floor(particles); i++)emitParticle();
		if(random()<fract)emitParticle();
	}
	
	protected void emitParticle() {
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
		new Particle(new Vector3d(centre),vel,Globals.PARTICLE_MASS,ttl,texture,scale).setGravity(gravity);
	}
	
	protected double genValue(double avg, double error) {
		return avg+(random.nextDouble()-0.5)*2*error;
	}
	
	protected Vector3d genWithinCone(Vector3d dir, double angle) {
		double cosAngle = cos(angle);
		double theta = random.nextDouble()*2*PI;
		double z = cosAngle+random.nextDouble()*(1-cosAngle);
		double p = sqrt(1-z*z);
		double x = p*cos(theta);
		double y = p*sin(theta);	 
		Vector4d direction = new Vector4d(x,y,z,1);
		if (dir.x != 0 || dir.y != 0 || (dir.z != 1 && dir.z != -1)) {
			Vector3d rotateAxis = new Vector3d(dir.y,-dir.x,0).normalize();
			double rotateAngle = acos(dir.z);
			Matrix4d rotationMatrix = new Matrix4d().setIdentity();
			rotationMatrix.rotate(-rotateAngle,rotateAxis);
			rotationMatrix.transform(direction);
		} else if (dir.z == -1) {
			direction.z*=-1;
		}
		return new Vector3d(direction);
	}
	
	protected Vector3d genRandom() {
		double theta = random.nextDouble()*2*PI;
		double z = random.nextDouble()*2-1;
		double p = sqrt(1-z*z);
		double x = p*cos(theta);
		double y = p*sin(theta);
		return new Vector3d(x,y,z);
	}

	@Override
	public boolean keepOnLoad() {
		return false;
	}
}