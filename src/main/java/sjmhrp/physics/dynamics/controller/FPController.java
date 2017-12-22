package sjmhrp.physics.dynamics.controller;

import sjmhrp.event.KeyHandler;
import sjmhrp.event.KeyListener;
import sjmhrp.io.ConfigHandler;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.physics.collision.Contact;
import sjmhrp.physics.collision.Manifold;
import sjmhrp.physics.collision.RaycastResult;
import sjmhrp.physics.dynamics.Ray;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.physics.shapes.CapsuleShape;
import sjmhrp.render.view.Camera;
import sjmhrp.utils.MatrixUtils;
import sjmhrp.utils.VectorUtils;
import sjmhrp.utils.linear.Matrix3d;
import sjmhrp.utils.linear.Quaternion;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.world.World;

public class FPController extends Controller implements KeyListener {

	private double halfHeight;
	private CapsuleShape shape;
	RigidBody body;
	Vector3d position = new Vector3d();
	Camera camera;
	
	Vector3d raySource;
	Vector3d rayTarget;
	double distance;
	
	private double fallMultiplier = 2.5;
	private double lowFallMultiplier = 2;
	private double maxLinearVelocity = 10;
	private double[] linearAccel = {8,1};
	private double accelThreshold = 5;
	
	{PhysicsEngine.addTickListener(this);}
	
	public FPController(World world, Camera camera) {
		this.camera=camera;
		shape = new CapsuleShape(1.207841,2.911351+1.149283-2*1.207841);
		halfHeight=shape.getHeight()/2d;
		distance=halfHeight;
		body = new RigidBody(new Vector3d(),5,shape);
		body.setCanSleep(false);
		body.setAngularFactor(new Vector3d());
		body.setFriction(0);
		body.setRestitution(0);
		world.addBody(body);
//		new EntityFactory(body).setColour("red").build().setSkew(new Matrix4d().setIdentity().translate(new Vector3d(0,-0.881034,0.4519965)));
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
		position.set(body.getPosition());
		boolean left = KeyHandler.keyPressed("left");
		boolean right = KeyHandler.keyPressed("right");
		boolean forward = KeyHandler.keyPressed("forward");
		boolean back = KeyHandler.keyPressed("back");
		
		Matrix3d basis = camera.getRotMatrix().getInverse().to3Matrix();
		Vector3d down = body.getGravityDir();
		Vector3d forwardDir = basis.transform(new Vector3d(0,0,-1));
		Vector3d leftDir = Vector3d.cross(forwardDir,down);
		
		if(!onGround()) {
			if(body.getLinearVel().dot(down)>0) {
				body.applyCentralForce(body.getWorld().getGravity(body).scale(fallMultiplier-1));
			} else if(KeyHandler.keyPressed("jump")) {
				body.applyCentralForce(body.getWorld().getGravity(body).scale(lowFallMultiplier-1));
			}
		}
		
		raySource=new Vector3d(body.getPosition());
		rayTarget=down.scale(halfHeight*2).add(raySource);
		RaycastResult result = body.getWorld().raycast(new Ray(raySource,rayTarget),body);
		distance=result.collides()?result.distance():halfHeight;
		Vector3d walkDir = new Vector3d();
		if(forward)walkDir.add(forwardDir);
		if(back)walkDir.sub(forwardDir);
		if(left)walkDir.add(leftDir);
		if(right)walkDir.sub(leftDir);
//		if(!forward&&!back&&!left&&!right) {
//			if(onGround()) {
//				Vector3d a = Vector3d.scale(Math.pow(0.2,PhysicsEngine.getTimeStep()*60),body.getLinearVel());
//				a.y=body.getLinearVel().y;
//				body.getLinearVel().set(a);
//			}
//		} else {
//			if(body.getLinearVel().length()<maxLinearVelocity) {
//				double limit = linearAccel[body.getLinearVel().length()<accelThreshold?0:1]*PhysicsEngine.getTimeStep();
//				if(body.getLinearVel().lengthSquared()!=0)limit*=2-walkDir.getUnit().dot(body.getLinearVel().getUnit());
//				walkDir.scale(maxLinearVelocity);
//				Vector3d delta = walkDir.sub(body.getLinearVel());
//				delta.y=0;
//				double l = delta.length();
//				if(l>limit)delta.scale(limit/l);
//				body.addLinearVelocity(delta);
//			} else {
//				walkDir.normalize().scale(maxLinearVelocity);
//				walkDir.y=body.getLinearVel().y;
//				body.setLinearVel(walkDir);
//			}
//		}
		if(walkDir.lengthSquared()!=0)walkDir.normalize();
		if(body.getLinearVel().length()<maxLinearVelocity) {
			double limit = linearAccel[body.getLinearVel().length()<accelThreshold?0:1]*PhysicsEngine.getTimeStep();
			if(body.getLinearVel().lengthSquared()!=0)limit*=2-walkDir.dot(body.getLinearVel().getUnit());
			walkDir.scale(maxLinearVelocity);
			Vector3d delta = walkDir.sub(body.getLinearVel());
			delta.y=0;
			double l = delta.length();
			if(l>limit)delta.scale(limit/l);
			body.addLinearVelocity(delta);
		} else {
			walkDir.scale(maxLinearVelocity);
			walkDir.y=body.getLinearVel().y;
			body.setLinearVel(walkDir);
		}
	}

	@Override
	public void setOrientation(Vector3d orientation) {
		Matrix3d rotMatrix = MatrixUtils.createRotation(orientation).to3Matrix();
		Vector3d dir = rotMatrix.getInverse().transform(new Vector3d(0,0,-1));
		Vector3d v = dir.sub(VectorUtils.proj(new Vector3d(0,1,0),dir)).normalize();
		double angle=Math.acos(v.x);
		if(v.z>0)angle*=-1;
		angle+=Math.PI*0.5;
		body.setOrientation(new Quaternion().setAxis(new Vector3d(0,1,0),angle));
	}

	@Override
	public void setPosition(Vector3d position) {
		body.setPosition(position);
	}

	@Override
	public Vector3d getCameraPosition() {
		return position;
	}	
	
	void jump() {
		if(!onGround())return;
//		Matrix3d basis = camera.getRotMatrix().getInverse().to3Matrix();
//		Vector3d up = basis.transform(new Vector3d(0,1,0));
		Vector3d up = body.getGravityDir().getNegative();
		body.addLinearVelocity(up.scale(8));
	}
	
	boolean canJump() {
		return onGround();
	}
	
	public boolean onGround() {
		Vector3d up = body.getGravityDir().getNegative();
		for(Manifold m : body.getWorld().getCollisions(body)) {
			boolean first = body!=m.body1;
			for(Contact c : m.points) {
				Vector3d point = first?c.globalPointA:c.globalPointB;
				if(Vector3d.sub(body.getPosition(),point).dot(up)>=0)return true;
			}
		}
		return false;
//		return distance<halfHeight;
	}
	
	public RigidBody getCollisionObject() {
		return body;
	}

	@Override
	public boolean keepOnLoad() {
		return false;
	}
}