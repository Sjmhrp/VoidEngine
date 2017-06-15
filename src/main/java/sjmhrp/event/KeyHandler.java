package sjmhrp.event;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import sjmhrp.io.ConfigHandler;

public class KeyHandler {

	public static boolean[] keys = new boolean[Keyboard.KEYBOARD_SIZE];
	public static boolean[] buttons = new boolean[Mouse.getButtonCount()];
	
	public static void tick(ArrayList<KeyListener> listeners) {
		while(Keyboard.next()) {
			int key = Keyboard.getEventKey();
			boolean pressed = Keyboard.getEventKeyState();
			for(KeyListener l : listeners) {
				if(pressed) {
					l.keyPressed(key);
				} else {
					l.keyReleased(key);
				}
			}
			keys[key]=pressed;
		}
		while(Mouse.next()) {
			int button = Mouse.getEventButton();
			if(button<0)continue;
			boolean pressed = Mouse.getEventButtonState();
			for(KeyListener l : listeners) {
				if(pressed) {
					l.mousePressed(button);
				} else {
					l.mouseReleased(button);
				}
			}
			buttons[button]=pressed;
		}
	}
	
	public static boolean keyPressed(String action) {
		ArrayList<Integer> bindings = ConfigHandler.getKeyBinding(action);
		if(bindings==null)return false;
		for(Integer i : bindings) {
			if(Keyboard.isKeyDown(i))return true;
		}
		return false;
	}
}