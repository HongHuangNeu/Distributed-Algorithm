package ghs.rmi;

import ghs.message.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MessageReciever extends Remote {
    public void receive(Message message) throws RemoteException;
}