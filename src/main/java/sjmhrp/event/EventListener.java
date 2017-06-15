package sjmhrp.event;

public interface EventListener {

	void tick();
	
	boolean canPause();
}