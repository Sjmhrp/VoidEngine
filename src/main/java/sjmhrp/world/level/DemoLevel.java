package sjmhrp.world.level;

import java.util.ArrayList;

import sjmhrp.core.Globals;
import sjmhrp.factory.AnimatedEntityFactory;
import sjmhrp.factory.EntityFactory;
import sjmhrp.io.OBJHandler;
import sjmhrp.physics.constraint.joints.PrismaticJoint;
import sjmhrp.physics.constraint.joints.RevoluteJoint;
import sjmhrp.physics.constraint.joints.SphericalJoint;
import sjmhrp.physics.dynamics.MultiRigidBody;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.physics.shapes.BoxShape;
import sjmhrp.physics.shapes.CapsuleShape;
import sjmhrp.physics.shapes.ConeShape;
import sjmhrp.physics.shapes.ConvexHullShape;
import sjmhrp.physics.shapes.CylinderShape;
import sjmhrp.physics.shapes.SphereShape;
import sjmhrp.render.RenderHandler;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.world.World;
import sjmhrp.world.terrain.generator.PerlinNoiseGenerator;
import sjmhrp.world.terrain.generator.TerrainShape.SphereTerrain;

public class DemoLevel extends Level{

	static double density = 1000;
	static Vector3d position = new Vector3d(25,0,-61);
	
	World create() {
		createWorld();
		createLevel();
		return world;
	}
	
	void createWorld() {
		world = new World();
		world.setGravity(new Vector3d(0,-Globals.EARTH_RADIUS,0),Globals.EARTH_MASS);
		world.generateSky();
		world.addSun();
		world.generateStars();
		world.setTerrain(new PerlinNoiseGenerator(1318537895),"grass");
		world.place(new SphereTerrain(false,new Vector3d(45,-10,-41),10));
		world.setTerrain(world.generateTerrain());
	}
	
	void createLevel() {
//		new Light(new Vector3d(0,20,10).add(position),new Vector3d(1,1,1),new Vector3d(1.1,0.0002,0.00004));
		torus();
		stack();
		vehicle();
		cone();
		bounce();
		revolute();
		barrel();
		string();
		piston();
		player();
	}
	
	void torus() {
		RigidBody torus = new RigidBody(new Vector3d(-8,21,-5).add(position),1000,OBJHandler.parseCompoundShape("TorusCol"));
		RigidBody sphere = new RigidBody(new Vector3d(-8,21,-10).add(position),density*new SphereShape(0.5).getVolume(),new SphereShape(0.5));
		world.addBody(torus);
		world.addBody(sphere);
		sphere.setLinearVel(0,0,20);
		RenderHandler.addTask(new EntityFactory(torus).setModel("Torus").setColour("lime")::build);
		RenderHandler.addTask(new EntityFactory(sphere).setColour("cyan")::build);
	}
	
	void stack() {
		BoxShape boxShape = new BoxShape(2,2,2);
		for(int i = 0; i < 10; i++) {
			RigidBody box = new RigidBody(new Vector3d(10,i*5+21,-3).add(position),density*boxShape.getVolume(),boxShape);
			world.addBody(box);
			RenderHandler.addTask(new EntityFactory(box).setColour("lime")::build);
		}
	}
	
	void vehicle() {
		Vector3d offset = new Vector3d(0,21,-20).add(position);
		RigidBody base = new RigidBody(new Vector3d(offset),density*3.2,new BoxShape(4,0.4,2));
		MultiRigidBody body = new MultiRigidBody(base,world);
		CylinderShape wheelShape = new CylinderShape(1,0.5);
		RigidBody wheel1 = new RigidBody(new Vector3d(-2.5,0,2.35).add(offset),density*wheelShape.getVolume(),wheelShape);
		RigidBody wheel2 = new RigidBody(new Vector3d(2.5,0,2.35).add(offset),density*wheelShape.getVolume(),wheelShape);
		RigidBody wheel3 = new RigidBody(new Vector3d(-2.5,0,-2.35).add(offset),density*wheelShape.getVolume(),wheelShape);
		RigidBody wheel4 = new RigidBody(new Vector3d(2.5,0,-2.35).add(offset),density*wheelShape.getVolume(),wheelShape);
		wheel1.rotate(Math.PI*0.5,0,0);
		wheel2.rotate(Math.PI*0.5,0,0);
		wheel3.rotate(Math.PI*0.5,0,0);
		wheel4.rotate(Math.PI*0.5,0,0);
		wheel1.setFriction(5);
		wheel2.setFriction(5);
		wheel3.setFriction(5);
		wheel4.setFriction(5);
		body.hinge(wheel1,new Vector3d(0,0,1),new Vector3d(-2.5,0,2.05).add(offset));
		body.hinge(wheel2,new Vector3d(0,0,1),new Vector3d(2.5,0,2.05).add(offset));
		body.hinge(wheel3,new Vector3d(0,0,1),new Vector3d(-2.5,0,-2.05).add(offset));
		body.hinge(wheel4,new Vector3d(0,0,1),new Vector3d(2.5,0,-2.05).add(offset));
		body.get(base).setLinearVel(-10,0,0);
		body.weld(wheel1,wheel3,new Vector3d(-2.5,0,0).add(offset));
		body.weld(wheel2,wheel4,new Vector3d(2.5,0,0).add(offset));
		RenderHandler.addTask(new EntityFactory(base).setColour("cyan")::build);
		RenderHandler.addTask(new EntityFactory(wheel1).setColour("red")::build);
		RenderHandler.addTask(new EntityFactory(wheel2).setColour("red")::build);
		RenderHandler.addTask(new EntityFactory(wheel3).setColour("red")::build);
		RenderHandler.addTask(new EntityFactory(wheel4).setColour("red")::build);
	}
	
