package sjmhrp.shaders;

public class PostShaderProgram extends ShaderProgram {

	static final String RES_LOC = "/sjmhrp/post/";
	
	public PostShaderProgram(String v, String f) {
		super(RES_LOC+v+"VertexShader.glsl",RES_LOC+f+"FragmentShader.glsl");
	}

	@Override
	protected void getAllUniformLocations() {
	}

	@Override
	protected void bind() {
		bindAttribute(0,"position");
	}
}