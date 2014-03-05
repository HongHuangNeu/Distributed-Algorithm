package assignment1;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI<T> extends Remote{
public void receive(Message<T> message)throws RemoteException;    
}