	void cone() {
		ConeShape shape = new ConeShape(4,8);
		RigidBody cone = new RigidBody(new Vector3d(-6,15,3).add(position),density*shape.getVolume(),shape);
		cone.rotate(0,0,Math.PI);
		world.addBody(cone);
		RenderHandler.addTask(new EntityFactory(cone).setColour("red")::build);
	}
	
	void bounce() {
		SphereShape sphereShape = new SphereShape(2);
		for(int i = 0; i < 10; i++) {
			RigidBody sphere = new RigidBody(new Vector3d(-25,i*4+20,-32).add(position),density*sphereShape.getVolume(),sphereShape);
			sphere.setRestitution(1);
			world.addBody(sphere);
			RenderHandler.addTask(new EntityFactory(sphere).setColour("red")::build);
		}
	}

	void revolute() {
		RigidBody b1 = new RigidBody(new Vector3d(10,24,3).add(position),0,new BoxShape(2,2,2));
		RigidBody b2 = new RigidBody(new Vector3d(10,21.5,6.1).add(position),density*32,new BoxShape(1,4,1));
		RigidBody b3 = new RigidBody(new Vector3d(10,16.5,8.2).add(position),density*32,new BoxShape(1,4,1));
		world.addBody(b1);
		world.addBody(b2);
		world.addBody(b3);
		world.addJoint(new RevoluteJoint(b1,b2,new Vector3d(10,24,5.05).add(position),new Vector3d(0,0,1)).setMotor(2,50000*density));
		world.addJoint(new RevoluteJoint(b2,b3,new Vector3d(10,19.5,7.15).add(position),new Vector3d(0,0,1)));
		RenderHandler.addTask(new EntityFactory(b1).setColour("cyan")::build);
		RenderHandler.addTask(new EntityFactory(b2).setColour("cyan")::build);
		RenderHandler.addTask(new EntityFactory(b3).setColour("cyan")::build);
	}

	void barrel() {
		RigidBody barrel = new RigidBody(new Vector3d(10,35,4).add(position),5*density,new ConvexHullShape(OBJHandler.parseVertices("barrel")));
		world.addBody(barrel);
		RenderHandler.addTask(new EntityFactory(barrel).setModel("barrel").setTexture("barrel").setNormalMap("barrelNormal").setSpecularMap("barrelS")::build);
	}

	void string() {
		CapsuleShape b = new CapsuleShape(0.2,1.6);
		RigidBody[] bodies = new RigidBody[10];
		for(int i = 0; i < 10; i++) {
			bodies[i] = new RigidBody(new Vector3d(-40,2*i+22.1,20).add(position),i==9?0:density*b.getVolume(),b);
			bodies[i].setFriction(0);
			if(i==5)bodies[i].setLinearVel(-10,2,1);
			world.addBody(bodies[i]);
			if(i!=0)world.addJoint(new SphericalJoint(bodies[i-1],bodies[i],new Vector3d(-40,2*i+21.55,20).add(position)));
		}
		ArrayList<EntityFactory> f = new ArrayList<EntityFactory>();
		for(int i = 0; i < 10; i++) {
			f.add(new EntityFactory(bodies[i]).setColour(i==0?"red":i==9?"lime":"cyan"));
		}
		RenderHandler.addTask(()->f.forEach(EntityFactory::build));
	}
	
	void piston() {
		RigidBody b1 = new RigidBody(new Vector3d(-20,24,3).add(position),0,new BoxShape(2,2,2));
		RigidBody b2 = new RigidBody(new Vector3d(-20,21.5,6.1).add(position),density*16,new BoxShape(1,2,1));
		RigidBody b3 = new RigidBody(new Vector3d(-20,16.5,8.2).add(position),density*48,new BoxShape(1,6,1));
		RigidBody b4 = new RigidBody(new Vector3d(-20,11.5,11.3).add(position),density*64,new BoxShape(2,2,2));
		RigidBody b5 = new RigidBody(new Vector3d(-20,15.5,11.3).add(position),density*64,new BoxShape(2,2,2));
		world.addBody(b1);
		world.addBody(b2);
		world.addBody(b3);
		world.addBody(b4);
		world.addBody(b5);
		world.addJoint(new RevoluteJoint(b1,b2,new Vector3d(-20,24,5.05).add(position),new Vector3d(0,0,1)).setMotor(2.2,500000*density));
		world.addJoint(new RevoluteJoint(b2,b3,new Vector3d(-20,20.5,7.15).add(position),new Vector3d(0,0,1)));
		world.addJoint(new RevoluteJoint(b3,b4,new Vector3d(-20,11.5,9.75).add(position),new Vector3d(0,0,1)));
		world.addJoint(new PrismaticJoint(b1,b4,new Vector3d(-20,17.75,7.15).add(position),new Vector3d(0,1,0)));
		RenderHandler.addTask(new EntityFactory(b1).setColour("cyan")::build);
		RenderHandler.addTask(new EntityFactory(b2).setColour("cyan")::build);
		RenderHandler.addTask(new EntityFactory(b3).setColour("cyan")::build);
		RenderHandler.addTask(new EntityFactory(b4).setColour("lime")::build);
		RenderHandler.addTask(new EntityFactory(b5).setColour("lime")::build);
	}
	
	void player() {
		RenderHandler.addTask(new AnimatedEntityFactory("player").addAnimation("player").setTexture("player").setPosition(new Vector3d(position))::build);	
	}
}