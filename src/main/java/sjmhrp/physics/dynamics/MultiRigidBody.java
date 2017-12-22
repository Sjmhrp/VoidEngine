package sjmhrp.physics.dynamics;

import java.io.Serializable;
import java.util.ArrayList;

import sjmhrp.physics.constraint.joints.Joint;
import sjmhrp.physics.constraint.joints.RevoluteJoint;
import sjmhrp.physics.constraint.joints.WeldJoint;
import sjmhrp.utils.GeometryUtils;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.world.World;

public class MultiRigidBody implements Serializable {

	private static final long serialVersionUID = -4808083811700577359L;
	
	MultiRigidBodyElement base;
	World world;
	ArrayList<MultiRigidBodyElement> elements = new ArrayList<MultiRigidBodyElement>();
	ArrayList<Joint> joints = new ArrayList<Joint>();
	
	public MultiRigidBody(RigidBody b, World world) {
		this.world=world;
		base = new MultiRigidBodyElement(b);
		base.parent=this;
		elements.add(base);
		world.addBody(base);
	}
	
	public MultiRigidBodyElement add(RigidBody b) {
		MultiRigidBodyElement body = get(b);
		if(body!=null)return body;
		body = new MultiRigidBodyElement(b);
		body.parent=this;
		elements.add(body);
		world.addBody(body);
		return body;
	}
	
	public WeldJoint weld(RigidBody b) {
		return weld(b,base);
	}
	
	public WeldJoint weld(RigidBody b1, Vector3d pos) {
		return weld(b1,base,pos);
	}
	
	public WeldJoint weld(RigidBody b1, RigidBody b2) {
		return weld(b1,b2,GeometryUtils.mid(b1.getPosition(),b2.getPosition()));
	}
	
	public WeldJoint weld(RigidBody b1, RigidBody b2, Vector3d pos) {
		MultiRigidBodyElement e = b1 instanceof MultiRigidBodyElement?(MultiRigidBodyElement)b1:add(b1);
		MultiRigidBodyElement base = b2 instanceof MultiRigidBodyElement?(MultiRigidBodyElement)b2:add(b2);
		WeldJoint w = new WeldJoint(base,e,GeometryUtils.mid(e.getPosition(),base.getPosition()));
		joints.add(w);
		world.addJoint(w);
		return w;
	}
	
	public RevoluteJoint hinge(RigidBody b, Vector3d dir) {
		return hinge(b,base,dir);
	}
	
	public RevoluteJoint hinge(RigidBody b, Vector3d dir, Vector3d pos) {
		return hinge(b,base,dir,pos);
	}
	
	public RevoluteJoint hinge(RigidBody b1, RigidBody b2, Vector3d dir) {
		return hinge(b1,b2,dir,GeometryUtils.mid(b1.getPosition(),b2.getPosition()));
	}
	
	public RevoluteJoint hinge(RigidBody b1, RigidBody b2, Vector3d dir, Vector3d pos) {
		MultiRigidBodyElement e = b1 instanceof MultiRigidBodyElement?(MultiRigidBodyElement)b1:add(b1);
		MultiRigidBodyElement base = b2 instanceof MultiRigidBodyElement?(MultiRigidBodyElement)b2:add(b2);
		RevoluteJoint r = new RevoluteJoint(base,e,pos,dir);
		joints.add(r);
		world.addJoint(r);
		return r;
	}
	
	public void addPosition(Vector3d pos) {
		for(MultiRigidBodyElement e : elements) {
			e.position.add(pos);
		}
	}
	
	public void addLinearVelociy(Vector3d v) {
		for(MultiRigidBodyElement e : elements) {
			e.velocity.add(v);
		}
	}
	
	public void setLinearVelociy(Vector3d v) {
		for(MultiRigidBodyElement e : elements) {
			e.velocity.set(v);
		}
	}
	
	public void stop() {
		for(MultiRigidBodyElement e : elements) {
			e.stop();
		}
	}
	
	public void setSleeping(boolean b) {
		for(MultiRigidBodyElement e : elements) {
			e.setSleeping(b);
		}
	}
	
	public RigidBody getBase() {
		return base;
	}

	public MultiRigidBodyElement get(RigidBody body) {
		for(MultiRigidBodyElement e : elements) {
			if(e.original==body)return e;
		}
		return null;
	}
	
	public ArrayList<MultiRigidBodyElement> getElements() {
		return elements;
	}

	public ArrayList<Joint> getJoints() {
		return joints;
	}

	public class MultiRigidBodyElement extends RigidBody {

		private static final long serialVersionUID = 6362685549023494195L;
		
		MultiRigidBody parent;
		RigidBody original;
		
		public MultiRigidBodyElement(RigidBody body) {
			super(body.getPosition(),body.getOrientation(),body.getMass(),body.getCollisionShape());
			original=body;
		}
		
		public void setParentPosition(Vector3d pos) {
			parent.addPosition(Vector3d.sub(pos,parent.base.position));
		}
		
		public void addParentPosition(Vector3d pos) {
			parent.addPosition(pos);
		}
		
		public void setParentSleeping(boolean b) {
			parent.setSleeping(b);
		}
		
		public MultiRigidBody getParent() {
			return parent;
		}
		
		public ArrayList<MultiRigidBodyElement> getElements() {
			return parent.getElements();
		}
	}
}