package assignment1;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import assignment1.clock.VectorClock;

public class Main {
public static Integer id=0;
public static PrintWriter writer ; 
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
		
		
		try {
			writer= new PrintWriter("D:\\the-file-name.txt", "UTF-8");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try{
	Process p1=	new Process( 0, new VectorClock(3,0));
	Process p2=	new Process( 1, new VectorClock(3,1));
	Process p3=	new Process( 2, new VectorClock(3,2));
		new Thread(p1).start();
		new Thread(p2).start();
		new Thread(p3).start();
		}catch(RemoteException e)
		{
			e.printStackTrace();
			System.out.println("Exception in creating process");
		}
		writer.close();
	}

     
}
