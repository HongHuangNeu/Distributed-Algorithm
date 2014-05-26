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



		try{
            String graphFileName = args[0];
			int nodeId = Integer.parseInt(args[1]);

            float[][] graph = GraphReader.readGraph(graphFileName);

            int nNodes = graph.length;

            Node u = new Node(nodeId, nNodes, new VectorClock(nodeId, nNodes), graph[nodeId]);

		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception in creating process");
		}
	}

     
}
