package sjmhrp.gui;

import sjmhrp.linear.Vector2d;
import sjmhrp.linear.Vector4d;
import sjmhrp.models.ModelPool;
import sjmhrp.render.RenderRegistry;

public class BasicGUIComponent extends GUIComponent {

	protected int texture;
	protected final Vector2d size = new Vector2d(1);

	public BasicGUIComponent(int texture) {
		this.texture = texture;
		setModel(ModelPool.getModel("quad"));
		RenderRegistry.registerGUIComponent(this);
	}

	public void remove() {
		RenderRegistry.removeGUIComponent(this);
	}
	
	public BasicGUIComponent setTexture(int texture) {
		this.texture=texture;
		return this;
	}
	
	public BasicGUIComponent setSize(double x, double y) {
		this.size.set(x,y);
		return this;
	}
	
	public int getTexture() {
		return texture;
	}
	
	public Vector2d getSize() {
		return size;
	}

	public Vector4d getBounds() {
		return new Vector4d(offset.x-size.x,offset.y-size.y,offset.x+size.x,offset.y+size.y);
	}
}