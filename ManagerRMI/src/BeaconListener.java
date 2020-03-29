import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BeaconListener extends Remote{
	public int deposit(Beacon b) throws RemoteException;
	
	public int SentGLT(GetLocalTime glt) throws RemoteException;
	
	public int SentVersion(GetVersion gv) throws RemoteException;
}
