package sjmhrp.factory;

import sjmhrp.particle.ParticleEmitter;
import sjmhrp.utils.linear.Vector3d;

public class ParticleEmitterFactory extends Factory<ParticleEmitter> {

	private static final long serialVersionUID = 3299600162697689076L;

	double rate = 1;
	double avgSpeed,avgTTL,avgScale;
	double speedError,lifeError,scaleError,directionError;
	boolean isActive = true;
	Vector3d centre = new Vector3d();
	Vector3d direction = new Vector3d();
	boolean hasDir = false;
	Vector3d gravity = new Vector3d();
	ParticleTextureFactory texture = new ParticleTextureFactory();
	
	public ParticleEmitterFactory(Vector3d centre) {
		this.centre=centre;
	}
	
	public ParticleEmitterFactory setRate(double rate) {
		this.rate=rate;
		return this;
	}
	
	public ParticleEmitterFactory setSpeed(double avgSpeed) {
		this.avgSpeed=avgSpeed;
		return this;
	}
	
	public ParticleEmitterFactory setTTL(double avgTTL) {
		this.avgTTL=avgTTL;
		return this;
	}
	
	public ParticleEmitterFactory setSpeedError(double speedError) {
		this.speedError=speedError;
		return this;
	}
	
	public ParticleEmitterFactory setLifeError(double lifeError) {
		this.lifeError=lifeError;
		return this;
	}
	
	public ParticleEmitterFactory setScaleError(double scaleError) {
		this.scaleError=scaleError;
		return this;
	}
	
	public ParticleEmitterFactory setDirectionError(double directionError) {
		this.directionError=directionError;
		return this;
	}
	
	public ParticleEmitterFactory setActive(boolean active) {
		isActive=active;
		return this;
	}
	
	public ParticleEmitterFactory setDirection(Vector3d direction) {
		this.direction=direction;
		hasDir=true;
		return this;
	}
	
	public ParticleEmitterFactory setGravity(Vector3d gravity) {
		this.gravity=gravity;
		return this;
	}
	
	public ParticleEmitterFactory setRows(int rows) {
		this.texture.setRows(rows);
		return this;
	}
	
	public ParticleEmitterFactory setTexture(String texture) {
		this.texture.setTexture(texture);
		return this;
	}
	
	public ParticleEmitterFactory setColour(String colour) {
		this.texture.setColour(colour);
		return this;
	}
	
	public ParticleEmitterFactory setColour(Vector3d colour) {
		this.texture.setColour(colour);
		return this;
	}
	
	@Override
	public ParticleEmitter build() {
		ParticleEmitter p = new ParticleEmitter(centre,rate,avgSpeed,avgTTL,avgScale,texture.build());
		if(hasDir)p.setDirection(direction,directionError);
		p.setSpeedError(speedError);
		p.setLifeError(lifeError);
		p.setScaleError(scaleError);
		p.setActive(isActive);
		p.setGravity(gravity);
		return p;
	}
}