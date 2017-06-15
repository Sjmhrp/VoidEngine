package sjmhrp.post;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class Fbo {

	public static final int COLOUR_ATTACHMENTS = 2;
	
    public static final int NONE = 0;
    public static final int DEPTH_TEXTURE = 1;
    public static final int DEPTH_STENCIL_BUFFER = 2;
    
    private final int width;
    private final int height;
 
    private boolean multi = false;
    
    private int frameBuffer;
 
    private int colourTexture;
    private int depthTexture;
 
    private int depthBuffer;
    private int[] colourBuffers = new int[COLOUR_ATTACHMENTS];
    
    private int depthStencilBuffer;
    
    public Fbo(int width, int height, int depthBufferType) {
        this.width = width;
        this.height = height;
        initialiseFrameBuffer(depthBufferType);
    }
 
    public Fbo(int width, int height) {
        this.width = width;
        this.height = height;
        this.multi = true;
        initialiseFrameBuffer(DEPTH_STENCIL_BUFFER);
    }
    
    public void cleanUp() {
        GL30.glDeleteFramebuffers(frameBuffer);
        GL11.glDeleteTextures(colourTexture);
        GL11.glDeleteTextures(depthTexture);
        GL30.glDeleteRenderbuffers(depthBuffer);
        for(int i = 0; i < colourBuffers.length; i++) {
        	GL30.glDeleteRenderbuffers(colourBuffers[i]);
        }
    }
 
    public void bindFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBuffer);
        GL11.glViewport(0, 0, width, height);
    }

    public void unbindFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
    }
 
    public int getColourTexture() {
        return colourTexture;
    }
 
    public int getDepthTexture() {
        return depthTexture;
    }
    
    public int getDepthBuffer() {
		return depthBuffer;
	}

	public int getDepthStencilBuffer() {
		return depthStencilBuffer;
	}

	public int getFrameBuffer() {
    	return frameBuffer;
    }
    
    public int getWidth() {
    	return width;
    }
    
    public int getHeight() {
    	return height;
    }
    
    public void resolve(int readBuffer, Fbo output) {
    	GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, output.frameBuffer);
    	GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.frameBuffer);
    	GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0+readBuffer);
    	GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, output.width, output.height, GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT, GL11.GL_NEAREST);
    	unbindFrameBuffer();
    }

    public void resolveDepth(Fbo output) {
    	GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER,output.frameBuffer);
    	GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER,this.frameBuffer);
    	GL11.glReadBuffer(GL30.GL_DEPTH_STENCIL_ATTACHMENT);
    	GL30.glBlitFramebuffer(0, 0, width, height, 0, 0, output.width, output.height, GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT, GL11.GL_NEAREST);
    	unbindFrameBuffer();
    }
    
    private void initialiseFrameBuffer(int type) {
        createFrameBuffer();
        if(multi) {
        	for(int i = 0; i < colourBuffers.length; i++) {
        		createMultiColourAttachment(i);
        	}
        } else {
        	createTextureAttachment();
        }
        if (type == DEPTH_TEXTURE) {
        	createStencilDepthTexture();
        } else if (type == DEPTH_STENCIL_BUFFER) {
        	createDepthStencilBuffer();
        }
        unbindFrameBuffer();
    }
 
    private void createFrameBuffer() {
        frameBuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        determineDrawBuffers();
    }
 
    private void determineDrawBuffers() {
    	IntBuffer drawBuffers = BufferUtils.createIntBuffer(COLOUR_ATTACHMENTS);
    	drawBuffers.put(GL30.GL_COLOR_ATTACHMENT0);
    	if(multi) {
    		for(int i = 1; i < colourBuffers.length; i++) {
    			drawBuffers.put(GL30.GL_COLOR_ATTACHMENT0+i);
    		}
    	}
    	drawBuffers.flip();
    	GL20.glDrawBuffers(drawBuffers);
    }
    
    private void createTextureAttachment() {
        colourTexture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colourTexture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA32F, width, height, 0, GL11.GL_RGBA, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colourTexture, 0);
    }
    
    private void createStencilDepthTexture() {
    	depthTexture = GL11.glGenTextures();
    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
    	GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_DEPTH32F_STENCIL8, width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    	GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0);
    }
   
    private void createMultiColourAttachment(int i) {
    	colourBuffers[i] = GL30.glGenRenderbuffers();
    	GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, colourBuffers[i]);
    	GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER,GL30.GL_RGBA32F, width, height);
    	GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0+i, GL30.GL_RENDERBUFFER, colourBuffers[i]);
    }
    
    private void createDepthStencilBuffer() {
    	depthStencilBuffer = GL30.glGenRenderbuffers();
    	GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthStencilBuffer);
    	GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH32F_STENCIL8, width, height);
    	GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_STENCIL_ATTACHMENT, GL30.GL_RENDERBUFFER, depthStencilBuffer);
    }
}