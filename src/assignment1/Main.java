package assignment1;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Main {
public static Integer id=0;
 /**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			java.rmi.registry.LocateRegistry.createRegistry(4303);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		try{
	Process p1=	new Process("0", 0);
	Process p2=	new Process("1", 1);
	Process p3=	new Process("2", 2);
		new Thread(p1).start();
		new Thread(p2).start();
		new Thread(p3).start();
		}catch(RemoteException e)
		{
			e.printStackTrace();
			System.out.println("Exception in creating process");
		}
	}

}
