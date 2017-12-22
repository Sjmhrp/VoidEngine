package sjmhrp.io.colladaloader;

public class JointsData {

	public final int jointCount;
	public final JointData head;
	
	public JointsData(int jointCount, JointData head) {
		this.jointCount=jointCount;
		this.head=head;
	}
}