package sjmhrp.core;

import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Vector3d;

public class Globals {

	public static final double GRAVITATIONAL = 6.67e-11;
	public static final double EARTH_MASS = 5.972e24;
	public static final double EARTH_RADIUS = 6371e3;
	
	public static final int MAX_REWIND_FRAMES = 3600;
	public static final double REWIND_SPEED = 1.5;
	public static final double WORLD_SIZE = 1000000000;
	public static final double EPSILON = 0;
	public static final double BAUMGARTE = 0.2;
	public static final double MARGIN = 0.04;
	public static final double LINEARSLOP = 0.1;
	public static final double RESTITUTIONSLOP = 0.3;
    public static final double REL_ERROR = 1e-3;
    public static final double REL_ERROR_SQUARE = REL_ERROR * REL_ERROR;
    public static final double PERSISTENT_THRESHOLD_SQUARED = 0.04;
	public static final int IMPULSE_ITERATIONS = 32;
	public static final int POSITION_ITERATIONS = 10;
	public static final double JOINT_BREAKING_STRENGTH = 1;
	
	public static final double PARTICLE_MASS = 1;

	public static final double MAX_TERRAIN_HEIGHT = 40;
	
	public static boolean accumulateImpulse = true;
	public static boolean warmstart = true;
	public static boolean positionCorrection = true;
	
	public static final Matrix4d BLENDER_CORRECTION = new Matrix4d().setIdentity().rotate(-Math.PI/2,new Vector3d(1,0,0));
}