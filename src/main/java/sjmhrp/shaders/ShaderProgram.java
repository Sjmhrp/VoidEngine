package sjmhrp.shaders;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glValidateProgram;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import sjmhrp.io.Log;
import sjmhrp.linear.Matrix4d;
import sjmhrp.linear.Vector2d;
import sjmhrp.linear.Vector3d;
import sjmhrp.linear.Vector4d;

public abstract class ShaderProgram {

	private int programID;
	private int vertexShaderID;
	private int geometryShaderID;
	private int fragmentShaderID;

	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

	protected int location_projectionMatrix;

	public ShaderProgram(String v, String f) {
		vertexShaderID = loadShader(v, GL_VERTEX_SHADER);
		geometryShaderID = 0;
		fragmentShaderID = loadShader(f, GL_FRAGMENT_SHADER);
		programID = glCreateProgram();
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, fragmentShaderID);
		bind();
		glLinkProgram(programID);
		glValidateProgram(programID);
		getAllUniformLocations();
	}
	
	public ShaderProgram(String v, String g, String f) {
		vertexShaderID = loadShader(v, GL_VERTEX_SHADER);
		geometryShaderID = loadShader(g, GL32.GL_GEOMETRY_SHADER);
		fragmentShaderID = loadShader(f, GL_FRAGMENT_SHADER);
		programID = glCreateProgram();
		glAttachShader(programID, vertexShaderID);
		glAttachShader(programID, geometryShaderID);
		glAttachShader(programID, fragmentShaderID);
		bind();
		glLinkProgram(programID);
		glValidateProgram(programID);
		getAllUniformLocations();
	}
	
	protected abstract void getAllUniformLocations();
	
	protected int getUniformLocation(String name) {
		return GL20.glGetUniformLocation(programID, name);
	}
	
	protected void loadFloat(int location, double value) {
		GL20.glUniform1f(location,(float)value);
	}
	
	protected void loadInt(int location, int value) {
		GL20.glUniform1i(location, value);
	}
	
	protected void load2Vector(int location, Vector2d value) {
		GL20.glUniform2f(location,(float)value.x,(float)value.y);
	}
	
	protected void load3Vector(int location, Vector3d value) {
		GL20.glUniform3f(location,(float)value.x,(float)value.y,(float)value.z);
	}
	
	protected void load4Vector(int location, Vector4d value) {
		GL20.glUniform4f(location,(float)value.x,(float)value.y,(float)value.z,(float)value.w);
	}
	
	protected void loadBoolean(int location, boolean value) {
		GL20.glUniform1f(location, value?1:0);
	}
	
	protected void loadMatrix(int location, Matrix4d value) {
		value.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(location, false, matrixBuffer);
	}
	
	public void start() {
		glUseProgram(programID);
	}

	public void stop() {
		glUseProgram(0);
	}

	public void cleanUp() {
		stop();
		glDetachShader(programID, vertexShaderID);
		glDetachShader(programID, fragmentShaderID);
		glDeleteShader(vertexShaderID);
		glDeleteShader(fragmentShaderID);
		if(geometryShaderID!=0) {
			glDetachShader(programID, geometryShaderID);
			glDeleteShader(geometryShaderID);
		}
		glDeleteProgram(programID);
	}
	
	protected abstract void bind();
	
	protected void bindAttribute(int i, String name) {
		glBindAttribLocation(programID, i, name);
	}
	
	private static int loadShader(String file, int type) {
		StringBuilder shaderSource = new StringBuilder();
		try {
			InputStream in = Class.class.getResourceAsStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while((line = reader.readLine()) != null){
				shaderSource.append(line).append("\n");
			}
			reader.close();
		} catch(Exception e) {
			Log.printError(e);
			System.exit(-1);
		}
		int shaderID = glCreateShader(type);
		glShaderSource(shaderID, shaderSource);
		glCompileShader(shaderID);
		if(glGetShaderi(shaderID, GL_COMPILE_STATUS)==GL_FALSE) {
			Log.println(glGetShaderInfoLog(shaderID, 500));
			Log.println("Could Not Compile Shader");
			System.exit(-1);
		}
		return shaderID;
	}
	
	public void loadProjectionMatrix(Matrix4d matrix4f) {
		loadMatrix(location_projectionMatrix, matrix4f);
	}
}