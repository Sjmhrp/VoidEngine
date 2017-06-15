package sjmhrp.view;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import org.lwjgl.input.Mouse;

import sjmhrp.event.KeyHandler;
import sjmhrp.event.KeyListener;
import sjmhrp.linear.Matrix4d;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.utils.MatrixUtils;

public class Camera implements KeyListener {

	static final double TURN_SPEED = 5;
	static final double MOVE_SPEED = 30;
	
	static final double MAX_LOOK_UP = 90;
	static final double MAX_LOOK_DOWN = -90;
	
	private final Vector3d position = new Vector3d();
	private final Vector3d orientation = new Vector3d();
	
	private final Matrix4d viewMatrix = new Matrix4d();
	private final Matrix4d rotMatrix = new Matrix4d();
	
	public Camera(Vector3d pos, Vector3d ori) {
		position.set(pos);
		orientation.set(ori);
		PhysicsEngine.addEventListener(this);
	}

	public Camera(Vector3d pos) {
		this(pos,new Vector3d());
	}

	public void updateViewMatrices() {
		viewMatrix.setIdentity();
		viewMatrix.mul(MatrixUtils.createRotation(Vector3d.scale(Math.PI/180,orientation)));
		rotMatrix.set(viewMatrix);
		viewMatrix.translate(position.getNegative());
	}

	public Matrix4d getViewMatrix() {
		return viewMatrix;
	}
	
	public Matrix4d getRotMatrix() {
		return rotMatrix;
	}
	
	void processMouse(double dt) {
		double dx = Mouse.getDX()*TURN_SPEED*dt;
		double dy = -Mouse.getDY()*TURN_SPEED*dt;
		orientation.y+=dx;
		orientation.y%=360;
		while(orientation.y<0)orientation.y+=360;
		orientation.x+=dy;
		if(orientation.x>MAX_LOOK_UP)orientation.x=MAX_LOOK_UP;
		if(orientation.x<MAX_LOOK_DOWN)orientation.x=MAX_LOOK_DOWN;
	}
	
	void processKeyboard(double dt) {
		Vector3d d = new Vector3d();
		if(KeyHandler.keyPressed("forward"))d.z-=MOVE_SPEED*dt;
		if(KeyHandler.keyPressed("back"))d.z+=MOVE_SPEED*dt;
		if(KeyHandler.keyPressed("left"))d.x-=MOVE_SPEED*dt;
		if(KeyHandler.keyPressed("right"))d.x+=MOVE_SPEED*dt;
		if(KeyHandler.keyPressed("up"))position.y+=MOVE_SPEED*dt;
		if(KeyHandler.keyPressed("down"))position.y-=MOVE_SPEED*dt;
		moveFromDir(d);
	}
	
	void moveFromDir(Vector3d v) {
		position.x+=v.x*cos(toRadians(orientation.y))-v.z*sin(toRadians(orientation.y));
		position.y-=v.y*sin(toRadians(orientation.x))-v.z*sin(toRadians(orientation.x));
		position.z+=v.x*sin(toRadians(orientation.y))+v.z*cos(toRadians(orientation.y));
	}

	@Override
	public void tick() {
		processMouse(PhysicsEngine.getTimeStep());
		processKeyboard(PhysicsEngine.getTimeStep());
		updateViewMatrices();
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
	}

	@Override
	public boolean canPause() {
		return true;
	}
}