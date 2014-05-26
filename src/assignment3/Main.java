package assignment3;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import assignment3.clock.VectorClock;

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
			float m =Float.MAX_VALUE;
			float[] arr0={m,1,m,m,m,m,m,8};
			float[] arr1={1,m,5,m,m,m,m,m};
			float[] arr2={m,5,m,3,m,m,m,m};
			float[] arr3={m,m,3,m,7,m,m,m};
			float[] arr4={m,m,m,7,m,2,m,m};
			float[] arr5={m,m,m,m,2,m,6,m};
			float[] arr6={m,m,m,m,m,6,m,4};
			float[] arr7={8,m,m,m,m,m,4,m};
			
	Component p0=	new Component( 0, 8,arr0);
	Component p1=	new Component( 1, 8,arr1);
	Component p2=	new Component( 2, 8,arr2);
	Component p3=	new Component( 3, 8,arr3);
	Component p4=	new Component( 4, 8,arr4);
	Component p5=	new Component( 5, 8,arr5);
	Component p6=	new Component( 6, 8,arr6);
	Component p7=	new Component( 7, 8,arr7);
	    new Thread(p0).start();
		new Thread(p1).start();
		new Thread(p2).start();
		new Thread(p3).start();
		new Thread(p4).start();
		new Thread(p5).start();
		new Thread(p6).start();
		new Thread(p7).start();
		
		}catch(RemoteException e)
		{
			e.printStackTrace();
			System.out.println("Exception in creating process");
		}
		writer.close();
	}

     
}
