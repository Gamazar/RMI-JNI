import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


class AgentTimeVersion implements Runnable{
	String name;
	CmdAgent interf;
	Beacon b;
	
	public AgentTimeVersion(Beacon b) throws MalformedURLException, RemoteException, NotBoundException {
		String name = "rmi://" + "127.0.0.1" +":"+9997+"/Beacon" + b.ID;
		interf = (CmdAgent)Naming.lookup(name);
		this.b = b;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {

			if(b.CmdAgentID.equals("GetLocalTime")) {
				GetLocalTime glt = new GetLocalTime();
				
				interf.GetLocalTime(glt);
			}
			else {
				GetVersion gv = new GetVersion();
				interf.GetVersion(gv);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

class AgentMonitor implements Runnable{
	
	Map<Integer, Beacon> beaconStorage;
	Map<Integer,Beacon> deadBeacons;
	
	
	public AgentMonitor() throws RemoteException {
		beaconStorage = new HashMap<>();
		deadBeacons = new HashMap<>();
	}
	
	public void add(Beacon b) throws MalformedURLException, RemoteException, NotBoundException {
		

			if(deadBeacons.containsKey(b.ID)) {
				deadBeacons.remove(b.ID);
				System.out.println("Beacon with ID: " + b.ID + " has resurrected.");
				beaconStorage.put(b.ID,b);
			}
			else if(beaconStorage.containsKey(b.ID)) {
				System.out.println("Received existing Beacon with ID: " + b.ID);
				beaconStorage.put(b.ID, b);
			}
			else {
				beaconStorage.put(b.ID,b);
				System.out.println("Received NEW beacon with ID: " + b.ID + ". Now getting set CmdAgentID: " + b.CmdAgentID);
				Thread tvThread = new Thread(new AgentTimeVersion(b));
				tvThread.start();
			}
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
			
		try {
			
			while(true) {
				if(beaconStorage.isEmpty()) {
					System.out.println("Not Active Beacons");
				}
				else {
					for(Map.Entry<Integer, Beacon> entry: beaconStorage.entrySet()) {
						int currSeconds = (int)(System.currentTimeMillis()/1000);
						if(currSeconds - entry.getValue().SentTime >=50) {
							System.out.println("Deleting Beacon with ID: " + entry.getKey());
							
							beaconStorage.remove(entry.getKey());
						}
					}
				}

				Thread.sleep(10000);
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


}

public class Manager extends UnicastRemoteObject implements BeaconListener{
	
	static AgentMonitor agent;
	
	protected Manager() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public int deposit(Beacon b) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Received Beacon with ID: " + b.ID);
		try {
			agent.add(b);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int SentGLT(GetLocalTime glt) throws RemoteException {
		// TODO Auto-generated method stub
		
		if(glt.valid == '0') {
			System.out.println("JNI has failed to get the LocalTime.");
		}
		else {
			int total = glt.time;
			int hours=total/3600;
			total%=3600;
			int minutes=total/60;
			total%=60;
			int seconds = total;
			String s = "";
			if(hours<10) {
				s+="0"+Integer.toString(hours) + " : ";
			}
			else {
				s+=Integer.toString(hours) + " : ";
			}
			if(minutes<10) {
				s+="0"+Integer.toString(minutes) + " : ";
			}
			else {
				s+=Integer.toString(minutes) + " : ";
			}
			if(seconds<10) {
				s+="0"+Integer.toString(seconds);
			}
			else {
				s+=Integer.toString(seconds);
			}
			System.out.println("LocalTime received from Agent JNI: " + s);
		}
		return 0;
	}

	@Override
	public int SentVersion(GetVersion gv) throws RemoteException {
		// TODO Auto-generated method stub
		
		System.out.println("Version from Agent side: " + gv.version);
		return 0;
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, AlreadyBoundException {

		//AgentMonitor thread declaration.
		agent = new AgentMonitor();
		
		Thread agentThread = new Thread(agent);
		agentThread.start();
		Registry registry = LocateRegistry.getRegistry(9997);

		registry.rebind("ManagerRemote", new Manager());
		
		System.out.println("Remote created.");
	}

}
