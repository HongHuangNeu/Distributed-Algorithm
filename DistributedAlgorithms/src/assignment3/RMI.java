package assignment3;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI extends Remote{
public void receive(Message message)throws RemoteException;    
}
