package distribute;

/****************************************************************************************
 * 											*	
 *																						*
 * This class simulate multiple processes for the RBA program							* 
 ***************************************************************************************/

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class main {
	private static int current;
	private static final int num =1;
	private static final int f =0;
	
	public static void main(String[] args) throws Exception {
		int numJVM=4;
		
		java.rmi.registry.LocateRegistry.createRegistry(4303);
		for(int i=0;i<numJVM;i++)
		{
			runJVM(numJVM,i);
		}
		/**REMARK:
		 * taskkill /f /im java.exe 
		 * in cmd to kill all the processes after terminating app in Eclipse!
		 * */
	}
	public static void runJVM(final int numJVM,final int indexJVM)
	{
		System.out.println("total JVM"+numJVM+" index"+indexJVM);
		System.out.println("========================================================");	
		Thread process = null; 
		//simulate multiple processes
		

			process = new Thread() {
				public void run() {
					try {
						List<String> command = new ArrayList<String>();
					
						String javaBin = "java ";
						command.add(javaBin);
						command.add("-cp");
						command.add(System.getProperty("java.class.path"));
						command.add(assignment3.Main.class.getCanonicalName());
						command.add("" + indexJVM);
						command.add(""+numJVM);
						//command.add("" + f);
						System.out.println(command);
						ProcessBuilder pb = new ProcessBuilder(command);
						pb.redirectErrorStream(true);
						Process p = pb.start();
						//Read out dir output
						InputStream is = p.getInputStream();
						InputStreamReader isr = new InputStreamReader(is);
						BufferedReader br = new BufferedReader(isr);
						String line;
						while ((line = br.readLine()) != null) {
							System.out.println(line);
						}
						p.waitFor();
					} catch (Exception e) {}
				}
			};
			process.start();
			//wait for some time
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
				
	}
}
