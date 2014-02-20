package assignment1;

import java.rmi.Naming;

public class Delay implements Runnable {
public static int upperBound;
	/**
	 * @param args
	 */
   Message message;
   public Delay(Message m)
   {
	   this.message=m;
   }
    public void run()
    {
    	
    }
}
