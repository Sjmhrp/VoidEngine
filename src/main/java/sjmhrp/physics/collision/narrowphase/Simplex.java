package sjmhrp.physics.collision.narrowphase;

import sjmhrp.linear.Vector3d;

public class Simplex {
	private final Vector3d[] points = {new Vector3d(), new Vector3d(), new Vector3d(), new Vector3d()};
	private final double[] pointsLengthSquared = new double[4];
	private double maxLengthSquare;
	private final Vector3d[] supportA = {new Vector3d(), new Vector3d(), new Vector3d(), new Vector3d()};
	private final Vector3d[] supportB = {new Vector3d(), new Vector3d(), new Vector3d(), new Vector3d()};
	private final Vector3d[][] difLength = {{new Vector3d(), new Vector3d(), new Vector3d(), new Vector3d()},
			{new Vector3d(), new Vector3d(), new Vector3d(), new Vector3d()},
			{new Vector3d(), new Vector3d(), new Vector3d(), new Vector3d()},
			{new Vector3d(), new Vector3d(), new Vector3d(), new Vector3d()}};
	private final double[][] det = new double[16][4];
	private final double[][] normSquare =  new double[4][4];
	private int bitsCurrentSimplex = 0x0;
	private int lastFound;
	private int lastFoundBit;
	private int allBits = 0x0;
	
	public boolean isFull() {
		return bitsCurrentSimplex==0xF;
	}

	public boolean isEmpty() {
		return bitsCurrentSimplex==0x0;
	}
	
	public double getMaxLengthSquared() {
		return maxLengthSquare;
	}

	public void addPoint(Vector3d point, Vector3d a, Vector3d b) {
		if(isFull())throw new IllegalStateException("Simplex Is Full");
		lastFound = 0;
		lastFoundBit = 0x1;
		while(overlap(bitsCurrentSimplex,lastFoundBit)) {
			lastFound++;
			lastFoundBit<<=1;
		}
		points[lastFound].set(point);
		pointsLengthSquared[lastFound]=point.lengthSquared();
		allBits = bitsCurrentSimplex | lastFoundBit;
		updateCache();
		computeDeterminants();
		supportA[lastFound].set(a);
		supportB[lastFound].set(b);
	}

	private void updateCache() {
		for(int i = 0, bit = 0x1; i < 4; i++, bit<<=1) {
			if(overlap(bitsCurrentSimplex,bit)) {
				difLength[i][lastFound].set(Vector3d.sub(points[i],points[lastFound]));
				difLength[lastFound][i].set(difLength[i][lastFound].getNegative());
				normSquare[i][lastFound] = normSquare[lastFound][i] = difLength[i][lastFound].lengthSquared();	
			}
		}
	}

	private void computeDeterminants() {
		det[lastFoundBit][lastFound] = 1;
		if(!isEmpty()) {
			for(int i = 0, bitI = 0x1; i < 4; i++, bitI<<=1) {
				if(overlap(bitsCurrentSimplex,bitI)) {
					final int bit2 = bitI | lastFoundBit;
					det[bit2][i] = difLength[lastFound][i].dot(points[lastFound]);
					det[bit2][lastFound] = difLength[i][lastFound].dot(points[i]);
					for(int j = 0, bitJ = 0x1; j < i; j++, bitJ<<=1) {
						if(overlap(bitsCurrentSimplex,bitJ)) {
							int k;
							final int bit3 = bitJ | bit2;
							k = normSquare[i][j] < normSquare[lastFound][j] ? i : lastFound; 
                            det[bit3][j] = det[bit2][i] * difLength[k][j].dot(points[i])
                                    + det[bit2][lastFound] * difLength[k][j].dot(points[lastFound]);
                            k = normSquare[j][i] < normSquare[lastFound][i] ? j : lastFound; 
                            det[bit3][i] = det[bitJ | lastFoundBit][j] * difLength[k][i].dot(points[j]) 
                                    + det[bitJ | lastFoundBit][lastFound] * difLength[k][i].dot(points[lastFound]); 
                            k = normSquare[i][lastFound] < normSquare[j][lastFound] ? i : j;
                            det[bit3][lastFound] = det[bitJ | bitI][j] * difLength[k][lastFound].dot(points[j]) 
                                    + det[bitJ | bitI][i] * difLength[k][lastFound].dot(points[i]);
						}
					}
				}
			}
			if(allBits==0xF) {
				int k;
				k = normSquare[1][0] < normSquare[2][0] ? 
                        (normSquare[1][0] < normSquare[3][0] ? 1 : 3) : 
                        (normSquare[2][0] < normSquare[3][0] ? 2 : 3); 
                det[0xF][0] = det[0xe][1] * difLength[k][0].dot(points[1])
                        + det[0xe][2] * difLength[k][0].dot(points[2]) 
                        + det[0xe][3] * difLength[k][0].dot(points[3]);
                k = normSquare[0][1] < normSquare[2][1] ? 
                        (normSquare[0][1] < normSquare[3][1] ? 0 : 3) : 
                        (normSquare[2][1] < normSquare[3][1] ? 2 : 3); 
                det[0xF][1] = det[0xd][0] * difLength[k][1].dot(points[0]) 
                        + det[0xd][2] * difLength[k][1].dot(points[2]) 
                        + det[0xd][3] * difLength[k][1].dot(points[3]); 
                k = normSquare[0][2] < normSquare[1][2] ? 
                        (normSquare[0][2] < normSquare[3][2] ? 0 : 3) : 
                        (normSquare[1][2] < normSquare[3][2] ? 1 : 3);
                det[0xF][2] = det[0xb][0] * difLength[k][2].dot(points[0]) 
                        + det[0xb][1] * difLength[k][2].dot(points[1]) 
                        + det[0xb][3] * difLength[k][2].dot(points[3]); 
                k = normSquare[0][3] < normSquare[1][3] ? 
                        (normSquare[0][3] < normSquare[2][3] ? 0 : 2) : 
                        (normSquare[1][3] < normSquare[2][3] ? 1 : 2);
                det[0xF][3] = det[0x7][0] * difLength[k][3].dot(points[0]) 
                        + det[0x7][1] * difLength[k][3].dot(points[1])
                        + det[0x7][2] * difLength[k][3].dot(points[2]);
			}
		}
	}

