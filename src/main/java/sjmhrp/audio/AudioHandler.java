package sjmhrp.audio;

import java.util.ArrayList;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.util.WaveData;

import sjmhrp.io.Log;
import sjmhrp.render.view.Camera;

public class AudioHandler {

	static String RES_LOC = "/res/audio/";
	static ArrayList<Integer> buffers = new ArrayList<Integer>();
	static ArrayList<AudioSource> sources = new ArrayList<AudioSource>();
	
	public static void init() {
		try {
			AL.create();
		} catch(Exception e) {
			Log.printError(e);
		}
	}

	public static void addSource(AudioSource source) {
		sources.add(source);
	}
	
	public static void removeSource(AudioSource source) {
		sources.remove(source);
	}
	
	public static void update(Camera c) {
		setListenerData(c);
	}
	
	static void setListenerData(Camera c) {
		AL10.alListener3f(AL10.AL_POSITION,(float)c.getPosition().x,(float)c.getPosition().y,(float)c.getPosition().z);
		AL10.alListener3f(AL10.AL_VELOCITY,(float)c.getVelocity().x,(float)c.getVelocity().y,(float)c.getVelocity().z);
	}

	public static void setAttenuationModel(int model) {
		AL10.alDistanceModel(model);
	}
	
	public static int loadSound(String file) {
		int buffer = AL10.alGenBuffers();
		buffers.add(buffer);
		WaveData waveFile = WaveData.create(Class.class.getResourceAsStream(RES_LOC+file+".wav"));
		AL10.alBufferData(buffer,waveFile.format,waveFile.data,waveFile.samplerate);
		waveFile.dispose();
		return buffer;
	}

	public static void cleanUp() {
		for(Integer i : buffers) {
			AL10.alDeleteBuffers(i);
		}
		AL.destroy();
	}
}