package sjmhrp.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import sjmhrp.core.Globals;
import sjmhrp.debug.DebugRenderer;
import sjmhrp.linear.Quaternion;
import sjmhrp.linear.Transform;
import sjmhrp.linear.Vector3d;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.physics.collision.CollisionHandler;
import sjmhrp.physics.collision.Manifold;
import sjmhrp.physics.collision.broadphase.AABB;
import sjmhrp.physics.collision.broadphase.Tree;
import sjmhrp.physics.constraint.joints.Joint;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.physics.dynamics.Island;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.physics.dynamics.forces.Force;
import sjmhrp.sky.Sun;
import sjmhrp.terrain.DefaultHeightGenerator;
import sjmhrp.terrain.PerlinNoiseGenerator;
import sjmhrp.terrain.Terrain;
import sjmhrp.textures.TerrainTexture;
import sjmhrp.utils.LimitedStack;

public class World {

	Sun sun;
	double skyDomeSize = Sun.AU;
	
	ArrayList<CollisionBody> bodies = new ArrayList<CollisionBody>();
	ArrayList<RigidBody> rigidBodies = new ArrayList<RigidBody>();
	HashMap<CollisionBody,Transform> predictedTransforms = new HashMap<CollisionBody,Transform>();
	LimitedStack<WorldState> states;
	ArrayList<Terrain> heightField = new ArrayList<Terrain>();
	ArrayList<Force> forces = new ArrayList<Force>();
	ArrayList<Manifold> manifolds = new ArrayList<Manifold>();
	ArrayList<Joint> joints = new ArrayList<Joint>();
	ArrayList<Island> islands = new ArrayList<Island>();

	public int seed;
	public int size;
	Vector3d gravity = new Vector3d();

	public World(int size, int seed) {
		this.size = size;
		this.seed = seed;
		this.states = new LimitedStack<WorldState>(Globals.MAX_REWIND_FRAMES);
		PhysicsEngine.registerWorld(this);
	}

	public World(int size) {
		this(size,new Random().nextInt(1000000000));
	}

	public void createSun() {
		createSun(new Vector3d(0,Sun.AU,0),new Quaternion().rotate(new Vector3d(0.5*Math.PI,0,0),1));
	}
	
	public void createSun(Vector3d colour) {
		createSun(new Vector3d(0,Sun.AU,0),new Quaternion().rotate(new Vector3d(0.5*Math.PI,0,0),1),colour);
	}
	
	public void createSun(Vector3d pos, Quaternion orientation) {
		sun = new Sun(pos,orientation);
	}
	
	public void createSun(Vector3d pos, Quaternion orientation, Vector3d colour) {
		sun = new Sun(pos,orientation,colour);
	}
	
	public void setSkyDomeSize(double size) {
		skyDomeSize=size;
	}
	
	public void generateFlatTerrain(TerrainTexture terrainTexture) {
		for(int i = -size; i<=size;i++) {
			for(int j=-size;j<=size;j++) {
				Terrain t = new Terrain(i,j,terrainTexture,new DefaultHeightGenerator());
				heightField.add(t);
				addBody(t.getMesh());
			}
		}
	}

	public void generateRandomTerrain(TerrainTexture terrainTexture) {
		for(int i = -size; i<=size;i++) {
			for(int j=-size;j<=size;j++) {
				Terrain t = new Terrain(i,j,terrainTexture,new PerlinNoiseGenerator(i,j,seed));
				heightField.add(t);
				addBody(t.getMesh());
			}
		}
	}

	public void generateTerrain(String heightmap, TerrainTexture terrainTexture) {
		for(int i = -size; i<=size;i++) {
			for(int j=-size;j<=size;j++) {
				new Terrain(i,j,terrainTexture,heightmap);
			}
		}
	}

	public void setGravity(Vector3d gravity) {
		this.gravity = gravity;
		for(RigidBody b : rigidBodies) {
			b.setGravity(Vector3d.scale(b.getMass(),gravity));
		}
	}

