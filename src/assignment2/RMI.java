package assignment2;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI<T> extends Remote{
public void receive(Message message)throws RemoteException;    
}
