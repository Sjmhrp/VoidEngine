package sjmhrp.event;

public interface KeyListener extends EventListener {

	void keyPressed(int key);
	
	void keyReleased(int key);
	
	void mousePressed(int key);
	
	void mouseReleased(int key);
}