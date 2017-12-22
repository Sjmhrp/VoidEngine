package sjmhrp.physics.dynamics.controller;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import sjmhrp.event.KeyHandler;
import sjmhrp.io.ConfigHandler;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.physics.collision.Contact;
import sjmhrp.physics.collision.Manifold;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.physics.shapes.CompoundShape;
import sjmhrp.physics.shapes.EllipsoidShape;
import sjmhrp.physics.shapes.SphereShape;
import sjmhrp.utils.ScalarUtils;
import sjmhrp.utils.VectorUtils;
import sjmhrp.utils.linear.Transform;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.world.World;

public class FirstPersonRigidBodyController extends Controller {

	public double walkSpeed = 12;
	public double runSpeed = 22;
	
	public double maxAcceleration = 20;
	
	public boolean limitDiagonal = true;
	public boolean toggleRun = false;
	
	public double jumpHeight = 5;

	public boolean slideWhenOverSlopeLimit = false;
	public double slideSpeed = 40;
	
	public double airControl = 0.05;
	
	public double antiBumpFactor = 0.01;
	public int antiBunnyHopFactor = 2;
	
	public double coyoteTime = 0.5;
	
	private Vector3d moveDir;
	private Vector3d orientation;
	private double[] modifiers;
	private double speed;
	private double grounded;
	private double scale;
	private int jumpTimer;
	
	RigidBody body;
	
	{PhysicsEngine.addTickListener(this);}
	
	public FirstPersonRigidBodyController(double mass, double scale, World world) {
		CompoundShape shape = new CompoundShape();
		shape.add(new EllipsoidShape(new Vector3d(2,2.5,2).scale(scale)),new Transform());
		shape.add(new SphereShape(1.5*scale),new Transform(new Vector3d(0,-scale,0)));
		body = new RigidBody(mass,shape);
		jumpTimer=antiBunnyHopFactor;
		this.scale=scale;
		moveDir = new Vector3d();
		speed = walkSpeed;
		grounded=coyoteTime;
		modifiers = new double[]{1,1,1,1};
		body.setAngularFactor(new Vector3d());
		body.setRestitution(0);
		body.setFriction(0);
		body.setCanSleep(false);
		world.addBody(body);
	}
	
	@Override
	public void tick() {
		double maxImpulse = maxAcceleration*PhysicsEngine.getTimeStep();
		Vector3d input = new Vector3d();
		if(KeyHandler.keyPressed("forward"))input.z-=1*modifiers[0];
		if(KeyHandler.keyPressed("back"))input.z+=1*modifiers[1];
		if(KeyHandler.keyPressed("left"))input.x-=1*modifiers[2];
		if(KeyHandler.keyPressed("right"))input.x+=1*modifiers[3];
		if(limitDiagonal) {
			double l = input.length();
			if(l!=0)input.scale(1d/l);
		}
		if(grounded()) {
			if(!toggleRun)speed=KeyHandler.keyPressed("run")?runSpeed:walkSpeed;
			moveDir.x=input.x*cos(toRadians(orientation.y))-input.z*sin(toRadians(orientation.y));
			moveDir.z=input.x*sin(toRadians(orientation.y))+input.z*cos(toRadians(orientation.y));
			moveDir.scale(new Vector3d(speed,1,speed));
			Vector3d delta = Vector3d.sub(moveDir,body.getLinearVel());
			delta.x=ScalarUtils.clamp(delta.x,-maxImpulse,maxImpulse);
			delta.y=0;
			delta.z=ScalarUtils.clamp(delta.z,-maxImpulse,maxImpulse);
			if(jumpTimer<antiBunnyHopFactor)jumpTimer++;
			if(KeyHandler.keyPressed("jump")&&jumpTimer>=antiBunnyHopFactor) {
				body.addLinearVelocity(body.getGravityDir().scale(-Math.sqrt(2*jumpHeight)));
				jumpTimer=0;
				grounded=coyoteTime;
			} else {
				delta.add(body.getGravityDir().scale(antiBumpFactor));
			}
			body.addLinearVelocity(VectorUtils.clamp(delta,-maxImpulse,maxImpulse));
		} else {
			if(airControl!=0) {
				moveDir.x=input.x*cos(toRadians(orientation.y))-input.z*sin(toRadians(orientation.y));
				moveDir.z=input.x*sin(toRadians(orientation.y))+input.z*cos(toRadians(orientation.y));
				moveDir.scale(speed*airControl);
				moveDir=VectorUtils.clamp(moveDir,-maxImpulse,maxImpulse);
				Vector3d total = Vector3d.add(moveDir,body.getLinearVel());
				if(total.lengthSquared()<=speed*speed)body.addLinearVelocity(moveDir);
			}
		}
		for(Manifold m : body.getWorld().getCollisions(body)) {
			boolean order = m.body1==body;
			for(Contact c : m.points) {
				Vector3d pos = order?c.globalPointA:c.globalPointB;
				Vector3d globalA = Transform.transform(m.transform1,c.localPointA);
				Vector3d globalB = Transform.transform(m.transform2,c.localPointB);
				Vector3d rAB = Vector3d.sub(globalB,globalA);
				boolean p = c.originalNormal.dot(rAB)<0;
				if(p&&pos.y<body.getPosition().y-getScale()) {
					grounded=0;
					return;
				}
			}
		}
		grounded+=PhysicsEngine.getTimeStep();
		grounded=Math.min(grounded,coyoteTime);
	}

	@Override
	public void keyPressed(int key) {
		if(toggleRun&&grounded()&&ConfigHandler.getKeyBinding("run").contains(key))speed=speed==runSpeed?walkSpeed:runSpeed;
		if(ConfigHandler.getKeyBinding("forward").contains(key))modifiers[0]*=0.5;
		if(ConfigHandler.getKeyBinding("back").contains(key))modifiers[1]*=0.5;
		if(ConfigHandler.getKeyBinding("left").contains(key))modifiers[2]*=0.5;
		if(ConfigHandler.getKeyBinding("right").contains(key))modifiers[3]*=0.5;
	}

	@Override
	public void keyReleased(int key) {
		if(ConfigHandler.getKeyBinding("forward").contains(key))modifiers[0]*=0.5;
		if(ConfigHandler.getKeyBinding("back").contains(key))modifiers[1]*=0.5;
		if(ConfigHandler.getKeyBinding("left").contains(key))modifiers[2]*=0.5;
		if(ConfigHandler.getKeyBinding("right").contains(key))modifiers[3]*=0.5;
	}

	@Override
	public void mousePressed(int key) {
	}

	@Override
	public void mouseReleased(int key) {
	}
	
	public void setOrientation(Vector3d o) {
		orientation=o;
	}

	public boolean grounded() {
		return grounded<coyoteTime;
	}
	
	public double getScale() {
		return scale;
	}
	
	public Vector3d getCameraPosition() {
		Vector3d position = new Vector3d(body.getPosition());
		position.y+=1.5*getScale();
		return position;
	}

	@Override
	public void setPosition(Vector3d position) {
		body.setPosition(position);
	}

	@Override
	public boolean keepOnLoad() {
		return false;
	}
}