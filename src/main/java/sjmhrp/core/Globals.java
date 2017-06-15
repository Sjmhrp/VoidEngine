package sjmhrp.core;

public class Globals {

	public static final int MAX_REWIND_FRAMES = 3600;
	public static final double EPSILON = 0;
	public static final double BAUMGARTE = 0.1;
	public static final double MARGIN = 0.04;
	public static final double LINEARSLOP = 0.1;
	public static final double RESTITUTIONSLOP = 0.3;
    public static final double REL_ERROR = 1e-3;
    public static final double REL_ERROR_SQUARE = REL_ERROR * REL_ERROR;
    public static final double PERSISTENT_THRESHOLD_SQUARED = 0.04;
	public static final int IMPULSE_ITERATIONS = 32;
	public static final int POSITION_ITERATIONS = 10;

	public static boolean debug = false;
	public static boolean accumulateImpulse = true;
	public static boolean warmstart = true;
	public static boolean positionCorrection = true;
}