	public Vector3d getGravity() {
		return gravity;
	}

	public ArrayList<CollisionBody> getCollisionBodies() {
		return bodies;
	}

	public ArrayList<RigidBody> getRigidBodies() {
		return rigidBodies;
	}

	public ArrayList<Terrain> getTerrain() {
		return heightField;
	}

	public boolean hasSun() {
		return sun!=null;
	}
	
	public Sun getSun() {
		return sun;
	}
	
	public double getSkyDomeSize() {
		return skyDomeSize;
	}
	
	public void clear() {
		bodies.clear();
		rigidBodies.clear();
		predictedTransforms.clear();
		heightField.clear();
		forces.clear();
		manifolds.clear();
		joints.clear();
		states = new LimitedStack<WorldState>(Globals.MAX_REWIND_FRAMES);
	}

	public double getHeight(double x, double z) {
		for(Terrain t : heightField) {
			if(x>=t.getX()&&x<t.getX()+Terrain.SIZE&&z>=t.getZ()&&z<t.getZ()+Terrain.SIZE) {
				return t.getHeight(x,z);
			}
		}
		return -1000;
	}

	Transform getPredictedTransform(CollisionBody b) {
		return b.isStaticTriMesh()?new Transform():predictedTransforms.get(b);
	}

	AABB getPredictedBoundingBox(CollisionBody b) {
		return b.getBoundingBox(getPredictedTransform(b));
	}

	public void addBody(CollisionBody b) {
		if(b.getBoundingBox()==null)return;
		bodies.add(b);
		b.setWorld(this);
		if(b instanceof RigidBody) {
			RigidBody r = (RigidBody)b;
			r.setGravity(Vector3d.scale(r.getMass(),gravity));
			rigidBodies.add(r);
		}
	}

	public void addJoint(Joint j) {
		if((j.getBody1().isStatic()&&j.getBody2().isStatic())||!rigidBodies.contains(j.getBody1())&&!rigidBodies.contains(j.getBody2()))return;
		joints.add(j);
	}

	public void addForce(Force f) {
		forces.add(f);
		f.setWorld(this);
	}

	public void stepForward() {
		sun.rotate(PhysicsEngine.getTimeStep());
		if(Globals.debug)DebugRenderer.clearContacts();
		states.push(new WorldState(this,PhysicsEngine.tick));
		applyForces();
		integrateVelocities();
		predictTransforms();
		detectCollisions();
		createIslands();
		solveVelocityConstraints();
		integratePositions();
		solvePositionConstraints();
	}

	public void stepBackward() {
		if(Globals.debug)DebugRenderer.clearContacts();
		if(states.size()==0)return;
		WorldState s = states.pop();
		for(RigidBody b : rigidBodies) {
			State state = s.get(b);
			if(state!=null)state.load(b);
		}
		s.loadSun(sun);
	}

	void applyForces() {
		applyGravity();
		for(Iterator<Force> i = forces.iterator(); i.hasNext();) {
			Force f = i.next();
			if(!f.update()) {
				i.remove();
			} else {
				f.applyForce();
			}
		}
	}

	void applyGravity() {
		for(RigidBody b : rigidBodies) {
			b.applyGravity();
		}
	}

	void integrateVelocities() {
		for(RigidBody b : rigidBodies) {
			b.integrateVelocity();
		}
	}

	void predictTransforms() {
		predictedTransforms.clear();
		for(RigidBody b : rigidBodies) {
			if(!b.isStaticTriMesh())predictedTransforms.put(b,b.predictTransform());
		}
	}

