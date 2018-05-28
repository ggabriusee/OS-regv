package os.regv.processes;

public class PID {

	private static int next = 1000;
	
	public static int getNew() {
		return next++;
	}
}
