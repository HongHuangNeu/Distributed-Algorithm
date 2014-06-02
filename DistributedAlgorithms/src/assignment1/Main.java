package assignment1;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

import assignment1.clock.VectorClock;

public class Main {
public static Integer id=0;
public static PrintWriter writer ; 
/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// Parse input
			int processes = Integer.parseInt(args[0]);
			int processIndex = Integer.parseInt(args[1]);
			
			try
			{
				java.rmi.registry.LocateRegistry.createRegistry(4303);
			}
			catch(ExportException e)
			{
				System.out.println("get Registry");
				java.rmi.registry.LocateRegistry.getRegistry(4303);
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
				Process p = new Process( processIndex, processes, new VectorClock(processes, processIndex));
				new Thread(p).start();
			}
			catch(RemoteException e)
			{
				e.printStackTrace();
				System.out.println("Exception in creating process");
			}
			writer.close();
			
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
	}

     
}