	void createIslands() {
		islands.clear();
		if(bodies.size()==0||(manifolds.size()==0&&joints.size()==0));
		for(RigidBody b : rigidBodies) {
			b.setInIsland(false);
		}
		for(Manifold m : manifolds) {
			m.setInIsland(false);
		}
		for(Joint j : joints) {
			j.setInIsland(false);
		}
		RigidBody[] bodiesToVisit = new RigidBody[rigidBodies.size()];
		for(RigidBody b : rigidBodies) {
			if(b.isInIsland()||b.isSleeping()||b.isStatic())continue;
			int n = 0;
			bodiesToVisit[n++]=b;
			b.setInIsland(true);
			islands.add(new Island());
			while(n>0) {
				n--;
				RigidBody body = bodiesToVisit[n];
				body.setSleeping(false);
				islands.get(islands.size()-1).addBody(body);
				if(body.isStatic())continue;
				for(Manifold m : manifolds) {
					if((m.body1!=body&&m.body2!=body)||m.isInIsland())continue;
					islands.get(islands.size()-1).addManifold(m);
					m.setInIsland(true);
					CollisionBody o = body==m.body1?m.body2:m.body1;
					if(!(o instanceof RigidBody))continue;
					RigidBody other = (RigidBody)o;
					if(other.isInIsland())continue;
					bodiesToVisit[n++] = other;
					other.setInIsland(true);
				}
				for(Joint j : joints) {
					if((j.getBody1()!=body&&j.getBody2()!=body)||j.isInIsland())continue;
					islands.get(islands.size()-1).addJoint(j);
					j.setInIsland(true);
					CollisionBody o = body==j.getBody1()?j.getBody2():j.getBody1();
					if(!(o instanceof RigidBody))continue;
					RigidBody other = (RigidBody)o;
					if(other.isInIsland())continue;
					bodiesToVisit[n++] = other;
					other.setInIsland(true);
				}
			}
			for(RigidBody body : islands.get(islands.size()-1).getBodies()) {
				if(body.isStatic())body.setInIsland(false);
			}
		}
	}

	void integratePositions() {
		for(RigidBody b : rigidBodies) {
			b.integratePosition();
		}
	}

	void detectCollisions() {
		broadPhase();
		narrowPhase();
	}

	void broadPhase() {
		Tree tree = new Tree();
		for(CollisionBody b : bodies) {
			tree.add(getPredictedBoundingBox(b),b);
		}
		if(!Globals.warmstart)manifolds.clear();
		ArrayList<CollisionBody> checked = new ArrayList<CollisionBody>();
		ArrayList<Manifold> possibleCollisions = new ArrayList<Manifold>(manifolds);
		for(CollisionBody b : bodies) {
			checked.add(b);
			for(Object o : b.isInfinite()?tree.getAll():tree.query(getPredictedBoundingBox(b))) {
				if(!(o instanceof CollisionBody&&!checked.contains(o))||(b.isStatic()&&((CollisionBody)o).isStatic()))continue;
				boolean f = false;
				for(Manifold m : possibleCollisions) {
					if((m.body1==b&&m.body2==o)||(m.body2==b&&m.body1==o)) {
						f = true;
						m.updateTransforms(getPredictedTransform(m.body1),getPredictedTransform(m.body2));
						possibleCollisions.remove(m);
						break;
					}
				}
				if(f)continue;
				manifolds.add(new Manifold(b,(RigidBody)o,getPredictedTransform(b),getPredictedTransform((RigidBody)o)));
			}
		}
	}

	void narrowPhase() {
		for(Joint j : joints) {
			j.updateTransforms(getPredictedTransform(j.getBody1()),getPredictedTransform(j.getBody2()));
		}
		for(Iterator<Manifold> i = manifolds.iterator();i.hasNext();) {
			Manifold m = i.next();
			if(!m.isPersistent()||!CollisionHandler.collide(m,getPredictedTransform(m.body1),getPredictedTransform(m.body2))){
				i.remove();
				continue;
			}
		}
		if(Globals.warmstart) {
			for(Iterator<Manifold> i = manifolds.iterator();i.hasNext();) {
				Manifold m = i.next();
				m.update();
				if(m.points.size()==0)i.remove();
			}
		}
	}

	void solveVelocityConstraints() {
		for(Island i : islands) {
			i.solveVelocityConstraints();
		}
	}

	void solvePositionConstraints() {
		for(Island i : islands) {
			i.solvePositionConstraints();
		}
	}
}