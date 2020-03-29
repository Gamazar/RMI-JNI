import java.io.Serializable;
import java.util.Random;

public class Beacon implements Serializable{
	int ID;
	int StartUpTime;
	String CmdAgentID;
	int SentTime;
	
	public Beacon() {
		Random rand = new Random();
		ID = rand.nextInt(100);
		StartUpTime = (int) (System.currentTimeMillis()/1000);
		
	}
}
