package assignment1;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI<T extends Message<?>> extends Remote{
public void receive(T message)throws RemoteException;    
}
