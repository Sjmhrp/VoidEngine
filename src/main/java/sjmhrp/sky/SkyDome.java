package sjmhrp.sky;

import java.io.Serializable;
import java.util.ArrayList;

import sjmhrp.io.StarHandler;
import sjmhrp.view.Frustum;

public class SkyDome implements Serializable{
	
	private static final long serialVersionUID = 2767563321211622722L;
	
	ArrayList<CelestialBody> bodies = new ArrayList<CelestialBody>();
	String starCat;
	transient ArrayList<Star> stars = new ArrayList<Star>();
	private double size = Frustum.FAR_PLANE;

	public void addBody(CelestialBody b) {
		bodies.add(b);
	}

	public void addBodies(ArrayList<CelestialBody> bodies) {
		this.bodies.addAll(bodies);
	}
	
	public void addStar(Star s) {
		stars.add(s);
	}
	
	public void addStars(ArrayList<Star> stars) {
		this.stars.addAll(stars);
	}
	
	public void tick(double dt) {
		for(CelestialBody b : bodies) {
			b.tick(dt);
		}
	}

	public ArrayList<CelestialBody> getBodies() {
		return bodies;
	}

	public ArrayList<Star> getStars() {
		return stars;
	}
	
	public double getSize() {
		return size;
	}

	public void setSize(double domeSize) {
		this.size = domeSize;
	}
	
	public Sun getSun() {
		for(CelestialBody body :bodies) {
			if(body instanceof Sun)return (Sun)body;
		}
		return null;
	}
	
	public void loadStars(String cat) {
		starCat = cat;
		reloadStars();
	}
	
	public void reloadStars() {
		stars = StarHandler.readStars(starCat+".dat");
	}
}