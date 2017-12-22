package sjmhrp.core;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import sjmhrp.event.KeyListener;
import sjmhrp.io.ConfigHandler;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.physics.collision.RaycastResult;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.physics.dynamics.MultiRigidBody.MultiRigidBodyElement;
import sjmhrp.physics.dynamics.Ray;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.render.RenderHandler;
import sjmhrp.render.RenderRegistry;
import sjmhrp.render.entity.Entity;
import sjmhrp.render.gui.GUIHandler;
import sjmhrp.render.view.Camera;
import sjmhrp.utils.GeometryUtils;
import sjmhrp.utils.ScalarUtils;
import sjmhrp.utils.VectorUtils;
import sjmhrp.utils.linear.Vector3d;

public class MainKeyListener implements KeyListener {

	static final double MIN_GRAB_DISTANCE = 10;
	static final double MAX_GRAB_DISTANCE = 50;
	static final double SCROLL_SPEED = 1;
	
	static final double THROW_SCALE = 1;
	static final double THROW_SPEED = 20;
	
	double grabDistance = MAX_GRAB_DISTANCE;
	Camera camera;
	RigidBody grabbed;
	ArrayList<Entity> highlights = new ArrayList<Entity>();
	
	{PhysicsEngine.addTickListener(this);}

	public MainKeyListener(Camera c) {
		camera=c;
	}

	@Override
	public void tick() {
		if(!GUIHandler.isPaused()&&!Display.isActive())GUIHandler.switchToScreen("pause");
		if(!GUIHandler.getScreen().equals("edit"))grabbed=null;
		if(!RenderHandler.isRenderer()) {
			RenderHandler.addUniqueTask("MainKeyListener::tick",this::tick);
			return;
		}
		if(GUIHandler.getScreen().equals("edit")) {
			grabDistance+=Mouse.getDWheel()*PhysicsEngine.getTimeStep()*SCROLL_SPEED;
			grabDistance=ScalarUtils.clamp(grabDistance,MIN_GRAB_DISTANCE,MAX_GRAB_DISTANCE);
			Vector3d dir = camera.getRotMatrix().getInverse().transform(new Vector3d(0,0,-1));
			Ray ray = new Ray(camera.getPosition(),dir,MAX_GRAB_DISTANCE);
			RaycastResult result = PhysicsEngine.getWorlds().size()>0?PhysicsEngine.getWorlds().get(0).raycast(ray,grabbed):new RaycastResult();
			if(result.collides())grabDistance=Math.min(Math.max(result.distance(),MIN_GRAB_DISTANCE),grabDistance);
			if(result.collides()||grabbed!=null) {
				CollisionBody b = grabbed!=null?grabbed:result.body();
				ArrayList<Entity> es = new ArrayList<Entity>();
				if(b instanceof MultiRigidBodyElement) {
					for(MultiRigidBodyElement e : ((MultiRigidBodyElement)b).getElements()) {
						es.addAll(RenderRegistry.getEntities(e));
					}
				} else {
					es.addAll(RenderRegistry.getEntities(b));
				}
				highlights.forEach(e->e.setHighlight(false));
				highlights.clear();
				for(Entity e : es) {
					if(e.isHighlighted())continue;
					highlights.add(e);
					e.setHighlight(true);
				}
			}
			if(grabbed!=null){
				Vector3d pos = dir.scale(grabDistance).add(camera.getPosition());
				if(grabbed instanceof MultiRigidBodyElement) {
					((MultiRigidBodyElement)grabbed).setParentPosition(pos);
					((MultiRigidBodyElement)grabbed).stop();
					((MultiRigidBodyElement)grabbed).setSleeping(false);
				} else {
					grabbed.setPosition(pos);
					grabbed.stop();
					grabbed.setSleeping(false);
				}
			}
		} else {
			highlights.forEach(e->e.setHighlight(false));
			highlights.clear();
		}
	}
	
	@Override
	public void keyPressed(int key) {
		if(key==Keyboard.KEY_F2)ConfigHandler.setProperty("wireframe",!ConfigHandler.getBoolean("wireframe"),false);
	}

	@Override
	public void keyReleased(int key) {
		if(key==Keyboard.KEY_ESCAPE)GUIHandler.pause();
		if(key==Keyboard.KEY_E&&!GUIHandler.isPaused())GUIHandler.switchToScreen(GUIHandler.getScreen().equals("main")?"edit":"main");
	}

	@Override
	public void mousePressed(int key) {
		if(GUIHandler.getScreen().equals("edit")&&key==0&&grabbed==null) {
			Vector3d dir = camera.getRotMatrix().getInverse().transform(new Vector3d(0,0,-1));
			Ray ray = new Ray(camera.getPosition(),dir,MAX_GRAB_DISTANCE);
			RaycastResult result = PhysicsEngine.getWorlds().size()>0?PhysicsEngine.getWorlds().get(0).raycast(ray,grabbed):new RaycastResult();
			if(result.collides()&&!result.body().isStatic()) {
				grabbed=(RigidBody)result.body();
				grabDistance=GeometryUtils.distance(camera.getPosition(),grabbed.getPosition());
			}
		}
	}

	@Override
	public void mouseReleased(int key) {
		if(key==0&&grabbed!=null) {
			grabbed.setLinearVel(VectorUtils.clampLength(THROW_SPEED,Vector3d.scale(GeometryUtils.distance(camera.getPosition(),grabbed.getPosition()),camera.getAngularVelocity()).add(camera.getVelocity()).scale(THROW_SCALE)));
			grabbed=null;
		}
	}

	@Override
	public boolean keepOnLoad() {
		return true;
	}
}