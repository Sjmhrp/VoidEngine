package sjmhrp.entity;

import sjmhrp.linear.Matrix4d;
import sjmhrp.linear.Vector2d;
import sjmhrp.shaders.MultiTextureShaderProgram;
import sjmhrp.shaders.ShaderProgram;

public class EntityShader extends ShaderProgram implements MultiTextureShaderProgram {

	private int location_transformMatrix;
	private int location_viewMatrix;
	private int location_reflectivity;
	private int location_fakeLighting;
	private int location_offset;
	private int location_rows;
	private int location_texture;
	private int location_normals;
	private int location_specular;
	private int location_hasNormals;
	private int location_hasSpecular;
	
	public EntityShader() {
		super("entity/Entity","entity/Entity");
	}

	@Override
	protected void bind() {
		bindAttribute(0,"position");
		bindAttribute(1,"texturePos");
		bindAttribute(2,"normal");
	}

	@Override
	protected void getAllUniformLocations() {
		location_transformMatrix = getUniformLocation("transformMatrix");
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_reflectivity = getUniformLocation("reflectivity");
		location_fakeLighting = getUniformLocation("fakeLighting");
		location_offset = getUniformLocation("offset");
		location_rows = getUniformLocation("rows");
		location_texture = getUniformLocation("textureSampler");
		location_normals = getUniformLocation("normals");
		location_specular = getUniformLocation("specular");
		location_hasNormals = getUniformLocation("hasNormals");
		location_hasSpecular = getUniformLocation("hasSpecular");
	}

	public void loadTransformMatrix(Matrix4d matrix) {
		load4Matrix(location_transformMatrix, matrix);
	}

	public void loadViewMatrix(Matrix4d matrix) {
		load4Matrix(location_viewMatrix, matrix);
	}

	public void loadReflect(double r) {
		loadFloat(location_reflectivity, r);
	}

	public void loadFakeLighting(boolean f) {
		loadBoolean(location_fakeLighting, f);
	}

	public void loadRows(double rows) {
		loadFloat(location_rows,rows);
	}

	public void loadOffset(double x, double y) {
		load2Vector(location_offset,new Vector2d(x,y));
	}

	public void loadNormals(boolean b) {
		loadBoolean(location_hasNormals,b);
	}

	public void loadSpecular(boolean b) {
		loadBoolean(location_hasSpecular,b);
	}

	public void connectTextures() {
		loadInt(location_texture, 0);
		loadInt(location_normals, 1);
		loadInt(location_specular, 2);
	}
}