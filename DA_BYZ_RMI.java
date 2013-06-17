

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;

public interface DA_BYZ_RMI extends java.rmi.Remote{
	String receive(List<Message> m) throws RemoteException;

}