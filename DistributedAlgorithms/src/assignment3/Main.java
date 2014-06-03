package assignment3;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Main {
    public static Integer id=0;
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        try {
            //setup rmi registery
            try {
                LocateRegistry.createRegistry(PortNumber.portnumber);
            }
            catch (RemoteException e) {
                LocateRegistry.getRegistry(PortNumber.portnumber);
            }
            //read args
            String graphFileName = args[0];
            int nodeId = Integer.parseInt(args[1]);

            //read graph
            float[][] graph = GraphReader.readGraph(graphFileName);
            int nNodes = graph.length;

            //create component
            Component[] cs = new Component[nNodes];
            for (int iComponent = 0; iComponent < nNodes; iComponent++) {
                cs[iComponent] = new Component(iComponent, nNodes, graph[iComponent]);
            }
            //Component c = new Component(nodeId, nNodes, graph[nodeId]);

            //start component
            //new Thread(c).start();

            for (Component c : cs) {
                new Thread(c).start();
            }
        }

        catch(RemoteException e)
        {
            e.printStackTrace();
            System.out.println("Exception in creating process");
        }

    }


}
