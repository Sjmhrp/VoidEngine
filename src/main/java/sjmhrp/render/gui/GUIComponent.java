package sjmhrp.render.gui;

import static java.lang.Math.*;

import java.util.ArrayList;

import sjmhrp.render.RenderRegistry;
import sjmhrp.render.gui.animation.GUIAnimation;
import sjmhrp.render.gui.text.GUIText;
import sjmhrp.render.models.RawModel;
import sjmhrp.utils.linear.Matrix2d;
import sjmhrp.utils.linear.Vector2d;

public class GUIComponent {

	protected ArrayList<String> attributes = new ArrayList<String>();
	protected RawModel model;
	protected final Vector2d offset = new Vector2d();
	protected double angle = 0;
	protected double opacity = 1;
	protected boolean active = false;
	GUIAnimation animation;

	public void remove() {
		if(this instanceof GUIBox)RenderRegistry.removeGUIComponent((GUIBox)this);
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

	public GUIComponent setAngle(double angle) {
		this.angle=angle;
		return this;
	}
	
	public GUIComponent addAngle(double angle) {
		this.angle+=angle;
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

	public Matrix2d getRotation() {
		return new Matrix2d(cos(angle),-sin(angle),sin(angle),cos(angle));
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