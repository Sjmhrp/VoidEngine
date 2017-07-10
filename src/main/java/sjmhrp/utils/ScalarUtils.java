package sjmhrp.utils;

import sjmhrp.linear.Vector3d;

public class ScalarUtils {

	public static double clamp(double x, double min, double max) {
		return Math.max(min,Math.min(max,x));
	}
	
	public static int clamp(int x, int min, int max) {
		return Math.max(min,Math.min(max,x));
	}
	
	public static double lerp(double a, double b, double f) {
		return a+f*(b-a);
	}
	
	public static Vector3d bvToRGB(double bv) {
		double t,r=0,g=0,b=0;
		if(bv<-0.4)bv=-0.4;
		if(bv>2)bv=2;
		if(bv<0) {
			t = (bv+0.4)*2.5;
			r = 0.61+0.11*t+0.1*t*t;
			g = 0.7+0.07*t+0.1*t*t;
		} else if(bv<0.4) {
			t = bv*2.5;
			r = 0.83+(0.17*t);
			g = 0.87+(0.11*t);
			b = 1;
		} else if (bv<1.6) {
			t = (bv-0.4)/1.2;
			r = 1;
			g = 0.98-0.16*t;
		} else {
			t = (bv-1.6)*2.5;
			r = 1;
			g = 0.82-0.5*t*t;
		}
		if(bv>=0.4&&bv<1.5) {
			t = (bv-0.4)/1.1;
			b = 1-0.47*t+0.1*t*t;
		} else if(bv>=1.5&&bv<1.951) {
			t = (bv-1.5)/0.44;
			b = 0.63-0.6*t*t;
		} else {
			b=0;
		}
		return new Vector3d(r,g,b);
	}
}