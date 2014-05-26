package assignment3;
import java.rmi.Remote;
import java.rmi.RemoteException;

import assignment3.message.Message;

public interface RMI extends Remote{
public void receive(Message message)throws RemoteException;    
}
