package sjmhrp.view;

import org.lwjgl.opengl.Display;

import sjmhrp.linear.Matrix4d;

public class Frustum {

	public static final double NEAR_PLANE = 0.3f;
	public static final double FAR_PLANE = 1000;
	public static final double fov = 90;

	static Matrix4d projectionMatrix;
	
	public static void init() {
		double aspectRatio = (double)Display.getWidth()/(double)Display.getHeight();
		double y_scale = (double) ((1f / Math.tan(Math.toRadians(fov / 2f))) * aspectRatio);
		double x_scale = y_scale / aspectRatio;
		double frustum_length = FAR_PLANE - NEAR_PLANE;
		projectionMatrix = new Matrix4d();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m32 = -1;
		projectionMatrix.m33 = 0;
	}

	public static Matrix4d getProjectionMatrix() {
		return projectionMatrix;
	}
}