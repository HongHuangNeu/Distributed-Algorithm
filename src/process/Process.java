package process;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import assignment1b.Main;

@SuppressWarnings("serial")
public class Process extends UnicastRemoteObject implements RMI<Message>,
		Runnable, Serializable {
	// private static final long serialVersionUID = 7247714666080613254L;
	int processName;
	public static int round = 0;

	public Process(int processName) throws RemoteException {

		super();
		this.processName = processName;
		try {
			synchronized (Main.registry) {
				// Registry r=java.rmi.registry.LocateRegistry.getRegistry();
				Main.registry.bind("" + processName, this);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Bind fail");
		}
		
		final Process process = this;
		(new Thread() {
			public void run() {
				process.run();
			}
		}).start();
		System.out.println("bind successful");
	}

	public void send(Message m, String name) {
		try {
			synchronized (Main.registry) {
				System.out.println("sent to process " + name + "from process "
						+ processName + " message:" + m.getMessage()
						+ " message number " + m.getId());

				System.setProperty("java.security.policy", Process.class
						.getResource("my.policy").toString());
				Registry r = java.rmi.registry.LocateRegistry.getRegistry();
				RMI<Message> process = (RMI<Message>) Main.registry
						.lookup(name);
				process.receive(m);
				Main.registry.rebind(name, process);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("now process" + processName);
			e.printStackTrace();
			System.out.println("now process" + processName);
		}

	}

	public void run() {
		for (int i = 0; i < 100; i++) {
			int j = i % 3;
			if (Integer.valueOf(processName) != j) {
				Message m;
				synchronized (Main.id)
				{
					m = new Message(processName, "hello", Main.id++);
				}
				send(m, String.valueOf(j));
			}
		}
		System.out.println("finish sending");
		while (true) {

		}
	}

	public void receive(Message message) throws RemoteException {
		System.out.println(processName + " receive from process "
				+ message.getSenderName() + " message:" + message.getMessage()
				+ " message number " + message.getId());
	}
}