	public boolean isPointInSimplex(Vector3d v) {
		for(int i = 0, bit = 0x1; i < 4; i++, bit<<=1) {
			if(overlap(allBits,bit)&&v.equals(points[i]))return true;
		}
		return false;
	}

	public int getSimplex(Vector3d[] suppPointsA, Vector3d[] suppPointsB, Vector3d[] points) {
		int nbVertices = 0;
		for (int i = 0, bit = 0x1; i < 4; i++, bit <<= 1) { 
			if (overlap(bitsCurrentSimplex, bit)) { 
				suppPointsA[nbVertices] = new Vector3d(supportA[nbVertices]); 
				suppPointsB[nbVertices] = new Vector3d(supportB[nbVertices]); 
				points[nbVertices] = new Vector3d(this.points[nbVertices]); 
				nbVertices++; 
			}
		}
		return nbVertices;
	}

	private boolean isProperSubset(int subset) {
		for(int i = 0, bit = 0x1; i < 4; i++, bit<<=1) {
			if(overlap(subset,bit)&&det[subset][i]<=0)return false;
		}
		return true;
	}

	public boolean isAffinelyDependent() {
		double sum = 0;
		for(int i = 0, bit = 0x1; i < 4; i++, bit<<=1) {
			if(overlap(allBits,bit))sum+=det[allBits][i];
		}
		return sum<=0;
	}

	private boolean isValidSubset(int subset) {
		for(int i = 0, bit = 0x1; i < 4; i++, bit<<=1) {
			if(overlap(allBits,bit)) {
				if(overlap(subset,bit)) {
					if(det[subset][i]<=0)return false;
				} else if(det[subset|bit][i]>0) {
					return false;
				}
			}
		}
		return true;
	}

	public void closestPoints(Vector3d a, Vector3d b) {
		double deltaX = 0;
		a.set(0,0,0);
		b.set(0,0,0);
		for(int i = 0, bit = 0x1; i < 4; i++, bit<<=1) {
			if(overlap(bitsCurrentSimplex,bit)) {
				deltaX+=det[bitsCurrentSimplex][i];
				a.add(Vector3d.scale(det[bitsCurrentSimplex][i],supportA[i]));
				b.add(Vector3d.scale(det[bitsCurrentSimplex][i],supportB[i]));
			}
		}
		if(deltaX<=0)throw new IllegalStateException("dX must be greater than 0");
		final double f = 1f/deltaX;
		a.scale(f);
		b.scale(f);
	}

	public boolean computeClosestPoint(Vector3d v) {
		for(int subset = bitsCurrentSimplex; subset != 0x0; subset--) {
			if(isSubset(subset,bitsCurrentSimplex)&&isValidSubset(subset|lastFoundBit)) {
				bitsCurrentSimplex = subset | lastFoundBit;
				v.set(computeClosestPointForSubset(bitsCurrentSimplex));
				return true;
			}
		}
		if(isValidSubset(lastFoundBit)) {
			bitsCurrentSimplex = lastFoundBit;
			maxLengthSquare = pointsLengthSquared[lastFound];
			v.set(points[lastFound]);
			return true;
		}
		return false;
	}

	public void backupClosestPoint(Vector3d v) {
		double minDistSquare = Float.MAX_VALUE;
		for(int bit = allBits; bit != 0x0; bit--) {
			if(isSubset(bit,allBits)&&isProperSubset(bit)) {
				final Vector3d u = computeClosestPointForSubset(bit);
				final double d = u.lengthSquared();
				if(d < minDistSquare) {
					minDistSquare = d;
					bitsCurrentSimplex = bit;
					v.set(u);
				}
			}
		}
	}

	private Vector3d computeClosestPointForSubset(int subset) {
		final Vector3d v = new Vector3d();
		maxLengthSquare = 0;
		double deltaX = 0;
		for(int i = 0, bit = 0x1; i < 4; i++, bit<<=1) {
			if(overlap(subset,bit)) {
				deltaX+=det[subset][i];
				if(maxLengthSquare<pointsLengthSquared[i])maxLengthSquare=pointsLengthSquared[i];
				Vector3d p = new Vector3d(points[i]);
				p.scale(det[subset][i]);
				v.add(p);
			}
		}
		if(deltaX<=0)throw new IllegalStateException("dX must be greater than 0");
		v.scale(1f/deltaX);
		return v;
	}

	private static boolean overlap(int a, int b) { 
		return (a & b) != 0x0; 
	} 

	private static boolean isSubset(int a, int b) { 
		return (a & b) == a; 
	} 
}