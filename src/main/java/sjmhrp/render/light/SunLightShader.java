package sjmhrp.render.light;

import sjmhrp.render.shader.MultiTextureShader;
import sjmhrp.render.shader.ShaderProgram;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.world.sky.CelestialBody;
import sjmhrp.world.sky.SkyDome;

public class SunLightShader extends ShaderProgram implements MultiTextureShader {

	private int location_viewMatrix;
	private int location_albedo;
	private int location_normal;
	private int location_mask;
	private int location_depth;
	private int location_pos;
	private int location_colour;
	
	public SunLightShader() {
		super("render/post/Generic","render/light/SunLight");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_albedo = getUniformLocation("albedo");
		location_normal = getUniformLocation("normalMap");
		location_mask = getUniformLocation("mask");
		location_depth = getUniformLocation("depth");
		location_pos = getUniformLocation("position");
		location_colour = getUniformLocation("colour");
	}

	@Override
	protected void bind() {
		bindAttribute(0,"position");
	}

	public void loadViewMatrix(Matrix4d matrix) {
		load4Matrix(location_viewMatrix,matrix);
	}
	
	public void load(SkyDome sky, CelestialBody body) {
		load3Vector(location_pos,body.getPosition().getUnit().scale(sky.getSize()));
		load3Vector(location_colour,body.getColour());
	}
	
	public void connectTextures() {
		loadInt(location_albedo,0);
		loadInt(location_normal,1);
		loadInt(location_mask,2);
		loadInt(location_depth,3);
	}
}