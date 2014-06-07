package ghs;

import ghs.message.EndReport;
import ghs.message.Payload;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ferdy on 6/6/14.
 */
public class Logger implements Remote, Serializable {

    private Map<Integer, EndReport> endReports = new HashMap<>();
    private int processes;
    private String graphFileName;

    public Logger(int processes, String graphFileName) throws RemoteException {
        this.processes = processes;
        this.graphFileName = graphFileName;

        Registry registry = LocateRegistry.getRegistry(4303);
        registry.rebind("logger", this);
    }

    public synchronized void receive(int from, Payload p) throws RemoteException {
        if(p instanceof EndReport) {
            this.endReports.put((int)p.getFrom(), (EndReport)p);
            System.out.println(this);
            System.out.println(this.endReports);
            if(this.endReports.values().size() == this.processes - 1) {
                boolean succes = this.generateGraph().equals(kruskal.core.fileToMst(this.graphFileName));
                System.out.println(succes);
            }
        }
        System.out.println("[" + from + "\t -> " + p.getFrom() + "\t] " + p);
    }

    private double[][] generateGraph() {
        double[][] graph = new double[this.processes][this.processes];

        for(int y = 0; y < this.processes; y ++) {
            for(int x = 0; x < this.processes; x++) {
                double w = this.endReports.get(x).getAdjacents()[y];
                graph[x][y] = w;
                graph[y][x] = w;
            }
        }

        return graph;
    }
}
