package sjmhrp.render.post;

import org.lwjgl.opengl.Display;

import sjmhrp.render.shader.MultiTextureShader;
import sjmhrp.render.shader.ShaderProgram;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Vector2d;
import sjmhrp.utils.linear.Vector3d;

public class SSAOShader extends ShaderProgram implements MultiTextureShader {

	private int location_viewMatrix;
	private int location_depth;
	private int location_normal;
	private int location_noise;
	private int location_resolution;
	private int[] location_samples;
	
	public SSAOShader() {
		super("render/post/Generic","render/post/SSAO");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_depth = getUniformLocation("depth");
		location_normal = getUniformLocation("normalMap");
		location_noise = getUniformLocation("noise");
		location_resolution = getUniformLocation("resolution");
		location_samples = new int[64];
		for(int i = 0; i < location_samples.length; i++) {
			location_samples[i] = getUniformLocation("samples["+i+"]");
		}
	}

	@Override
	protected void bind() {
		bindAttribute(0, "position");
	}

	@Override
	public void start() {
		super.start();
		load2Vector(location_resolution, new Vector2d(Display.getWidth(),Display.getHeight()));
	}

	public void loadViewMatrix(Matrix4d m) {
		load4Matrix(location_viewMatrix,m);
	}

	public void loadSamples(Vector3d[] samples) {
		if(samples.length==64) {
			for(int i = 0; i < 64; i++) {
				load3Vector(location_samples[i],samples[i]);
			}
		}
	}

	public void connectTextures() {
		loadInt(location_depth,0);
		loadInt(location_normal,1);
		loadInt(location_noise,2);
	}
}