package assignment1;

import java.rmi.Naming;

public class Delay implements Runnable {
	public static int upperBound;
	/**
	 * @param args
	 */
	Message<VectorTimeStamp> message;

	public Delay(Message<VectorTimeStamp> m) {
		this.message = m;
	}

	public void run() {
		int delay = (int) (Math.random() * (Delay.upperBound + 1));
		try {
			Thread.sleep(delay * 1000);
		} catch (Exception e) {
		}
		try {
			RemoteProcess receiver = (RemoteProcess) Naming
					.lookup("rmi://localhost:1099/" + message.getReceiverName());
			receiver.recieve( message);
		} catch (Exception e) {

			System.out.println("Exception in fetching the object!");
		}
	}
}
