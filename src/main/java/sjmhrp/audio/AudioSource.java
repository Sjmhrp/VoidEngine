package sjmhrp.audio;

import org.lwjgl.openal.AL10;

import sjmhrp.utils.linear.Vector3d;

public class AudioSource {

	private int sourceId;
	private Vector3d position;
	private Vector3d velocity;
	private float gain;
	private float pitch;
	private boolean looping;
	private boolean active = true;
	private int buffer;
	
	public AudioSource(Vector3d position) {
		this(position,new Vector3d());
	}
	
	public AudioSource(Vector3d pos, Vector3d vel) {
		this(pos,vel,1,1);
	}
	
	public AudioSource(Vector3d pos, Vector3d velocity, float gain, float pitch) {
		this.position=pos;
		this.velocity=velocity;
		this.gain=gain;
		this.pitch=pitch;
		sourceId = AL10.alGenSources();
		AL10.alSourcef(sourceId,AL10.AL_GAIN,gain);
		AL10.alSourcef(sourceId,AL10.AL_PITCH,pitch);
		AL10.alSource3f(sourceId,AL10.AL_POSITION,(float)position.x,(float)position.y,(float)position.z);
		AL10.alSource3f(sourceId,AL10.AL_VELOCITY,(float)velocity.x,(float)velocity.y,(float)velocity.z);
		AudioHandler.addSource(this);
	}
	
	public AudioSource setPosition(Vector3d pos) {
		this.position=pos;
		AL10.alSource3f(sourceId,AL10.AL_POSITION,(float)position.x,(float)position.y,(float)position.z);
		return this;
	}
	
	public AudioSource setVelocity(Vector3d vel) {
		this.velocity=vel;
		AL10.alSource3f(sourceId,AL10.AL_VELOCITY,(float)velocity.x,(float)velocity.y,(float)velocity.z);
		return this;
	}
	
	public AudioSource setLooping(boolean b) {
		looping=b;
		AL10.alSourcei(sourceId,AL10.AL_LOOPING,b?1:0);
		return this;
	}
	
	public AudioSource setVolume(float gain) {
		this.gain=gain;
		AL10.alSourcef(sourceId,AL10.AL_GAIN,gain);
		return this;
	}
	
	public AudioSource setPitch(float pitch) {
		this.pitch=pitch;
		AL10.alSourcef(sourceId,AL10.AL_PITCH,pitch);
		return this;
	}
	
	public AudioSource setBuffer(int buffer) {
		this.buffer=buffer;
		return this;
	}
	
	public AudioSource setActive(boolean b) {
		active=b;
		return this;
	}
	
	public AudioSource setRollOff(float rollOff) {
		AL10.alSourcef(sourceId,AL10.AL_ROLLOFF_FACTOR,rollOff);
		return this;
	}
	
	public AudioSource setReferenceDistance(float distance) {
		AL10.alSourcef(sourceId,AL10.AL_REFERENCE_DISTANCE,distance);
		return this;
	}
	
	public AudioSource setMaxDistance(float distance) {
		AL10.alSourcef(sourceId,AL10.AL_MAX_DISTANCE,distance);
		return this;
	}
	
	public AudioSource setAttenuation(float rollOff, float distance) {
		setRollOff(rollOff);
		setReferenceDistance(distance);
		return this;
	}
	
	public AudioSource setAttenuation(float rollOff, float ref, float max) {
		setRollOff(rollOff);
		setReferenceDistance(ref);
		setMaxDistance(max);
		return this;
	}
	
	public AudioSource removeAttenutation() {
		removeDoppler();
		setRollOff(0);
		return this;
	}

	public AudioSource setDopplerFactor(float f) {
		AL10.alSourcef(sourceId,AL10.AL_DOPPLER_FACTOR,f);
		return this;
	}
	
	public AudioSource removeDoppler() {
		return setDopplerFactor(0);
	}
	
	public void play() {
		stop();
		AL10.alSourcei(sourceId,AL10.AL_BUFFER,buffer);
		AL10.alSourcePlay(sourceId);
	}
	
	public void pause() {
		AL10.alSourcePause(sourceId);
	}
	
	public void continuePlaying() {
		AL10.alSourcePlay(sourceId);
	}
	
	public void stop() {
		AL10.alSourceStop(sourceId);
	}
	
	public Vector3d getPosition() {
		return position;
	}
	
	public Vector3d getVelocity() {
		return velocity;
	}
	
	public boolean isLooping() {
		return looping;
	}
	
	public float getGain() {
		return gain;
	}
	
	public float getPitch() {
		return pitch;
	}
	
	public boolean isPlaying() {
		return AL10.alGetSourcef(sourceId,AL10.AL_SOURCE_STATE)==AL10.AL_PLAYING;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void delete() {
		stop();
		AL10.alDeleteSources(sourceId);
		AudioHandler.removeSource(this);
	}
}