package sjmhrp.physics.dynamics.controller;

import sjmhrp.event.KeyHandler;
import sjmhrp.factory.EntityFactory;
import sjmhrp.io.ConfigHandler;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.physics.collision.RaycastResult;
import sjmhrp.physics.dynamics.Ray;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.physics.shapes.CapsuleShape;
import sjmhrp.render.RenderHandler;
import sjmhrp.render.view.Camera;
import sjmhrp.utils.VectorUtils;
import sjmhrp.utils.linear.Matrix3d;
import sjmhrp.utils.linear.Matrix4d;
import sjmhrp.utils.linear.Quaternion;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.world.World;

public class DynamicTPController extends Controller {

	private double halfHeight;
	private CapsuleShape shape;
	private RigidBody body;
	private Camera camera;
	
	private Vector3d[] raySource = new Vector3d[2];
	private Vector3d[] rayTarget = new Vector3d[2];
	private double[] rayLambda = new double[2];
	
	private double turnAngle;
	private double maxLinearVelocity;
	private double[] linearAccel;
	private double turnVelocity;
	private double accelThreshold;
	private Vector3d cameraDir = new Vector3d();
	
	{PhysicsEngine.addTickListener(this);}
	
	public DynamicTPController(Camera camera, World world) {
		this.camera=camera;
		shape = new CapsuleShape(1.207841,2.911351+1.149283-2*1.207841);
		halfHeight=shape.getHeight()/2d;
		rayLambda[0]=halfHeight;
		rayLambda[1]=halfHeight;
		turnAngle=0;
		maxLinearVelocity=10;
		linearAccel = new double[]{8,1};
		turnVelocity=5;
		accelThreshold=5;
		body = new RigidBody(new Vector3d(),5,shape);
		body.setCanSleep(false);
		body.setAngularFactor(new Vector3d());
		body.setFriction(0);
		body.setRestitution(0);
		world.addBody(body);
		RenderHandler.addTask(()->new EntityFactory(body).setModel("ohno").setColour("red").build().setSkew(new Matrix4d().setIdentity().translate(new Vector3d(0,-0.881034,0.4519965))));
	}
	
	@Override
	public void keyPressed(int key) {
		if(ConfigHandler.getKeyBinding("jump").contains(key))jump();
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
	public void tick() {
		boolean left = KeyHandler.keyPressed("left");
		boolean right = KeyHandler.keyPressed("right");
		boolean forward = KeyHandler.keyPressed("forward");
		boolean back = KeyHandler.keyPressed("back");

		Matrix3d basis = body.getRotation();
		Vector3d down = basis.transform(new Vector3d(0,-1,0));
		Vector3d forwardDir = basis.transform(new Vector3d(0,0,1));
		raySource[0]=new Vector3d(body.getPosition());
		raySource[1]=new Vector3d(body.getPosition());
		rayTarget[0]=down.scale(halfHeight*1.1).add(raySource[0]);
		rayTarget[1]=Vector3d.scale(halfHeight*1.1,forwardDir).add(raySource[0]);;
		for(int i = 0; i < 2; i++) {
			RaycastResult result = body.getWorld().raycast(new Ray(raySource[i],rayTarget[i]),body);
			rayLambda[i]=result.collides()?result.distance():halfHeight;
		}
		
		if(left)turnAngle+=PhysicsEngine.getTimeStep()*turnVelocity;
		if(right)turnAngle-=PhysicsEngine.getTimeStep()*turnVelocity;
		while(turnAngle<0)turnAngle+=2*Math.PI;
		turnAngle%=2*Math.PI;
		if(forward||back) {
			Vector3d dir = camera.getRotMatrix().getInverse().transform(new Vector3d(0,0,-1));
			Vector3d v = dir.sub(VectorUtils.proj(new Vector3d(0,1,0),dir)).normalize();
			double angle=Math.acos(v.x);
			if(v.z>0)angle*=-1;
			angle+=Math.PI*0.5;
			while(angle<0)angle+=2*Math.PI;
			angle%=2*Math.PI;
			double d = PhysicsEngine.getTimeStep()*turnVelocity;
			if(Math.abs(angle-turnAngle)>Math.PI) {
				turnAngle-=Math.min(d,2*Math.PI+turnAngle-angle);
			} else {
				turnAngle+=Math.min(d,angle-turnAngle);
			}
		}
		while(turnAngle<0)turnAngle+=2*Math.PI;
		turnAngle%=2*Math.PI;
		body.setOrientation(new Quaternion().setAxis(new Vector3d(0,1,0),turnAngle).normalize());
		Vector3d walkDir = new Vector3d();
		if(forward)walkDir.add(forwardDir);
		if(back)walkDir.sub(forwardDir);
		if(!forward&&!back) {
			if(onGround())body.getLinearVel().scale(Math.pow(0.2,PhysicsEngine.getTimeStep()*60));
		} else {
			if(walkDir.lengthSquared()==0)return;
			if(body.getLinearVel().length()<maxLinearVelocity) {
				double limit = linearAccel[body.getLinearVel().length()<accelThreshold?0:1]*PhysicsEngine.getTimeStep();
				if(body.getLinearVel().lengthSquared()!=0)limit*=2-walkDir.getUnit().dot(body.getLinearVel().getUnit());
				walkDir.scale(maxLinearVelocity);
				Vector3d delta = walkDir.sub(body.getLinearVel());
				delta.y=0;
				double l = delta.length();
				if(l>limit)delta.scale(limit/l);
				body.addLinearVelocity(delta);
			} else {
				walkDir.normalize().scale(maxLinearVelocity);
				walkDir.y=body.getLinearVel().y;
				body.setLinearVel(walkDir);
			}
		}
	}

	@Override
	public void setOrientation(Vector3d orientation) {
		cameraDir.set(orientation);
	}

	@Override
	public void setPosition(Vector3d position) {
		body.setPosition(position);
	}

	@Override
	public Vector3d getCameraPosition() {
		return body.getPosition();
	}	
	
	void jump() {
		if(!onGround())return;
		Matrix3d basis = body.getRotation();
		Vector3d up = basis.transform(new Vector3d(0,1,0));
		body.addLinearVelocity(up.scale(8));
	}
	
	boolean canJump() {
		return onGround();
	}
	
	public boolean onGround() {
		return rayLambda[0]<halfHeight;
	}
	
	public RigidBody getCollisionObject() {
		return body;
	}

	@Override
	public boolean keepOnLoad() {
		return false;
	}
}