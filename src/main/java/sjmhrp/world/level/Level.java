package sjmhrp.world.level;

import sjmhrp.render.gui.GUIHandler;
import sjmhrp.world.World;

public abstract class Level {

	World world;
	
	public World build() {
		GUIHandler.switchToScreen("loading");
		World w = create();
		GUIHandler.switchToScreen("main");
		return w;
	}
	
	abstract World create();
}