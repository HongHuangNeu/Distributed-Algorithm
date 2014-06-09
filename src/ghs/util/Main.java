package ghs.util;

import ghs.Logger;
import ghs.Node;
import ghs.clock.VectorClock;

import java.rmi.RemoteException;

public class Main {
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            java.rmi.registry.LocateRegistry.createRegistry(4303);
        } catch (RemoteException e) {

        }

        try {
            String graphFileName = args[0];
            int iJVM = Integer.parseInt(args[1]);
            int nJVM = Integer.parseInt(args[2]);

            double[][] graph = GraphReader.readGraph(graphFileName);
            int nNodes = graph.length;

            System.out.println("Original Graph");
            for (int x = 0; x < graph.length; x++) {
                for (int y = 0; y < graph.length; y++) {
                    double c = graph[x][y];
                    System.out.print((c == Double.MAX_VALUE ? "-" : c) + "\t");
                }

                System.out.println();
            }

            double[][] mst = kruskal.core.fileToMst(graphFileName);

            System.out.println("Kruskal mst");
            for (int x = 0; x < nNodes; x++) {
                for (int y = 0; y < nNodes; y++) {
                    Double c = mst[x][y];
                    System.out.print((c == Double.MAX_VALUE ? "-" : c) + "\t");
                }

                System.out.println();
            }

            if(iJVM == 0) {
                //setup logger
                Logger logger = new Logger(nNodes, graphFileName);
                new Thread(logger).start();
            }

            Node[] nodes = new Node[nNodes];
            for (int i = 0; i < nNodes; i++) {
                if(nJVM % (i + 1) == iJVM)
                {
                    System.out.println("creating and making a new node");
                    nodes[i] = new Node(i, nNodes, new VectorClock(nNodes, i), graph[i]);
                    new Thread(nodes[i]).start();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception in creating process");
        }
    }
}
