package sjmhrp.world.terrain;

import sjmhrp.utils.MatrixUtils;
import sjmhrp.utils.SVDUtils;
import sjmhrp.utils.linear.Matrix3d;
import sjmhrp.utils.linear.Vector3d;

public class QEF {

	static class QEFData {
        double ata_00, ata_01, ata_02, ata_11, ata_12, ata_22;
        double atb_x, atb_y, atb_z;
        double btb;
        double massPoint_x, massPoint_y, massPoint_z;
        int numPoints;
        
        public QEFData() {
        	clear();
        }
        
        public QEFData(double ata_00, double ata_01, double ata_02, double ata_11, double ata_12, double ata_22, double atb_x, double atb_y, double atb_z, double btb, double massPoint_x, double massPoint_y, double massPoint_z, int numPoints) {
        	set(ata_00,ata_01,ata_02,ata_11,ata_12,ata_22,atb_x,atb_y,atb_z,btb,massPoint_x, massPoint_y, massPoint_z,numPoints);
        }
        
        public QEFData(QEFData data) {
        	set(data);
        }
        
        public void set(QEFData data) {
           	this.ata_00=data.ata_00;
        	this.ata_01=data.ata_01;
        	this.ata_02=data.ata_02;
        	this.ata_11=data.ata_11;
        	this.ata_12=data.ata_12;
        	this.ata_22=data.ata_22;
        	this.atb_x=data.atb_x;
        	this.atb_y=data.atb_y;
        	this.atb_z=data.atb_z;
        	this.btb=data.btb;
        	this.massPoint_x=data.massPoint_x;
        	this.massPoint_y=data.massPoint_y;
        	this.massPoint_z=data.massPoint_z;
        	this.numPoints=data.numPoints;
        }
        
        public void set(double ata_00, double ata_01, double ata_02, double ata_11, double ata_12, double ata_22, double atb_x, double atb_y, double atb_z, double btb, double massPoint_x, double massPoint_y, double massPoint_z, int numPoints) {
        	this.ata_00=ata_00;
        	this.ata_01=ata_01;
        	this.ata_02=ata_02;
        	this.ata_11=ata_11;
        	this.ata_12=ata_12;
        	this.ata_22=ata_22;
        	this.atb_x=atb_x;
        	this.atb_y=atb_y;
        	this.atb_z=atb_z;
        	this.btb=btb;
        	this.massPoint_x=massPoint_x;
        	this.massPoint_y=massPoint_y;
        	this.massPoint_z=massPoint_z;
        	this.numPoints=numPoints;
        }
        
        public void add(QEFData data) {
        	this.ata_00+=data.ata_00;
        	this.ata_01+=data.ata_01;
        	this.ata_02+=data.ata_02;
        	this.ata_11+=data.ata_11;
        	this.ata_12+=data.ata_12;
        	this.ata_22+=data.ata_22;
        	this.atb_x+=data.atb_x;
        	this.atb_y+=data.atb_y;
        	this.atb_z+=data.atb_z;
        	this.btb+=data.btb;
        	this.massPoint_x+=data.massPoint_x;
        	this.massPoint_y+=data.massPoint_y;
        	this.massPoint_z+=data.massPoint_z;
        	this.numPoints+=data.numPoints;
        }
        
        public void clear() {
        	set(0,0,0,0,0,0,0,0,0,0,0,0,0,0);
        }
	}
	
	static class QEFSolver {
		QEFData data = new QEFData();
		Matrix3d ata = new Matrix3d();
		Vector3d atb = new Vector3d();
		Vector3d massPoint = new Vector3d();
		Vector3d x = new Vector3d();
		boolean hasSolution = false;
		
		public void add(double px, double py, double pz, double nx, double ny, double nz) {
			hasSolution=false;
			Vector3d n = new Vector3d(nx,ny,nz).normalize();
			data.ata_00+=n.x*n.x;
			data.ata_01+=n.x*n.y;
			data.ata_02+=n.x*n.z;
			data.ata_11+=n.y*n.y;
			data.ata_12+=n.y*n.z;
			data.ata_22+=n.z*n.z;
			double dot = n.dot(new Vector3d(px,py,pz));
			data.atb_x+=dot*n.x;
			data.atb_y+=dot*n.y;
			data.atb_z+=dot*n.z;
			data.btb+=dot*dot;
			data.massPoint_x+=px;
			data.massPoint_y+=py;
			data.massPoint_z+=pz;
			data.numPoints++;
		}
		
		public void add(Vector3d p, Vector3d n) {
			add(p.x,p.y,p.z,n.x,n.y,n.z);
		}
		
		public void add(QEFData rhs) {
			hasSolution=false;
			data.add(rhs);
		}
		
		public QEFData getData() {
			return data;
		}
		
		double getError() {
			if(!hasSolution)throw new IllegalStateException("No Solution");
			return getError(x);
		}
		
		double getError(Vector3d pos) {
			if(!hasSolution) {
				setAta();
				setAtb();
			}
			Vector3d atax = MatrixUtils.transformSymmetric(ata,pos);
			return pos.dot(atax)-2*pos.dot(atb)+data.btb;
		}
		
		public void reset() {
			hasSolution=false;
			data.clear();
		}
		
		public void setAta() {
			ata.setSymmetric(data.ata_00,data.ata_01,data.ata_02,data.ata_11,data.ata_12,data.ata_22);
		}
		
		public void setAtb() {
			atb.set(data.atb_x,data.atb_y,data.atb_z);
		}
		
		double solve(Vector3d outx, double svd_tol, int svd_sweeps, double pinv_tol) {
			if(data.numPoints==0)throw new IllegalArgumentException("No Points");
			massPoint.set(data.massPoint_x,data.massPoint_y,data.massPoint_z);
			massPoint.scale(1d/data.numPoints);
			setAta();
			setAtb();
			atb.sub(MatrixUtils.transformSymmetric(ata,massPoint));
			x.zero();
			double result = SVDUtils.solveSymmetric(ata,atb,x,svd_tol,svd_sweeps,pinv_tol);
			x.add(massPoint);
			setAtb();
			outx.set(x);
			hasSolution=true;
			return result;
		}
	}
}