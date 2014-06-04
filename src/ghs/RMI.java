package ghs;
import java.rmi.Remote;
import java.rmi.RemoteException;

import ghs.message.Message;

public interface RMI extends Remote{
    public void receive(Message message) throws RemoteException;
}