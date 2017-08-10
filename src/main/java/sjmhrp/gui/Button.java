package sjmhrp.gui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import sjmhrp.linear.Vector4d;
import sjmhrp.models.ModelPool;

public abstract class Button extends AbstractButton {

	protected int defaultTexture;
	protected int hoverTexture;

	public Button(int texture) {
		super(texture);
		setModel(ModelPool.getModel("quad"));
		this.texture = texture;
		this.defaultTexture = texture;
		this.hoverTexture = texture;
	}

	@Override
	public void remove() {
		super.remove();
	}
	
	@Override
	public boolean isSelected() {
		Vector4d bounds = getBounds();
		double x = 2d*Mouse.getX()/Display.getWidth()-1;
		double y = 2d*Mouse.getY()/Display.getHeight()-1;
		return x>=bounds.x&&y>=bounds.y&&x<=bounds.z&&y<=bounds.w;
	}

	@Override
	public abstract void onClick(int key);

	@Override
	public void onHover() {
		setTexture(hoverTexture);
	}

	@Override
	public void stopHover() {
		setTexture(defaultTexture);		
	}
	
	public Button setBackground(int texture) {
		defaultTexture=texture;
		return this;
	}
	
	public Button setRollover(int texture) {
		hoverTexture=texture;
		return this;
	}
}