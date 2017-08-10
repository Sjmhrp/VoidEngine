package sjmhrp.gui;

import java.util.ArrayList;

import sjmhrp.gui.animation.GUIAnimation;
import sjmhrp.gui.text.GUIText;
import sjmhrp.linear.Vector2d;
import sjmhrp.models.RawModel;
import sjmhrp.render.RenderRegistry;

public class GUIComponent {

	protected ArrayList<String> attributes = new ArrayList<String>();
	protected RawModel model;
	protected final Vector2d offset = new Vector2d();
	protected double opacity = 1;
	protected boolean active = true;
	GUIAnimation animation;

	public void remove() {
		if(this instanceof BasicGUIComponent)RenderRegistry.removeGUIComponent((BasicGUIComponent)this);
		if(this instanceof GUIText)RenderRegistry.removeGUIText((GUIText)this);
	}
	
	public GUIComponent setModel(RawModel model) {
		this.model = model;
		return this;
	}
	
	public GUIComponent setOpacity(double opacity) {
		this.opacity = opacity;
		return this;
	}

	public GUIComponent setActive(boolean active) {
		if(animation!=null) {
			if(active&&!this.active)animation.start();
			if(!active&&this.active)animation.stop();
		}
		this.active = active;
		return this;
	}
	
	public GUIComponent setOffset(Vector2d offset) {
		return setOffset(offset.x,offset.y);
	}
	
	public GUIComponent setOffset(double x, double y) {
		this.offset.set(x,y);
		return this;
	}
	
	public GUIComponent addAttribute(String att) {
		attributes.add(att);
		return this;
	}
	
	public GUIComponent addAnimation(GUIAnimation g) {
		g.setComponent(this);
		GUIHandler.addAnimation(g);
		animation = g;
		return this;
	}
	
	public RawModel getModel() {
		return model;
	}

	public Vector2d getOffset() {
		return offset;
	}

	public double getOpacity() {
		return opacity;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public boolean hasAttribute(String att) {
		return attributes.contains(att);
	}
}