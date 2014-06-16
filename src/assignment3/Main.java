package assignment3;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;
import distribute.*;


public class Main {
public static Integer id=0;
public static PrintWriter writer ; 
/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		float[][] arr=GraphReader.readGraph("small");
		int numProcess=GraphReader.processNum;
		int numJVM=Integer.parseInt(args[1]);
		int indexJVM=Integer.parseInt(args[0]);
		
				
									
		ArrayList<Component> list=new ArrayList<Component>();
		
		for(int i=0;i<numProcess;i++)
		{
			if(i%numJVM==indexJVM)
			{
				Component p;
				try {
					p = new Component( i, numProcess,arr[i]);
					list.add(p);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
	for(Component p:list)
	{
	    new Thread(p).start();
	}
				
		
	
	}

     
}
