package sjmhrp.render.view;

import org.lwjgl.opengl.Display;

import sjmhrp.physics.collision.broadphase.AABB;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.utils.linear.Vector4d;

public class Frustum {

	public static final double NEAR_PLANE = 0.3;
	public static final double FAR_PLANE = 1000;
	public static final double fov = 90;

	static Matrix4d projectionMatrix;
	static Vector4d[] planes = new Vector4d[6];
	
	public static void init() {
		double aspectRatio = Display.getWidth()/Display.getHeight();
		double y_scale = ((1d/Math.tan(Math.toRadians(fov/2d)))*aspectRatio);
		double x_scale = y_scale/aspectRatio;
		double frustum_length = FAR_PLANE-NEAR_PLANE;
		projectionMatrix = new Matrix4d();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE+NEAR_PLANE)/frustum_length);
		projectionMatrix.m23 = -2*NEAR_PLANE*FAR_PLANE/frustum_length;
		projectionMatrix.m32 = -1;
		for(int i=0;i<planes.length;i++)planes[i]=new Vector4d();
	}

	public static Matrix4d getProjectionMatrix() {
		return projectionMatrix;
	}
	
	public static void updatePlanes(Camera c) {
		Matrix4d m = Matrix4d.mul(projectionMatrix,c.getViewMatrix());
		planes[0]=new Vector4d(m.m30+m.m00,m.m31+m.m01,m.m32+m.m02,m.m33+m.m03);
		planes[1]=new Vector4d(m.m30-m.m00,m.m31-m.m01,m.m32-m.m02,m.m33-m.m03);
		planes[2]=new Vector4d(m.m30+m.m10,m.m31+m.m11,m.m32+m.m12,m.m33+m.m13);
		planes[3]=new Vector4d(m.m30-m.m10,m.m31-m.m11,m.m32-m.m12,m.m33-m.m13);
		planes[4]=new Vector4d(m.m30+m.m20,m.m31+m.m21,m.m32+m.m22,m.m33+m.m23);
		planes[5]=new Vector4d(m.m30-m.m20,m.m31-m.m21,m.m32-m.m22,m.m33-m.m23);
	}
	
	public static boolean isVisible(AABB b) {
		if(b==null)return false;
		Vector3d[] box = {b.getMin(),b.getMax()};
		for(int i = 0; i < planes.length; i++) {
			Vector4d p = planes[i];
			int px = p.x>0?1:0;
			int py = p.y>0?1:0;
			int pz = p.z>0?1:0;
			double dp = p.x*box[px].x+p.y*box[py].y+p.z*box[pz].z;
			if(dp<-p.w)return false;
		}
		return true;
	}
}