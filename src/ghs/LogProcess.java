package ghs;

import ghs.clock.VectorClock;
import ghs.message.EndReport;
import ghs.message.Message;
import ghs.message.Payload;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by ferdy on 6/6/14.
 */
public class LogProcess implements RMI {

    private Map<Integer, EndReport> endReports = new HashMap<>();
    public LogProcess() throws RemoteException{
        Registry registry = LocateRegistry.getRegistry(4303);
        registry.rebind("logger", this);
    }

    @Override
    public void receive(Message m) throws RemoteException {
        if(m.getPayload() instanceof EndReport) {
            this.endReports.put(m.getSenderId(), (EndReport)m.getPayload());
        }
    }
}
