package sjmhrp.world;

import static sjmhrp.utils.linear.Vector3d.scale;
import static java.lang.Math.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import sjmhrp.core.Globals;
import sjmhrp.io.ConfigHandler;
import sjmhrp.physics.PhysicsEngine;
import sjmhrp.physics.collision.CollisionHandler;
import sjmhrp.physics.collision.Manifold;
import sjmhrp.physics.collision.RaycastResult;
import sjmhrp.physics.collision.broadphase.AABB;
import sjmhrp.physics.collision.broadphase.Tree;
import sjmhrp.physics.constraint.joints.Joint;
import sjmhrp.physics.dynamics.CollisionBody;
import sjmhrp.physics.dynamics.Island;
import sjmhrp.physics.dynamics.Ray;
import sjmhrp.physics.dynamics.RigidBody;
import sjmhrp.physics.dynamics.forces.Force;
import sjmhrp.physics.shapes.CollisionShape;
import sjmhrp.render.RenderHandler;
import sjmhrp.render.debug.DebugRenderer;
import sjmhrp.render.textures.TerrainTexture;
import sjmhrp.utils.GeometryUtils;
import sjmhrp.utils.LimitedStack;
import sjmhrp.utils.Profiler;
import sjmhrp.utils.VectorUtils;
import sjmhrp.utils.linear.Transform;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.world.sky.SkyDome;
import sjmhrp.world.sky.Sun;
import sjmhrp.world.terrain.ChunkTree;
import sjmhrp.world.terrain.ChunkTree.ChunkNode;
import sjmhrp.world.terrain.Octree;
import sjmhrp.world.terrain.generator.CompoundTerrainGenerator;
import sjmhrp.world.terrain.generator.TerrainGenerator;
import sjmhrp.world.terrain.generator.TerrainShape;

public class World implements Serializable{

	private static final long serialVersionUID = -3142869796981188644L;

	SkyDome sky;

	ArrayList<CollisionBody> bodies = new ArrayList<CollisionBody>();
	ArrayList<RigidBody> rigidBodies = new ArrayList<RigidBody>();
	transient HashMap<CollisionBody,Transform> predictedTransforms = new HashMap<CollisionBody,Transform>();
	transient LimitedStack<WorldState> states = new LimitedStack<WorldState>(Globals.MAX_REWIND_FRAMES);
	TerrainGenerator terrainGenerator;
	TerrainTexture terrainTexture;
	transient ChunkTree terrain;
	ArrayList<Force> forces = new ArrayList<Force>();
	ArrayList<Manifold> manifolds = new ArrayList<Manifold>();
	ArrayList<Joint> joints = new ArrayList<Joint>();
	transient ArrayList<Island> islands = new ArrayList<Island>();

	Vector3d center = new Vector3d();
	double mass;
	
	{PhysicsEngine.registerWorld(this);}
	
	public void generateSky() {
		sky = new SkyDome();
	}
	
	public void addSun() {
		if(!hasSky())return;
		Sun sun = new Sun();
		sun.getPosition().set(0,1,0);
		sun.getColour().set(1,1,1);
		sun.setSize(150);
		sun.setDayLength(1200);
		sun.setTexture("sun");
		sky.addBody(sun);
	}
	
	public void generateStars() {
		if(!hasSky())return;
		sky.loadStars("hip2");
	}
	

	public void setTerrain(TerrainGenerator gen, String texture) {
		setTerrain(gen,new TerrainTexture(texture));
	}
	
	public void setTerrain(TerrainGenerator gen, TerrainTexture texture) {
		setTerrainGenerator(gen);
		setTerrainTexture(texture);
	}
	
	public void setTerrainGenerator(TerrainGenerator gen) {
		terrainGenerator=gen;
	}
	
	public void setTerrainTexture(TerrainTexture texture) {
		terrainTexture=texture;
	}
	
