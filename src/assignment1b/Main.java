package assignment1b;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import process.Process;

public class Main {
	public static Registry registry;
	public static Integer id = 0;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Main.registry = java.rmi.registry.LocateRegistry
					.createRegistry(3721);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		try {
			new Process(0);
			new Process(1);
			new Process(2);
		} catch (RemoteException e) {
			e.printStackTrace();
			System.out.println("Exception in creating process");
		}
	}

}
