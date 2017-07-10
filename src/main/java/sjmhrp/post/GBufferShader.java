package sjmhrp.post;

import sjmhrp.shaders.ShaderProgram;

public class GBufferShader extends ShaderProgram {

	private static final String VERTEX_FILE = "/sjmhrp/post/GenericVertexShader.glsl";
	private static final String FRAGMENT_FILE = "/sjmhrp/post/GBufferFragmentShader.glsl";

	private int location_albedo;
	private int location_light;
	private int location_ssao;
	private int location_depth;
	
	public GBufferShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void getAllUniformLocations() {
		location_albedo = getUniformLocation("albedo");
		location_light = getUniformLocation("light");
		location_ssao = getUniformLocation("ssao");
		location_depth = getUniformLocation("depth");
	}

	@Override
	protected void bind() {
		bindAttribute(0, "position");
	}

	public void connectTextures() {
		loadInt(location_albedo,0);
		loadInt(location_light,1);
		loadInt(location_ssao,2);
		loadInt(location_depth,3);
	}
}