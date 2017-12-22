package sjmhrp.world.terrain.generator;

import static java.lang.Math.cos;
import static java.lang.Math.floor;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static sjmhrp.utils.ScalarUtils.lerp;

import sjmhrp.utils.linear.Vector2d;
import sjmhrp.utils.linear.Vector3d;

public class CascadeTerrainGenerator extends TerrainGenerator {

	private static final long serialVersionUID = -2576388259840010334L;

	static int[] p = {151,160,137,91,90,15,131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,190,6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,88,237,149,56,87,174,20,125,136,171,168,68,175,74,165,71,134,139,48,27,166,77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,102,143,54, 65,25,63,161,1,216,80,73,209,76,132,187,208,89,18,169,200,196,135,130,116,188,159,86,164,100,109,198,173,186,3,64,52,217,226,250,124,123,5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,223,183,170,213,119,248,152,2,44,154,163,70,221,153,101,155,167,43,172,9,129,22,39,253,19,98,108,110,79,113,224,232,178,185,112,104,218,246,97,228,251,34,242,193,238,210,144,12,191,179,162,241,81,51,145,235,249,14,239,107,49,192,214,31,181,199,106,157,184, 84,204,176,115,121,50,45,127,4,150,254,138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180,151,160,137,91,90,15,131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,190,6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,88,237,149,56,87,174,20,125,136,171,168,68,175,74,165,71,134,139,48,27,166,77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,102,143,54,65,25,63,161,1,216,80,73,209,76,132,187,208, 89,18,169,200,196,135,130,116,188,159,86,164,100,109,198,173,186,3,64,52,217,226,250,124,123,5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,223,183,170,213,119,248,152,2,44,154,163,70,221,153,101,155,167,43,172,9,129,22,39,253,19,98,108,110,79,113,224,232,178,185,112,104,218,246,97,228,251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127,4,150,254,138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180};
	
	static double grad(int hash, double x, double y, double z) {
		int h = hash&15;
		double u = h<8?x:y;
		double v = h<4?y:h==12||h==14?x:z;
		return ((h&1)==0?u:-u)+((h&2)==0?v:-v);
	}

	static double fade(double t) {return t*t*t*(t*(t*6-15)+10);}
	
	static double perlinNoise(Vector3d position) {
		int X = ((int)floor(position.x))&255;
		int Y = ((int)floor(position.y))&255;
		int Z = ((int)floor(position.z))&255;
		position.x-=floor(position.x);
		position.y-=floor(position.y);
		position.z-=floor(position.z);
		double u = fade(position.x);
		double v = fade(position.y);
		double w = fade(position.z);
		int A = p[X]+Y;
		int AA = p[A]+Z;
		int AB = p[A+1]+Z;
		int B = p[X+1]+Y;
		int BA = p[B]+Z;
		int BB = p[B+1]+Z;
		return 0.5*lerp(w, lerp(v, lerp(u, grad(p[AA], position.x, position.y, position.z), 
			                           grad(p[BA], position.x-1, position.y, position.z)),
			                   lerp(u, grad(p[AB], position.x, position.y-1, position.z), 
			                           grad(p[BB], position.x-1, position.y-1, position.z))),
			                   lerp(v, lerp(u, grad(p[AA+1], position.x, position.y, position.z-1), 
			                                   grad(p[BA+1], position.x-1, position.y, position.z-1)),
			                   lerp(u, grad(p[AB+1], position.x, position.y-1, position.z-1),
			                           grad(p[BB+1], position.x-1, position.y-1, position.z-1))))+0.5;
	}
	
	{normalResolution = 1;}
	
	@Override
	public double getDensity(double x, double y, double z) {
		double scale1 = 10;
		double scale2 = 0.5;
		Vector3d ws = new Vector3d(x,y,z).scale(1d/scale1);
		double d = 0;
		Vector3d[] pillars = {new Vector3d(0.3,0,2.3).scale(scale2),new Vector3d(-1.7,0,-2).scale(scale2),new Vector3d(1.7,0,0.1).scale(scale2)};
		for(int i = 0; i < 3; i++) {
			d+=1d/ws.xz().sub(pillars[i].xz()).length()-1;
		}
		d-=1d/ws.xz().length()-1;
		d-=pow(ws.xz().length(),3);
		d+=scale2*4*ws.xz().dot(new Vector2d(cos(ws.y),sin(ws.y)));
		d+=scale2*4*cos(ws.y);
		d+=perlinNoise(ws);
		return -d;
	}	
}