package sjmhrp.core;

import org.lwjgl.opengl.Display;

import sjmhrp.entity.EntityBuilder;
import sjmhrp.flare.FlareRenderer;
import sjmhrp.io.ConfigHandler;
import sjmhrp.io.OBJHandler;
import sjmhrp.light.Light;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.physics.constraint.joints.PrismaticJoint;
import sjmhrp.physics.constraint.joints.RevoluteJoint;
import sjmhrp.physics.constraint.joints.SphericalJoint;
import sjmhrp.physics.constraint.joints.WeldJoint;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.physics.shapes.BoxShape;
import sjmhrp.physics.shapes.CapsuleShape;
import sjmhrp.physics.shapes.ConeShape;
import sjmhrp.physics.shapes.ConvexHullShape;
import sjmhrp.physics.shapes.CylinderShape;
import sjmhrp.physics.shapes.SphereShape;
import sjmhrp.post.Post;
import sjmhrp.render.Loader;
import sjmhrp.render.RenderHandler;
import sjmhrp.render.RenderRegistry;
import sjmhrp.render.SSAORenderer;
import sjmhrp.shaders.Shader;
import sjmhrp.textures.TerrainTexture;
import sjmhrp.view.Camera;
import sjmhrp.world.World;

public class Main {

	public static final String TITLE = "Void Engine";
	public static final String VERSION = "1.0.2";
	public static final int[] SIZE = {720,480};	

	static Camera camera = new Camera(new Vector3d(375,33,461));
	static World world;
	static Shader shader;

	public static void main(String[] args) {
		ConfigHandler.loadConfigFiles();
		RenderHandler.init(TITLE+" "+VERSION,SIZE[0],SIZE[1],false);
		new MainKeyListener();
		shader = new Shader();
		createWorld();
		createDemoLevel();
		loop();
	}

	public static void restart() {
		PhysicsEngine.clear();
		RenderRegistry.clear();
		createWorld();
		createDemoLevel();
		loop();
	}
	
	static void createWorld() {
		world = new World();
		world.generateSky();
		world.addSun();
		world.generateStars();
		world.generateRandomTerrain(0,653008806,new TerrainTexture("grass","dirt","pinkFlowers","path","map/terrainBlendMap"));
		world.setGravity(new Vector3d(0,-ConfigHandler.getDouble("gravity"),0));
	}
	
	static void createDemoLevel() {
		new Light(new Vector3d(400,20,410),new Vector3d(1,1,1),new Vector3d(1.1,0.0002,0.00004));
		torus();
		stack();
		vehicle();
		cone();
		bounce();
		revolute();
		barrel();
		string();
		piston();
	}
	
	static void torus() {
		RigidBody torus = new RigidBody(new Vector3d(392,21,395),1,OBJHandler.parseCompoundShape("TorusCol"));
		RigidBody sphere = new RigidBody(new Vector3d(392,21,390),0.1,new SphereShape(0.5));
		world.addBody(torus);
		world.addBody(sphere);
		sphere.setLinearVel(0,0,20);
		EntityBuilder.createColourEntity(torus,"Torus","lime");
		EntityBuilder.createColourEntity(sphere,"cyan");
	}
	
	static void stack() {
		BoxShape boxShape = new BoxShape(2,2,2);
		for(int i = 0; i < 10; i++) {
			RigidBody box = new RigidBody(new Vector3d(410,i*5+21,397),2,boxShape);
			world.addBody(box);
			EntityBuilder.createColourEntity(box,"lime");
		}
	}
	
	static void vehicle() {
		Vector3d offset = new Vector3d(400,21,380);
		double mass = 4;
		double density = mass/(3.2+2*Math.PI);
		RigidBody base = new RigidBody(new Vector3d(offset),density*3.2,new BoxShape(4,0.4,2));
		CylinderShape wheelShape = new CylinderShape(1,0.5);
		RigidBody wheel1 = new RigidBody(new Vector3d(-2.5,0,2.35).add(offset),density*0.5*Math.PI,wheelShape);
		RigidBody wheel2 = new RigidBody(new Vector3d(2.5,0,2.35).add(offset),density*0.5*Math.PI,wheelShape);
		RigidBody wheel3 = new RigidBody(new Vector3d(-2.5,0,-2.35).add(offset),density*0.5*Math.PI,wheelShape);
		RigidBody wheel4 = new RigidBody(new Vector3d(2.5,0,-2.35).add(offset),density*0.5*Math.PI,wheelShape);
		wheel1.rotate(Math.PI*0.5,0,0);
		wheel2.rotate(Math.PI*0.5,0,0);
		wheel3.rotate(Math.PI*0.5,0,0);
		wheel4.rotate(Math.PI*0.5,0,0);
		wheel1.setFriction(5);
		wheel2.setFriction(5);
		wheel3.setFriction(5);
		wheel4.setFriction(5);
		base.setLinearVel(-10,0,0);
		world.addBody(base);
		world.addBody(wheel1);
		world.addBody(wheel2);
		world.addBody(wheel3);
		world.addBody(wheel4);
		world.addJoint(new RevoluteJoint(base,wheel1,new Vector3d(-2.5,0,2.05).add(offset),new Vector3d(0,0,1)));
		world.addJoint(new RevoluteJoint(base,wheel2,new Vector3d(2.5,0,2.05).add(offset),new Vector3d(0,0,1)));
		world.addJoint(new RevoluteJoint(base,wheel3,new Vector3d(-2.5,0,-2.05).add(offset),new Vector3d(0,0,1)));
		world.addJoint(new RevoluteJoint(base,wheel4,new Vector3d(2.5,0,-2.05).add(offset),new Vector3d(0,0,1)));
		world.addJoint(new WeldJoint(wheel1,wheel3,new Vector3d(-2.5,0,0).add(offset)));
		world.addJoint(new WeldJoint(wheel2,wheel4,new Vector3d(2.5,0,0).add(offset)));
		EntityBuilder.createColourEntity(base,"cyan");
		EntityBuilder.createColourEntity(wheel1,"red");
		EntityBuilder.createColourEntity(wheel2,"red");
		EntityBuilder.createColourEntity(wheel3,"red");
		EntityBuilder.createColourEntity(wheel4,"red");
	}
	
