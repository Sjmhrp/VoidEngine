package sjmhrp.particle;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

import sjmhrp.render.Loader;
import sjmhrp.render.RenderRegistry;
import sjmhrp.render.models.RawModel;
import sjmhrp.render.textures.ParticleTexture;
import sjmhrp.render.view.Camera;
import sjmhrp.utils.Profiler;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Vector2d;

public class ParticleRenderer {

	public static final int MAX_PARTICLES = 10000;
	public static final int DATA_LENGTH = 22;
	
	private static final FloatBuffer buffer = BufferUtils.createFloatBuffer(DATA_LENGTH * MAX_PARTICLES);
	
	static RawModel quad;
	public static int vbo;
	public static int pointer;
	
	public static void init() {
		quad = Loader.load(new double[]{-1,1,-1,-1,1,1,1,-1},2);
		vbo = Loader.createEmptyVBO(DATA_LENGTH*MAX_PARTICLES);
		Loader.addInstancedAttrib(quad.getVaoId(),vbo,1,4,DATA_LENGTH,0);
		Loader.addInstancedAttrib(quad.getVaoId(),vbo,2,4,DATA_LENGTH,4);
		Loader.addInstancedAttrib(quad.getVaoId(),vbo,3,4,DATA_LENGTH,8);
		Loader.addInstancedAttrib(quad.getVaoId(),vbo,4,4,DATA_LENGTH,12);
		Loader.addInstancedAttrib(quad.getVaoId(),vbo,5,4,DATA_LENGTH,16);
		Loader.addInstancedAttrib(quad.getVaoId(),vbo,6,1,DATA_LENGTH,20);
		Loader.addInstancedAttrib(quad.getVaoId(),vbo,7,1,DATA_LENGTH,21);
	}
	
	public static void renderParticles(Camera c, ParticleShader s) {
		s.start();
		Matrix4d vm = c.getViewMatrix();
		glDepthMask(false);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);
		GL30.glBindVertexArray(quad.getVaoId());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glEnableVertexAttribArray(2);
		GL20.glEnableVertexAttribArray(3);
		GL20.glEnableVertexAttribArray(4);
		GL20.glEnableVertexAttribArray(5);
		GL20.glEnableVertexAttribArray(6);
		GL20.glEnableVertexAttribArray(7);
		RenderRegistry.getAllParticles().stream().filter(p->p.ttl<=0).forEach(RenderRegistry::removeParticle);
		for(Entry<ParticleTexture,ArrayList<Particle>> e : RenderRegistry.getParticles().entrySet()) {
			ParticleTexture t = e.getKey();
			ArrayList<Particle> ps = e.getValue();
			s.loadRows(t.getRows());
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D,t.getTexture());
			pointer = 0;
			float[] data = new float[Math.min(ps.size(),MAX_PARTICLES)*DATA_LENGTH];
			for(Particle p : ps) {
				if(p==null||pointer>=buffer.capacity())continue;
				Matrix4d modelMatrix = new Matrix4d().setIdentity();
				modelMatrix.translate(p.getPosition());
				modelMatrix.m00 = vm.m00;
				modelMatrix.m01 = vm.m10;
				modelMatrix.m02 = vm.m20;
				modelMatrix.m10 = vm.m01;
				modelMatrix.m11 = vm.m11;
				modelMatrix.m12 = vm.m21;
				modelMatrix.m20 = vm.m02;
				modelMatrix.m21 = vm.m12;
				modelMatrix.m22 = vm.m22;
				modelMatrix.scale(p.getScale());
				Matrix4d modelViewMatrix = Matrix4d.mul(vm,modelMatrix);
				double index = p.getAge()*t.getRows()*t.getRows();
				int textureOffset1 = (int)index;
				int textureOffset2 = textureOffset1<t.getRows()*t.getRows()-1?textureOffset1+1:0;
				Vector2d o1 = p.getTextureOffset(textureOffset1);
				Vector2d o2 = p.getTextureOffset(textureOffset2);
				storeMatrix(modelViewMatrix,data);
				data[pointer++] = (float)o1.x;
				data[pointer++] = (float)o1.y;
				data[pointer++] = (float)o2.x;
				data[pointer++] = (float)o2.y;
				data[pointer++] = (float)index-textureOffset1;
				data[pointer++] = (float)p.getAge();
			}
			Loader.updateVbo(vbo,data,buffer);
			GL31.glDrawArraysInstanced(GL_TRIANGLE_STRIP,0,quad.getVertexCount(),ps.size());
			Profiler.drawCalls++;
		}
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL20.glDisableVertexAttribArray(2);
		GL20.glDisableVertexAttribArray(3);
		GL20.glDisableVertexAttribArray(4);
		GL20.glDisableVertexAttribArray(5);
		GL20.glDisableVertexAttribArray(6);
		GL20.glDisableVertexAttribArray(7);
		GL30.glBindVertexArray(0);
		glDisable(GL_BLEND);
		glDepthMask(true);
		s.stop();
	}
	
	static void storeMatrix(Matrix4d matrix, float[] vboData) {
		vboData[pointer++] = (float)matrix.m00;
		vboData[pointer++] = (float)matrix.m10;
		vboData[pointer++] = (float)matrix.m20;
		vboData[pointer++] = (float)matrix.m30;
		vboData[pointer++] = (float)matrix.m01;
		vboData[pointer++] = (float)matrix.m11;
		vboData[pointer++] = (float)matrix.m21;
		vboData[pointer++] = (float)matrix.m31;
		vboData[pointer++] = (float)matrix.m02;
		vboData[pointer++] = (float)matrix.m12;
		vboData[pointer++] = (float)matrix.m22;
		vboData[pointer++] = (float)matrix.m32;
		vboData[pointer++] = (float)matrix.m03;
		vboData[pointer++] = (float)matrix.m13;
		vboData[pointer++] = (float)matrix.m23;
		vboData[pointer++] = (float)matrix.m33;
	}
}