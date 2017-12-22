package sjmhrp.render.gui;

import sjmhrp.render.RenderRegistry;
import sjmhrp.render.models.ModelPool;
import sjmhrp.utils.linear.Vector2d;
import sjmhrp.utils.linear.Vector4d;

public class GUIBox extends GUIComponent {

	protected int texture;
	protected final Vector2d size = new Vector2d(1);

	public GUIBox(int texture) {
		this.texture = texture;
		setModel(ModelPool.getModel("quad"));
		RenderRegistry.registerGUIComponent(this);
	}

	public void remove() {
		RenderRegistry.removeGUIComponent(this);
	}
	
	public GUIBox setTexture(int texture) {
		this.texture=texture;
		return this;
	}
	
	public GUIBox setSize(double x, double y) {
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