	static void cone() {
		RigidBody cone = new RigidBody(new Vector3d(374,15,403),1,new ConeShape(4,8));
		cone.rotate(0,0,Math.PI);
		world.addBody(cone);
		EntityBuilder.createColourEntity(cone,"red");
	}
	
	static void bounce() {
		SphereShape sphereShape = new SphereShape(2);
		for(int i = 0; i < 10; i++) {
			RigidBody sphere = new RigidBody(new Vector3d(375,i*4+20,368),2,sphereShape);
			sphere.setRestitution(1);
			world.addBody(sphere);
			EntityBuilder.createColourEntity(sphere,"red");
		}
	}

	static void revolute() {
		RigidBody b1 = new RigidBody(new Vector3d(410,24,403),0,new BoxShape(2,2,2));
		RigidBody b2 = new RigidBody(new Vector3d(410,21.5,406.1),1,new BoxShape(1,4,1));
		RigidBody b3 = new RigidBody(new Vector3d(410,16.5,408.2),1,new BoxShape(1,4,1));
		world.addBody(b1);
		world.addBody(b2);
		world.addBody(b3);
		world.addJoint(new RevoluteJoint(b1,b2,new Vector3d(410,24,405.05),new Vector3d(0,0,1)).setMotor(2,500));
		world.addJoint(new RevoluteJoint(b2,b3,new Vector3d(410,19.5,407.15),new Vector3d(0,0,1)));
		EntityBuilder.createColourEntity(b1,"cyan");
		EntityBuilder.createColourEntity(b2,"cyan");
		EntityBuilder.createColourEntity(b3,"cyan");
	}

	static void barrel() {
		RigidBody barrel = new RigidBody(new Vector3d(410,35,404),5,new ConvexHullShape(OBJHandler.parseVertices("barrel")));
		world.addBody(barrel);
		EntityBuilder.createEntity(barrel,"barrel","barrel").loadNormalMap("barrelNormal").loadSpecularMap("barrelS");
	}

	static void string() {
		CapsuleShape b = new CapsuleShape(0.2,1.6);
		RigidBody[] bodies = new RigidBody[10];
		for(int i = 0; i < 10; i++) {
			bodies[i] = new RigidBody(new Vector3d(360,2*i+22.1,420),i==9?0:1,b);
			bodies[i].setFriction(0);
			if(i==5)bodies[i].setLinearVel(-10,2,1);
			world.addBody(bodies[i]);
			if(i!=0)world.addJoint(new SphericalJoint(bodies[i-1],bodies[i],new Vector3d(360,2*i+21.55,420)));
			EntityBuilder.createColourEntity(bodies[i],i==0?"red":i==9?"lime":"cyan");
		}
	}
	
	static void piston() {
		RigidBody b1 = new RigidBody(new Vector3d(380,24,403),0,new BoxShape(2,2,2));
		RigidBody b2 = new RigidBody(new Vector3d(380,21.5,406.1),1,new BoxShape(1,2,1));
		RigidBody b3 = new RigidBody(new Vector3d(380,16.5,408.2),3,new BoxShape(1,6,1));
		RigidBody b4 = new RigidBody(new Vector3d(380,11.5,411.3),4,new BoxShape(2,2,2));
		RigidBody b5 = new RigidBody(new Vector3d(380,15.5,411.3),4,new BoxShape(2,2,2));
		world.addBody(b1);
		world.addBody(b2);
		world.addBody(b3);
		world.addBody(b4);
		world.addBody(b5);
		world.addJoint(new RevoluteJoint(b1,b2,new Vector3d(380,24,405.05),new Vector3d(0,0,1)).setMotor(2.2,5000));
		world.addJoint(new RevoluteJoint(b2,b3,new Vector3d(380,20.5,407.15),new Vector3d(0,0,1)));
		world.addJoint(new RevoluteJoint(b3,b4,new Vector3d(380,11.5,409.75),new Vector3d(0,0,1)));
		world.addJoint(new PrismaticJoint(b1,b4,new Vector3d(380,17.75,407.15),new Vector3d(0,1,0)));
		EntityBuilder.createColourEntity(b1,"cyan");
		EntityBuilder.createColourEntity(b2,"cyan");
		EntityBuilder.createColourEntity(b3,"cyan");
		EntityBuilder.createColourEntity(b4,"lime");
		EntityBuilder.createColourEntity(b5,"lime");
	}
	
	static void loop() {
		long time = System.nanoTime();
		while(!Display.isCloseRequested()) {
			double dt = System.nanoTime()-time;
			time = System.nanoTime();
			PhysicsEngine.step(dt);
			RenderHandler.renderWorld(world,camera,shader);
		}
		exit();
	}

	public static void exit() {
		Loader.cleanUp();
		shader.cleanUp();
		RenderHandler.cleanUp();
		Post.cleanUp();
		SSAORenderer.cleanUp();
		FlareRenderer.cleanUp();
		Display.destroy();
		System.exit(0);
	}
}