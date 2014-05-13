package assignment3;

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
			float[] arr0={Float.MAX_VALUE,1,5,2};
			float[] arr1={1,Float.MAX_VALUE,3,4};
			float[] arr2={5,3,Float.MAX_VALUE,6};
			float[] arr3={2,4,6,Float.MAX_VALUE};
			
	Component p0=	new Component( 0, 4,arr0);
	Component p1=	new Component( 1, 4,arr1);
	Component p2=	new Component( 2, 4,arr2);
	Component p3=	new Component( 3, 4,arr3);
	    new Thread(p0).start();
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
