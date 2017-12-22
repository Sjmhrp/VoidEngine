package sjmhrp.world.level;

import static java.lang.Math.PI;
import static java.lang.Math.sqrt;

import sjmhrp.factory.EntityFactory;
import sjmhrp.physics.constraint.joints.RevoluteJoint;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.physics.shapes.BoxShape;
import sjmhrp.render.RenderHandler;
import sjmhrp.utils.linear.Quaternion;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.world.World;
import sjmhrp.world.terrain.generator.FlatTerrainGenerator;

public class Bernoulli extends Level {

	World create() {
		createWorld();
		createLevel();
		return world;
	}

	void createWorld() {
		world = new World();
		world.generateSky();
		world.addSun();
		world.generateStars();
		world.setTerrain(new FlatTerrainGenerator(),"background");
		world.setTerrain(world.generateTerrain());
	}
	
	void createLevel() {

		Vector3d f1 = new Vector3d(-10,25,-20);
		Vector3d f2 = new Vector3d(10,25,-20);
		
		BoxShape anc = new BoxShape(1,1,1);
		BoxShape r1 = new BoxShape(5*sqrt(2)+1,1,1);
		BoxShape r2 = new BoxShape(1,11,1);
		RigidBody anchor1 = new RigidBody(new Vector3d(f1),0,anc);
		RigidBody anchor2 = new RigidBody(new Vector3d(f2),0,anc);
		RigidBody rod1 = new RigidBody(new Vector3d(-5,20,-17.9),new Quaternion().setAxis(new Vector3d(0,0,1),-PI/4),1000,r1);
		RigidBody rod2 = new RigidBody(new Vector3d(5,30,-17.9),new Quaternion().setAxis(new Vector3d(0,0,1),-PI/4),1000,r1);
		RigidBody rod3 = new RigidBody(new Vector3d(0,25,-15.8),1000,r2);
		
		world.addBody(anchor1);
		world.addBody(anchor2);
		world.addBody(rod1);
		world.addBody(rod2);
		world.addBody(rod3);
		
		world.addJoint(new RevoluteJoint(anchor1,rod1,new Vector3d(-10,25,-18.95),new Vector3d(0,0,1)).setMotor(0.5,10000));
		world.addJoint(new RevoluteJoint(anchor2,rod2,new Vector3d(10,25,-18.95),new Vector3d(0,0,1)));
		world.addJoint(new RevoluteJoint(rod1,rod3,new Vector3d(0,15,-16.85),new Vector3d(0,0,1)));
		world.addJoint(new RevoluteJoint(rod2,rod3,new Vector3d(0,35,-16.85),new Vector3d(0,0,1)));
		
		RenderHandler.addTask(new EntityFactory(anchor1).setColour("cyan")::build);
		RenderHandler.addTask(new EntityFactory(anchor2).setColour("cyan")::build);
		RenderHandler.addTask(new EntityFactory(rod1).setColour("cyan")::build);
		RenderHandler.addTask(new EntityFactory(rod2).setColour("cyan")::build);
		RenderHandler.addTask(new EntityFactory(rod3).setColour("cyan").addTrail(10,new Vector3d(1,1,0),new Vector3d(0,0,1.05))::build);
	}
}