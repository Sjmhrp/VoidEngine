package sjmhrp.shaders;

public class PostShaderProgram extends ShaderProgram {
	
	public PostShaderProgram(String v, String f) {
		super(v,f);
	}

	@Override
	protected void getAllUniformLocations() {
	}

	@Override
	protected void bind() {
		bindAttribute(0,"position");
	}
}