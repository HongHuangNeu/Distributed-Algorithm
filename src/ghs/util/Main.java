package ghs.util;


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
            int nodeId = Integer.parseInt(args[1]);

            double[][] graph = GraphReader.readGraph(graphFileName);
            int nNodes = graph.length;

            //setup logger
           
            System.out.println("the graph is:");
            for (int x = 0; x < graph.length; x++) {
                for (int y = 0; y < graph.length; y++) {
                    double c = graph[x][y];
                    System.out.print((c == Double.MAX_VALUE ? "-" : c) + "\t");
                }

                System.out.println();
            }

            double[][] mst = kruskal.core.fileToMst(graphFileName);

            System.out.println("The MST should be");
            for (int x = 0; x < nNodes; x++) {
                for (int y = 0; y < nNodes; y++) {
                    Double c = mst[x][y];
                    System.out.print((c == Double.MAX_VALUE ? "-" : c) + "\t");
                }

                System.out.println();
            }

            //Node u = new Node(nodeId, nNodes, new VectorClock(nodeId, nNodes), graph[nodeId]);

            Node[] nodes = new Node[nNodes];

            for (int i = 0; i < nNodes; i++) {
                nodes[i] = new Node(i, nNodes, new VectorClock(nNodes, i), graph[i]);
            }

            for (Node node : nodes) {
                new Thread(node).start();
            }

            for (Node node : nodes) {
                node.wakeup();
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception in creating process");
        }
    }
}
