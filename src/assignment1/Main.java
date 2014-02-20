package assignment1;

import java.rmi.Naming;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int process_num=3;
		
			RemoteProcess a=new RemoteProcess(process_num,0);
			RemoteProcess b=new RemoteProcess(process_num,1);
			RemoteProcess c=new RemoteProcess(process_num,2);
			try{
			Naming.rebind("0", a);
			Naming.rebind("1", b);
			Naming.rebind("2",c);
			}catch(Exception e)
			{
				System.out.println();
			}
			
	}

}
