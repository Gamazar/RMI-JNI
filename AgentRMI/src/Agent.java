import java.util.Random;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Agent extends UnicastRemoteObject implements BeaconListener,CmdAgent {
	protected Agent() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	static {
		System.loadLibrary("Server");
	}

	static BeaconListener interf;
	public native void C_GetLocalTime(GetLocalTime glt);
	public native void C_GetVersion(GetVersion gv);
	
	static class BeaconSender implements Runnable{
		
		
		String name;
		private Beacon b;
		
		
		public BeaconSender() {
			b = new Beacon();
		}

		@Override
		public void run() {
			
			try {
				Registry registry = LocateRegistry.getRegistry(9997);

				registry.bind("Beacon"+b.ID, (Remote) new Agent());
				System.out.println("Created new registry");
			}
			catch(RemoteException | AlreadyBoundException e) {
				e.printStackTrace();
			}
			
			while(true) {
				
				try {
					
					b.SentTime = (int)(System.currentTimeMillis()/1000);
					
					interf.deposit(b);
					
					System.out.println("Sent Beacon with ID: " + b.ID);
					
					Thread.sleep(2000);
				} catch (InterruptedException | RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}		
	}

	public static void main(String[] args) throws IOException, InterruptedException, NotBoundException {
		
		String name = "rmi://" + "127.0.0.1" +":"+9997+"/ManagerRemote";
		interf = (BeaconListener)Naming.lookup(name);
		
		Thread bsThread = new Thread(new BeaconSender());
		bsThread.start();
		
		while(true) {
			
		}
	}

	@Override
	public int deposit(Beacon b) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int GetLocalTime(GetLocalTime glt) throws RemoteException {

		C_GetLocalTime(glt);
		interf.SentGLT(glt);
		return 0;
	}

	@Override
	public int GetVersion(GetVersion gv) throws RemoteException {
		// TODO Auto-generated method stub
		C_GetVersion(gv);
		
		interf.SentVersion(gv);
		return 0;
	}

	@Override
	public int SentGLT(GetLocalTime glt) throws RemoteException {
		// TODO Auto-generated method stub

		return 0;
	}
	@Override
	public int SentVersion(GetVersion gv) throws RemoteException {
		// TODO Auto-generated method stub
		
		return 0;
	}
}
