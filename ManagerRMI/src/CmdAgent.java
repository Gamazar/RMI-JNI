import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CmdAgent extends Remote{
	
	public int GetLocalTime(GetLocalTime glt) throws RemoteException;
	
	public int GetVersion(GetVersion gv) throws RemoteException;
	
}