package sjmhrp.render.gui.text;

import java.util.function.IntConsumer;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import sjmhrp.event.KeyListener;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.utils.linear.Vector4d;

public class TextButton extends GUIText implements KeyListener {

	{PhysicsEngine.addTickListener(this);}
	
	double originalSize;
	IntConsumer onClick;
	
	public TextButton(String text) {
		this(text,(IntConsumer)null);
	}
	
	public TextButton(String text, String font) {
		this(text,font,null);
	}
	
	public TextButton(String text, IntConsumer onClick) {
		this(text,DEFAULT_FONT,onClick);
	}
	
	public TextButton(String text, String font, IntConsumer onClick) {
		super(text,font);
		originalSize = fontSize;
		addAttribute("button");
		this.onClick=onClick;
	}

	public TextButton setOnClick(IntConsumer onClick) {
		this.onClick=onClick;
		return this;
	}
	
	@Override
	public GUIText setFontSize(double size) {
		originalSize = size;
		return super.setFontSize(size);
	}
	
	@Override
	public void tick() {
		if(!active)return;
		if(isSelected()) {
			onHover();
		} else {
			stopHover();
		}
	}

	@Override
	public void keyPressed(int key) {
	}

	@Override
	public void keyReleased(int key) {
	}

	@Override
	public void mousePressed(int key) {
	}

	@Override
	public void mouseReleased(int key) {
		if(active&&isSelected())onClick(key);
	}
	
	public boolean isSelected() {
		Vector4d bounds = getBounds();
		double x = 2d*Mouse.getX()/Display.getWidth()-1;
		double y = 2d*Mouse.getY()/Display.getHeight()-1;
		return x>=bounds.x&&y>=bounds.y&&x<=bounds.z&&y<=bounds.w;
	}
	
	public void onClick(int key) {
		if(onClick!=null)onClick.accept(key);
	}
	
	public void onHover() {
		fontSize = originalSize*1.1;
	}
	
	public void stopHover() {
		fontSize = originalSize;
	}

	@Override
	public boolean keepOnLoad() {
		return true;
	}
}