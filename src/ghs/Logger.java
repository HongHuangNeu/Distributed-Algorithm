package ghs;

import ghs.message.EndReport;
import ghs.message.Message;
import ghs.rmi.MessageReciever;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ferdy on 6/6/14.
 */
public class Logger implements MessageReciever, Serializable, Runnable {

    private static final long serialVersionUID = 7247714126080613254L;
    private static Map<Integer, EndReport> endReports = new HashMap<>();
    private int processes;
    private String graphFileName;

    public Logger(int processes, String graphFileName) throws RemoteException {
        this.processes = processes;
        this.graphFileName = graphFileName;

        Registry registry = LocateRegistry.getRegistry(4303);
        registry.rebind("logger", this);
    }

    @Override
    synchronized public void receive(Message m) throws RemoteException {
        if(m.getPayload() instanceof EndReport) {
            this.endReports.put((int) m.getPayload().getFrom(), (EndReport) m.getPayload());
            if(this.endReports.values().size() == this.processes) {
                boolean succes = this.weighTree(this.generateGraph()) == (this.weighTree(kruskal.core.fileToMst(this.graphFileName)));
                System.out.println(succes);
            }
        }
        System.out.println("[" + m.getSenderId() + "\t -> " + m.getPayload().getFrom() + "\t] " + m.getPayload());
    }

    private double[][] generateGraph() {
        double[][] graph = new double[this.processes][this.processes];

        for(int y = 0; y < this.processes; y ++) {
            double[] row = endReports.get(y).getAdjacents();
            System.out.println();
            for(int x = 0; x < this.processes; x++) {
                double w = row[x];
                System.out.print(w != Double.MAX_VALUE ? w : "-");
                System.out.print("\t");
                graph[x][y] = w;
                graph[y][x] = w;
            }
        }

        return graph;
    }

    public double weighTree(double[][] tree) {
        double weight = 0;

        for (int x = 0; x < this.processes; x++) {
            for (int y = 0; y < x; y++) {
                double w = tree[x][y];
                if(w != Double.MAX_VALUE) {
                    weight += w;
                }
            }
        }

        return weight;
    }

    public void run () {
        while(true);
    }
}
