package sjmhrp.utils;

public class ScalarUtils {

	public static double clamp(double x, double min, double max) {
		return Math.max(min,Math.min(max,x));
	}
	
	public static double lerp(double a, double b, double f) {
		return a+f*(b-a);
	}
}