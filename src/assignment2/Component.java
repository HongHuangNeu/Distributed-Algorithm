package assignment2;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import assignment2.DelayedMessageSender;
import assignment1.Process;
import assignment1.clock.VectorClock;
import assignment1.clock.VectorTimeStamp;

//@SuppressWarnings("serial")
public class Component<T> extends UnicastRemoteObject implements RMI<T>,
		Runnable, Serializable {
	
	private static final long serialVersionUID = 7247714666080613254L;
	private State[] S;
	private int[] N;
	private int componentId;
	private int totalNumber;
	private Token token=null;
	private int maxDelay=1000;
	public Component(int componentIndex,int totalNumber)
			throws RemoteException{
		super();
		S=new State[totalNumber];
		N=new int[totalNumber];
		componentId=componentIndex;
		this.totalNumber=totalNumber;
		for(int i=0;i<N.length;i++)
		{
			N[i]=0;
		}
		for(int i=0;i<this.componentId;i++)
		{
			S[i]=State.Request;
		}
		for(int i=this.componentId;i<totalNumber;i++)
		{
			S[i]=State.Other;
		}
		if(componentId==0)
		{
			S[0]=State.Hold;
			token=new Token(totalNumber);
		}
		
		try {
			Registry registry = LocateRegistry.getRegistry(4303);
			registry.rebind(Integer.toString(this.componentId), this);
			System.out.println("bind successful");
			System.setProperty("java.security.policy", Process.class
			.getResource("my.policy").toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Bind fail");
		}
		System.out.println(this.componentId+S(this.S));
		System.out.println(this.componentId+N(this.N));
	}
	
	

	
	
	public void send(Message m, int receiverIndex)
	{
		
		
		try
		{
			
			// wrap message contents m into message object
			Message mObj;
			
			// notify the clock
				//System.out.println("number before send"+(Main.id+1)+this.processClock.getCurrentTime());
				//Main.writer.println("number before send"+(Main.id+1)+this.processClock.getCurrentTime());
					mObj = m;
				//logging
							
			
			//System.out.println("after send"+mObj.getId()+this.processClock.getCurrentTime());
			// get the proxy object
			Registry registry = LocateRegistry.getRegistry("127.0.0.1",
					4303);
			RMI<T> reciever = (RMI<T>) registry.lookup(Integer.toString(receiverIndex));
			
			// send the message
			//reciever.receive(mObj);
			DelayedMessageSender<T> sender = new DelayedMessageSender<T>(reciever, mObj, 0);
			new Thread(sender).start();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();

		}
	}
	public void processToken(Token token)
	{
		synchronized(this)	{
			System.out.println("componnet "+this.componentId+"granted critical section!"+N(this.N)+" "+S(this.S));
			System.out.println("enter in"+this.componentId+token);
		this.token=token;
		S[this.componentId]=State.Execute;
		criticalSection();
		S[this.componentId]=State.Other;
		token.setTSelement(this.componentId, State.Other);
		
		for(int i=0;i<this.totalNumber;i++)
		{
			if(N[i]>token.getTN()[i])
			{
				token.setTNelement(i, N[i]);
				token.setTSelement(i, S[i]);
			}
			else
			{
				N[i]=token.getTN()[i];
				S[i]=token.getTS()[i];
			}
		}
		System.out.println(token);
		System.out.println("self state"+this.componentId+N(N)+" "+S(S));
		if(noRequest())
		{
			S[this.componentId]=State.Hold;
		}else
		{  
			for(int i=0;i<S.length;i++)
			{
				if(S[i]==State.Request)
				{
					this.send(newToken(token), i);
					token=null;
					return;
				}
			}
		}
		
		}
	}
	public boolean noRequest()
	{
		
		for(int i=0;i<S.length;i++)
		{
		   if(i!=this.componentId&&!S[i].equals(State.Other))
		   {return false;}
		}
		
		return true;
	}
	public void criticalSection()
	{
		
		System.out.println(this.componentId+"start critical section"+N(this.N)+" "+S(this.S));		
		System.out.println(this.componentId+"finish critical section");
	}
	public void processRequest(Request request)
	{
		synchronized(this){
		System.out.println(this.componentId+"get request from"+request.getSenderId());
	
		N[request.getSenderId()]=request.getNumOfRequest();
		switch(S[this.componentId])
		{
		case Other:
			S[request.getSenderId()]=State.Request;
			System.out.println("process "+this.componentId+"is in Other");	
			break;
		case Execute:
			S[request.getSenderId()]=State.Request;
			System.out.println("process "+this.componentId+"is executing");	
			break;
		case Request:
			if(!S[request.getSenderId()].equals(State.Request))
			{
				System.out.println("process "+this.componentId+"is Request");	
				S[request.getSenderId()]=State.Request;
				send(new Request(this.componentId,N[this.componentId]),request.getSenderId());
			}
			break;
		case Hold:
			S[request.getSenderId()]=State.Request;
			S[this.componentId]=State.Other;
			System.out.println("process "+this.componentId+"is holding and send to"+request.getSenderId());	

			if(token==null){System.out.println("token is null!");}
			token.setTSelement(request.getSenderId(), State.Request);// bug Hold but token is null
			token.setTNelement(request.getSenderId(), request.getNumOfRequest());
			
			send(newToken(token),request.getSenderId());
			System.out.println("process"+this.componentId+"gives token to"+request.getSenderId()+" "+S(S)+" "+N(N));
			System.out.println("after give:"+token);
			token=null;
			break;
		}
		}
		
	}
	public void run() {
		for (int i = 0; i < 1000; i++) {
			
			if(S[this.componentId]!=State.Hold&&S[this.componentId]!=State.Execute)
			this.request();
			
			while(S[this.componentId]==State.Request)
			{
		
			}
			
			while(S[this.componentId]==State.Execute||S[this.componentId]==State.Hold)
			{	
			}
			System.out.println(this.componentId+" "+i+"rounds");
			}
		System.out.println(this.componentId+"sent all request");
	}
	public void request()
	{
	synchronized(this){
		if(this.componentId==0)
		System.out.println("component"+this.componentId+"request resource"+N(this.N)+" "+S(this.S));
		S[this.componentId]=State.Request;
		N[this.componentId]++;
	
		for(int i=0;i<this.totalNumber;i++)
		{
			if(i!=this.componentId&&S[i].equals(State.Request))
			{
				if(this.componentId==0)
				System.out.println("component"+this.componentId+"sends request to"+i);
				send(new Request(this.componentId,N[this.componentId]),i);
				
			}
		
		}
	}
	}
	public  void receive(Message message) throws RemoteException {
		// notify the clock
		
		if(message instanceof Request)
		{
			processRequest((Request)message);
		}
		if(message instanceof Token)
		{
			processToken((Token)message);
		}
	}
	private long generateDelay()
	{
		return Math.round(Math.random() * this.maxDelay);
	}
	public Token newToken(Token token)
	{
		Token t=new Token(this.totalNumber);
		t.setTN(token.getTN());
		t.setTS(token.getTS());
		return t;
	}
	public String N(int[] n)
	{
		String string="";
		for(int i:n)
		{
			string+=i;
		}
		return string;
	}
	public String S(State[] s)
	{
		String string="";
		for(State i:s)
		{
			string+=i;
		}
		return string;
	}
}
