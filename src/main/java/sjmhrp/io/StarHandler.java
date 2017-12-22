package sjmhrp.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import sjmhrp.utils.ScalarUtils;
import sjmhrp.utils.linear.Vector3d;
import sjmhrp.world.sky.SkyRenderer;
import sjmhrp.world.sky.Star;

public class StarHandler {
	static final String RES_LOC = "/res/";

	public static ArrayList<Star> readStars(String file) {
		InputStreamReader in = new InputStreamReader(Class.class.getResourceAsStream(RES_LOC+file));
		BufferedReader reader = new BufferedReader(in);
		String line = null;
		ArrayList<Star> stars = new ArrayList<Star>();
		try{
			while(true) {
				line=reader.readLine();
				if(line==null)break;
				if(line.length()!=276)continue;
				String ra = line.substring(15,28);
				String de = line.substring(29,42);
				String m = line.substring(129,136);
				String bv = line.substring(152,158);
				ra=ra.replaceAll("\\s","");
				de=de.replaceAll("\\s","");
				m=m.replaceAll("\\s","");
				bv=bv.replaceAll("\\s","");
				double rightAscension = Double.valueOf(ra);
				double declination = Double.valueOf(de);
				double x = Math.cos(declination) * Math.cos(rightAscension);
				double z = Math.cos(declination) * Math.sin(rightAscension);
				double y = Math.sin(declination);
				Vector3d pos = new Vector3d(x,y,z);
				double radius = Math.sqrt(Math.exp(0.92103*Double.valueOf(m)))*0.02;
				Vector3d colour = ScalarUtils.bvToRGB(Double.valueOf(bv));
				if(radius>SkyRenderer.MIN_STAR_SIZE)stars.add(new Star(pos,colour,radius));
			}
			reader.close();
		} catch(Exception e) {
			Log.printError(e);
		}
		return stars;
	}
}