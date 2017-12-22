package sjmhrp.render.view;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import org.lwjgl.input.Mouse;

import sjmhrp.event.KeyHandler;
import sjmhrp.physics.dynamics.controller.Controller;
import sjmhrp.physics.dynamics.controller.DynamicTPController;
import sjmhrp.physics.dynamics.controller.FPController;
import sjmhrp.render.RenderHandler;
import sjmhrp.render.gui.GUIHandler;
import sjmhrp.utils.MatrixUtils;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.world.World;

public class Camera {

	static final double TURN_SPEED = 5;
	static final double MOVE_SPEED = 30;
	
	static final double MAX_LOOK_UP = 90;
	static final double MAX_LOOK_DOWN = -90;
	
	static final double MIN_DISTANCE = 3;
	static final double MAX_DISTANCE = 10;
	
	private final Vector3d position = new Vector3d();
	private final Vector3d velocity = new Vector3d();
	private final Vector3d angularVelocity = new Vector3d();
	private final Vector3d orientation = new Vector3d();
	private double distance;
	
	private final Matrix4d viewMatrix = new Matrix4d().setIdentity();
	private final Matrix4d rotMatrix = new Matrix4d().setIdentity();

	boolean firstPerson = true;
	Controller controller;
	
	public Camera(Vector3d pos, Vector3d ori) {
		position.set(pos);
		orientation.set(ori);
	}

	public Camera(Vector3d pos) {
		this(pos,new Vector3d());
	}
	
	public void createFPController(World world) {
		FPController p = new FPController(world,this);
		firstPerson=true;
		setController(p);
	}
	
	public void createController(World world) {
		DynamicTPController p = new DynamicTPController(this,world);
		firstPerson=false;
		distance=5;
		setController(p);
	}
	
	public void setController(Controller c) {
		if(!c.hasCamera())return;
		controller=c;
		c.setPosition(new Vector3d(position));
		c.setOrientation(new Vector3d(orientation));
	}
	
	public void removeController() {
		controller=null;
	}
	
	void updateViewMatrices() {
		rotMatrix.set(MatrixUtils.createRotation(Vector3d.scale(Math.PI/180,orientation)));
		viewMatrix.set(rotMatrix);
		viewMatrix.translate(position.getNegative());
		Frustum.updatePlanes(this);
	}
	
	public void setPosition(Vector3d position) {
		this.position.set(position);
	}
	
	public Vector3d getPosition() {
		return position;
	}
	
	public Vector3d getVelocity() {
		return velocity;
	}
	
	public void setOrientation(Vector3d orientation) {
		this.orientation.set(orientation);
	}
	
	public Vector3d getOrientation() {
		return orientation;
	}

	public Vector3d getAngularVelocity() {
		return angularVelocity;
	}
	
	public Matrix4d getViewMatrix() {
		return viewMatrix;
	}
	
	public Matrix4d getRotMatrix() {
		return rotMatrix;
	}
	
	void processMouse(double dt) {
		double turnSpeed = TURN_SPEED*(Mouse.isGrabbed()?1:2);
		double dx = Mouse.getDX()*turnSpeed*dt;
		double dy = -Mouse.getDY()*turnSpeed*dt;
		Vector3d oldO = new Vector3d(orientation);
		orientation.x+=dy;
		if(orientation.x>MAX_LOOK_UP)orientation.x=MAX_LOOK_UP;
		if(orientation.x<MAX_LOOK_DOWN)orientation.x=MAX_LOOK_DOWN;
		orientation.y+=dx;
		orientation.y%=360;
		while(orientation.y<0)orientation.y+=360;
		angularVelocity.set(orientation).sub(oldO).scale(1d/RenderHandler.getTimeStep());
		if(controller!=null&&firstPerson)controller.setOrientation(orientation);
	}
	
	void processKeyboard(double dt) {
		Vector3d d = new Vector3d();
		if(KeyHandler.keyPressed("forward"))d.z-=MOVE_SPEED*dt;
		if(KeyHandler.keyPressed("back"))d.z+=MOVE_SPEED*dt;
		if(KeyHandler.keyPressed("left"))d.x-=MOVE_SPEED*dt;
		if(KeyHandler.keyPressed("right"))d.x+=MOVE_SPEED*dt;
		if(KeyHandler.keyPressed("up"))position.y+=MOVE_SPEED*dt;
		if(KeyHandler.keyPressed("down"))position.y-=MOVE_SPEED*dt;
		Vector3d prevPos = new Vector3d(position);
		position.x+=d.x*cos(toRadians(orientation.y))-d.z*sin(toRadians(orientation.y));
		position.y-=d.y*sin(toRadians(orientation.x))-d.z*sin(toRadians(orientation.x));
		position.z+=d.x*sin(toRadians(orientation.y))+d.z*cos(toRadians(orientation.y));
		velocity.set(new Vector3d(position).sub(prevPos).scale(1d/dt));
	}

	public void tick() {
		if(GUIHandler.isPaused())return;
		if(!RenderHandler.isRenderer())return;
		if(Mouse.isGrabbed()||Mouse.isButtonDown(1))processMouse(RenderHandler.getTimeStep());
		if(controller==null) {
			processKeyboard(RenderHandler.getTimeStep());
		} else {
			Vector3d prevPos = new Vector3d(position); 
			position.set(controller.getCameraPosition());
			if(!firstPerson) {
				double h=distance*cos(toRadians(orientation.x));
				double angle = 180-orientation.y;
				double dx = h*sin(toRadians(angle));
				double dz = h*cos(toRadians(angle));
				position.add(new Vector3d(-dx,distance*sin(toRadians(orientation.x)),-dz));
			}
			velocity.set(new Vector3d(position).sub(prevPos).scale(1d/RenderHandler.getTimeStep()));
		}
		updateViewMatrices();
	}
}