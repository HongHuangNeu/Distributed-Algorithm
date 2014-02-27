package assignment1;
import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

//@SuppressWarnings("serial")
public class Process extends UnicastRemoteObject implements RMI<Message>,Runnable,Serializable{
	private static final long serialVersionUID = 7247714666080613254L;
	String processName;
  Process process;
  public static int round=0;
  public Process(String processName) throws RemoteException
  {
	  
		  super();
        this.processName=processName;
         try{       
        	 synchronized(this){
        	
        		 Registry registry = LocateRegistry.getRegistry(4303);
                 registry.rebind(processName, this);
        		 
        	 }
         }catch(Exception e)
         {
        	 e.printStackTrace();
        	System.out.println("Bind fail"); 
         }
         
         
         System.out.println("bind successful");
  }
  public void send( Message  m ,String name)
  {
	  synchronized(this){
	  try {
		  
			  m.id=Main.id++;
			  System.out.println("sent to process "+name+"from process "+processName+" message:"+m.message+" message number "+m.id);
			 
		   System.setProperty("java.security.policy",Process.class.getResource ("my.policy").toString ());
			
			 
		   Registry registry = LocateRegistry.getRegistry("127.0.0.1", 4303);
				RMI<Message> process = (RMI<Message>) registry.lookup(name);
	
		   
				process.receive(m);
		registry.rebind(name, process);
		  
	} catch (Exception e) {
		// TODO Auto-generated catch block
		System.out.println("now process"+processName);
		e.printStackTrace();
		System.out.println("now process"+processName);
		
	} 
	  }
  }
  public void run()
  {
	  for(int i=0;i<100;i++)
	  {
		  int j=i%3;
		  if(Integer.valueOf(processName)!=j)
		  {
			  Message m=new Message(processName,"hello",0);
			  send(m,String.valueOf(j));
		  }
	  }
	  System.out.println("finish sending");
	  //TODO change while loop
	  while(true)
	  {
		  
	  }
  }
	public void receive(Message message)throws RemoteException
 {
		System.out.println(processName+" receive from process "+message.senderName+" message:"+message.message+" message number "+message.id);
  }
}
