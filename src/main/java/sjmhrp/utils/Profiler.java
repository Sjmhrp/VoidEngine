package sjmhrp.utils;

import java.util.HashMap;
import java.util.Map.Entry;

import sjmhrp.io.Log;

public class Profiler {

	public static int drawCalls;

	static long currentTime;
	static HashMap<String,Long> timeSteps;

	public static void start() {
		currentTime = System.nanoTime();
		timeSteps = new HashMap<String,Long>();
	}

	public static void record(String s) {
		long time=System.nanoTime(); 
		timeSteps.put(s,time-currentTime);
		currentTime=time;
	}

	public static void print() {
		if(timeSteps==null)return;
		for(Entry<String,Long> e : timeSteps.entrySet()) {
			Log.println(e.getKey()+": "+e.getValue());
		}
		timeSteps.clear();
	}
}