	public void place(TerrainShape shape) {
		if(terrainGenerator==null)return;
		if(!(terrainGenerator instanceof CompoundTerrainGenerator))terrainGenerator=new CompoundTerrainGenerator(terrainGenerator);
		((CompoundTerrainGenerator)terrainGenerator).place(shape);
	}
	
	public void setTerrain(ChunkTree terrain) {
		this.terrain=terrain;
	}
	
	public ChunkTree generateTerrain() {
		Vector3d p = new Vector3d();
		int maxSize = 128;
		ChunkTree terrain=new ChunkTree();
		Vector3d cMin = VectorUtils.chunkMin(maxSize,p);
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				for(int k = -1; k <= 1; k++) {
					Vector3d min = new Vector3d(i,j,k).scale(maxSize).add(cMin);
					Vector3d centre = new Vector3d(maxSize/2).add(min);
					double d = GeometryUtils.distance(p,centre);
					if(d>maxSize) {
						ChunkNode n = new ChunkNode(min,maxSize,null,true);
						n.generate(terrainGenerator);
						terrain.addNode(n);
						continue;
					} else {
						int size = maxSize;
						Vector3d pos = GeometryUtils.closest(min,size,p);
						while(size>ChunkTree.VOXEL_COUNT) {
							Vector3d chunkMin = VectorUtils.chunkMin(size,pos);
							size/=2;
							Vector3d c = VectorUtils.chunkMin(size,pos);
							for(Vector3d v : Octree.CHILD_MIN_OFFSETS) {
								Vector3d m = scale(size,v).add(chunkMin);
								if(size>ChunkTree.VOXEL_COUNT&&m.equals(c))continue;
								ChunkNode n = new ChunkNode(m,size,null,true);
								n.generate(terrainGenerator);
								terrain.addNode(n);
							}
						}
					}
				}
			}
		}
		terrain.getAll().stream().filter(n->n.hasChanged()).forEach(n->n.addSeam(terrain.findSeamNodes(n),terrainGenerator,terrain));
		RenderHandler.addTask(()->terrain.createModels());
		return terrain;
	}
	
	
	
	public void setGravity(Vector3d center, double mass) {
		this.center=center;
		this.mass=mass;
	}

	public boolean hasSky() {
		return sky!=null;
	}
	
	public SkyDome getSky() {
		return sky;
	}
	
	public Vector3d getGravityCenter() {
		return center;
	}

	public double getMass() {
		return mass;
	}
	
	public Vector3d getGravity(RigidBody body) {
		Vector3d d = Vector3d.sub(center,body.getPosition());
		double r2 = d.lengthSquared();
		if(r2==0)return new Vector3d();
		return d.getUnit().scale(Globals.GRAVITATIONAL*mass*body.getMass()/r2);
	}
	
	public Vector3d getGravityDir(RigidBody body) {
		Vector3d d = Vector3d.sub(center,body.getPosition());
		return d.lengthSquared()==0?new Vector3d():d.normalize();
	}
	
	public ArrayList<CollisionBody> getCollisionBodies() {
		return bodies;
	}

	public ArrayList<RigidBody> getRigidBodies() {
		return rigidBodies;
	}

	public boolean hasTerrain() {
		return terrain!=null;
	}
	
	public ArrayList<ChunkNode> getTerrain() {
		return terrain.getAll();
	}
	
	public TerrainGenerator getTerrainGenerator() {
		return terrainGenerator;
	}
	
	public TerrainTexture getTerrainTexture() {
		return terrainTexture;
	}
	
	public void reload() {
		states = new LimitedStack<WorldState>(Globals.MAX_REWIND_FRAMES);
		predictedTransforms = new HashMap<CollisionBody,Transform>();
		islands = new ArrayList<Island>();
		terrainGenerator.reload();
		terrainTexture.reload();
		setTerrain(generateTerrain());
		sky.reloadStars();
	}
	
	public void clear() {
		bodies.clear();
		rigidBodies.clear();
		predictedTransforms.clear();
		terrain=null;
		forces.clear();
		manifolds.clear();
		joints.clear();
		states = new LimitedStack<WorldState>(Globals.MAX_REWIND_FRAMES);
	}

	Transform getPredictedTransform(CollisionBody b) {
		return b.isStaticTriMesh()?new Transform():predictedTransforms.get(b);
	}

	AABB getPredictedBoundingBox(CollisionBody b) {
		return b.getBoundingBox(getPredictedTransform(b));
	}

	public ArrayList<Manifold> getCollisions(CollisionBody b) {
		ArrayList<Manifold> result = new ArrayList<Manifold>();
		for(Manifold m : manifolds) {
			if(m.body1==b||m.body2==b)result.add(m);
		}
		return result;
	}
	
	public void addBody(CollisionBody b) {
		if(b.getBoundingBox()==null)return;
		bodies.add(b);
		b.setWorld(this);
		if(b instanceof RigidBody)rigidBodies.add((RigidBody)b);
	}

	public void addJoint(Joint j) {
		if((j.getBody1().isStatic()&&j.getBody2().isStatic())||!rigidBodies.contains(j.getBody1())&&!rigidBodies.contains(j.getBody2()))return;
		joints.add(j);
	}

	public void addForce(Force f) {
		forces.add(f);
		f.setWorld(this);
	}

	public void removeBody(CollisionBody b) {
		bodies.remove(b);
		for(Joint j : new ArrayList<Joint>(joints)) {
			if(j.getBody1()==b||j.getBody2()==b)removeJoint(j);
		}
	}
	
	public void removeJoint(Joint j) {
		joints.remove(j);
	}
	
	public void removeForce(Force f) {
		forces.remove(f);
	}
	
	public RaycastResult raycast(Ray ray) {
		return raycast(ray,null);
	}
	
	public RaycastResult raycast(Ray ray, CollisionBody exception) {
		Tree<CollisionBody> tree = new Tree<CollisionBody>();
		ArrayList<CollisionBody> bodies = new ArrayList<CollisionBody>(this.bodies);
		if(terrain!=null) {
			for(ChunkNode t : terrain.getAll()) {
				if(t.getMesh()!=null)bodies.add(t.getMesh());
				if(t.hasSeam()&&t.getSeamMesh()!=null)bodies.add(t.getSeamMesh());
			}
		}
		bodies.remove(exception);
		Profiler.start();
		for(CollisionBody b : bodies) {
			tree.add(b.getBoundingBox(b.isStaticTriMesh()?new Transform():b.getTransform()),b);
		}
		RaycastResult result = null;
		for(CollisionBody body : tree.query(ray)) {
			RaycastResult r = CollisionHandler.raycast(ray,(CollisionBody)body);
			if((result==null||r.distance()<result.distance())&&r.collides())result=r;
		}
		return result==null?new RaycastResult(ray):result;
	}

	public ArrayList<CollisionBody> getCollisions(CollisionShape shape, Transform transform) {
		ArrayList<CollisionBody> bs = new ArrayList<CollisionBody>();
		RigidBody body = new RigidBody(0,shape);
		body.setTransform(transform);
		AABB aabb = shape.getBoundingBox(transform);
		for(CollisionBody b : bodies) {
			if(GeometryUtils.intersects(b.getBoundingBox(),aabb)) {
				Manifold m = new Manifold(body,b,transform,b.getTransform());
				if(CollisionHandler.collide(m,transform,b.getTransform()))bs.add(b);
			}
		}
		return bs;
	}

	public void stepForward() {
		if(hasSky())sky.tick(PhysicsEngine.getTimeStep());
		states.push(new WorldState(this,PhysicsEngine.tick));
		applyForces();
		integrateVelocities();
		predictTransforms();
		detectCollisions();
		createIslands();
		prestep();
		solveVelocityConstraints();
		integratePositions();
		solvePositionConstraints();
		sleep();
	}

	public void stepBackward() {
		if(ConfigHandler.getBoolean("debug"))DebugRenderer.clearContacts();
		if(states.size()==0)return;
		WorldState s = states.peek();
		double d = System.nanoTime()-PhysicsEngine.getTimeStep()*1000000000;
		while(!states.empty()&&abs(s.getTimeStamp()-d)>abs(states.peek().getTimeStamp()-d)) {
			states.pop();
			s=states.peek();
		}
		double f = PhysicsEngine.getTimeStep()/s.getTimeStep();
		if(Math.random()>f*Globals.REWIND_SPEED) {
			return;
		}
		s=states.pop();
		for(RigidBody b : rigidBodies) {
			State state = s.get(b);
			if(state!=null)state.load(b);
			b.setSleeping(false);
		}
		joints = new ArrayList<Joint>(s.joints);
		forces = new ArrayList<Force>(s.forces);
		for(Joint j : joints) {
			j.resetImpulse();
		}
		if(hasSky())s.loadSky(sky);
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
			b.applyCentralForce(getGravity(b));
		}
	}
	
	void integrateVelocities() {
		for(RigidBody b : rigidBodies) {
			if(!b.isSleeping()||!b.canSleep())b.integrateVelocity();
		}
	}

	void predictTransforms() {
		predictedTransforms.clear();
		for(RigidBody b : rigidBodies) {
			if(!b.isStaticTriMesh())predictedTransforms.put(b,b.predictTransform());
		}
	}

	void createIslands() {
		ArrayList<Island> oldIslands = new ArrayList<Island>(islands);
		islands.clear();
		if(bodies.size()==0||(manifolds.size()==0&&joints.size()==0))return;
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
			if(b.isInIsland()||b.isStatic())continue;
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
		for(Island i : islands) {
			for(Island old : oldIslands) {
				if(i.equals(old)) {
					i.setSleepTimer(old.getSleepTimer());
					break;
				}
			}
		}
	}

	void sleep() {
		for(Island i : islands) {
			i.sleep();
		}
	}
	
	void integratePositions() {
		for(RigidBody b : rigidBodies) {
			if(!b.isSleeping()||!b.canSleep())b.integratePosition();
		}
	}

	void detectCollisions() {
		broadPhase();
		narrowPhase();
		Profiler.print();
	}

	void broadPhase() {
		Tree<CollisionBody> tree = new Tree<CollisionBody>();
		ArrayList<CollisionBody> bodies = new ArrayList<CollisionBody>(this.bodies);
		if(terrain!=null) {
			for(ChunkNode t : terrain.getAll()) {
				if(t.getMesh()!=null)bodies.add(t.getMesh());
				if(t.hasSeam()&&t.getSeamMesh()!=null)bodies.add(t.getSeamMesh());
			}
		}
		for(CollisionBody b : bodies) {
			tree.add(getPredictedBoundingBox(b),b);
		}
		if(!Globals.warmstart)manifolds.clear();
		ArrayList<CollisionBody> checked = new ArrayList<CollisionBody>();
		ArrayList<Manifold> possibleCollisions = new ArrayList<Manifold>(manifolds);
		for(CollisionBody b : bodies) {
			if(b.isStatic()||(b instanceof RigidBody&&((RigidBody)b).isSleeping()))continue;
			checked.add(b);
			for(CollisionBody o : b.isInfinite()?tree.getAll():tree.query(getPredictedBoundingBox(b))) {
				if(checked.contains(o))continue;
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
				manifolds.add(new Manifold(b,o,getPredictedTransform(b),getPredictedTransform(o)));
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

	void prestep() {
		for(Island i : islands) {
			if(!i.isSleeping())i.prestep();
		}
	}
	
	void solveVelocityConstraints() {
		for(Island i : islands) {
			if(!i.isSleeping())i.solveVelocityConstraints();
		}
	}

	void solvePositionConstraints() {
		for(Island i : islands) {
			if(!i.isSleeping())i.solvePositionConstraints();
		}
	}
}