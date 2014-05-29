package assignment3;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.awt.AWTException;
import java.awt.Robot;

import assignment3.DelayedMessageSender;
import assignment1.Process;
import assignment1.clock.VectorClock;
import assignment1.clock.VectorTimeStamp;

//@SuppressWarnings("serial")
public class Component extends UnicastRemoteObject implements RMI,
		Runnable, Serializable {
	private static final long serialVersionUID = 7247714666080613254L;
	private int LN;
	private float FN;
	private State SN=State.Sleep;
	private int in_branch;
	private int test_edge=Accept.Initial;
	private int best_edge;
	private float best_weight;
	private int find_count;
	private float[] adjacent;
	private int c=0;
	private boolean printed=false;
	Queue<Message> queue = new LinkedList<Message>(); 
	/*
	 * LN=0;
			SN=State.Found;
			find_count=0;
	 * */
	
	private ArrayList<Integer> unknown_inMST=new ArrayList<Integer>();
	private ArrayList<Integer> not_inMST=new ArrayList<Integer>();
	private ArrayList<Integer> inMST=new ArrayList<Integer>();
	
	private int componentId;
	private int totalNumber;
	private int maxDelay=1000;
	int counter=0;
	
	public Component(int componentIndex,int totalNumber,float[] adjacent)
			throws RemoteException{
		super();
		this.componentId=componentIndex;
		this.totalNumber=totalNumber;
		this.adjacent=new float[totalNumber];
		for(int i=0;i<adjacent.length;i++)
		{
			this.adjacent[i]=adjacent[i];
		}
		for(int i=0;i<this.adjacent.length;i++)
		{
			
			if(this.adjacent[i]!=Float.MAX_VALUE)
			{
				this.unknown_inMST.add(i);
			}
		}
		
		try {
			Registry registry = LocateRegistry.getRegistry(4303);
			registry.rebind(Integer.toString(this.componentId), this);
			//System.out.println("bind successful");
			System.setProperty("java.security.policy", Process.class
			.getResource("my.policy").toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			//System.out.println("Bind fail");
		}
		
	}
	
	

	
	
	public void send(Message m, int receiverIndex)
	{
		
		
		try
		{
			
			// wrap message contents m into message object
			Message mObj;
			m.seq_id=counter;
			counter++;
			// notify the clock
				////System.out.println("number before send"+(Main.id+1)+this.processClock.getCurrentTime());
				//Main.writer.println("number before send"+(Main.id+1)+this.processClock.getCurrentTime());
					mObj = m;
				//logging
							
			
			////System.out.println("after send"+mObj.getId()+this.processClock.getCurrentTime());
			// get the proxy object
			Registry registry = LocateRegistry.getRegistry("127.0.0.1",
					4303);
			RMI reciever = (RMI) registry.lookup(Integer.toString(receiverIndex));
			
			// send the message
			//reciever.receive(mObj);
		
			//reciever.receive(mObj);
			DelayedMessageSender sender = new DelayedMessageSender(reciever, mObj, 0);
			new Thread(sender).start();
			
		}
		catch (Exception e)
		{
			e.printStackTrace();

		}
	}
	
	public boolean processChangeRoot()
	{
		change_root();
		return true;
	}
	public void report()
	{
		System.out.println(componentId+"try to report");
		synchronized(this){
			//System.out.println("find count"+find_count);
			if(find_count==0&&this.test_edge==Accept.Initial)
			{
				SN=State.Found;
				
				this.send(new Report(this.componentId,this.best_weight),this.in_branch);
				System.out.println(componentId+"report to "+this.in_branch);
				
			}else
			{
				System.out.println("not report, find count"+find_count+"test_edge"+test_edge);
			}
		}
	}
	public boolean processReport(Report msg)
	{
		
		if(msg.inQueue)
			System.out.println(this.componentId+"process msg report again from"+msg.getSenderId()+"number "+msg.seq_id);
		else
			System.out.println(this.componentId+"process msg report from"+msg.getSenderId()+"number"+msg.seq_id);
		//System.out.println(this.componentId+"process msg report from"+msg.getSenderId());
		synchronized(this){
			if(msg.getSenderId()!=in_branch)
			{
				//careful
				
				find_count--;
				if(msg.getBest_weight()<best_weight)
				{
					best_weight=msg.getBest_weight();
					best_edge=msg.getSenderId();
				}
				report();
			}
			else{
				System.out.println(componentId+"in branch"+in_branch);
				if(SN.equals(State.Find))
				{
					System.out.println(this.componentId+"says"+msg.getSenderId()+"has to wait,seq:"+msg.seq_id);
					if(!msg.inQueue)
					{
						msg.inQueue=true;
						queue.offer(msg);
					}
					return false;
				}else{
					if(msg.getBest_weight()>best_weight)
					{
						change_root();
					}else{
						if(msg.getBest_weight()==best_weight&&msg.getBest_weight()==Float.MAX_VALUE)
						{
							System.out.println(this.componentId+"Halt");
							System.out.println(this.componentId+"process: in MST:"+inMST+"\n  not in MST:"+this.not_inMST+"find count "+this.find_count+"this state "+this.SN);
				 			System.out.println(this.FN+" level"+this.LN);
							checkTerminate();
							SN=State.Found;
						}
					}
				}
			}
		}
		return true;
	}
	public void wakeup()
	{
		synchronized(this){
			int j=adjacentMinimalEdge();
			System.out.println(componentId+"choose"+j+"to be the minimum weight");
			//System.out.println(this.componentId+" unknown list"+unknown_inMST);
			this.delUnknownInMst(j);
			//this.unknown_inMST.remove(j);
			this.delNotInMst(j);
			//this.not_inMST.remove(j);
			//inMST.add(j);//single
			addInMST(j);
			LN=0;
			SN=State.Found;
			find_count=0;
			this.send(new Connect(this.componentId,0),j);
		}
	}
	public void change_root()
	{
		synchronized(this){	
			if(inMST.contains(this.best_edge))
			{
				this.send(new ChangeRoot(this.componentId), this.best_edge);
			}
			else
			{
				this.send(new Connect(this.componentId,LN), this.best_edge);
				//System.out.println(this.componentId+" unknown list "+unknown_inMST);
				this.delUnknownInMst(best_edge);
				//this.unknown_inMST.remove(this.best_edge);
				this.delNotInMst(best_edge);
				//this.not_inMST.remove(this.best_edge);
				//inMST.add(this.best_edge);//single
				addInMST(best_edge);
			}
		}
	}
	public boolean processInitial(Initiate msg)
	{//System.out.println(this.componentId+"process msg initiate"+msg.getSerialversionuid());
		
		if(msg.inQueue)
			System.out.println(this.componentId+"process msg initial again from"+msg.getSenderId()+"seq:"+msg.seq_id+"level"+msg.getL());
		else
			System.out.println(this.componentId+"process msg initial from"+msg.getSenderId()+"seq"+msg.seq_id);
		synchronized(this){
			LN=msg.getL();
			FN=msg.getF();
			SN=msg.getS();			
			System.out.println(this.componentId+"level becomes"+LN);
			in_branch=msg.getSenderId();
			best_edge=Accept.Initial;
			best_weight=Float.MAX_VALUE;
			for(int i=0;i<adjacent.length;i++)
			{
				if(adjacent[i]!=Float.MAX_VALUE&&i!=msg.getSenderId()&&inMST.contains(i))
				{
					
					send(new Initiate(this.componentId,msg.getL(),FN=msg.getF(),SN=msg.getS()),i);
					if(msg.getS().equals(State.Find))
					{
						find_count++;
					}
				}
			}
			if(msg.getS().equals(State.Find))	
			test();
			
		}
		return true;
	}
	
	public void test()
	{
		//System.out.println(this.componentId+" "+this.unknown_inMST);
		synchronized(this){	
			if(this.unknown_inMST.size()!=0)
			{
				test_edge=this.unknowMinimumEdge();
				send(new Test(this.componentId,LN,FN),test_edge);
			}else{
				test_edge=Accept.Initial;
				report();
			}
		}
	}
	
	public boolean processConnect(Connect msg)
	{
		
		if(msg.inQueue)
		System.out.println(this.componentId+"process msg connect again from"+msg.getSenderId()+"seq"+msg.seq_id);
		else
			System.out.println(this.componentId+"process msg connect from"+msg.getSenderId()+"seq"+msg.seq_id);
		synchronized(this){
			if(SN.equals(State.Sleep))
			{
				wakeup();
			}
			if(msg.getL()<LN)
			{
				//System.out.println(this.componentId+"unknown list"+unknown_inMST);
				//this.unknown_inMST.remove(msg.getSenderId());
				this.delUnknownInMst(msg.getSenderId());
				//this.not_inMST.remove(msg.getSenderId());
				this.delNotInMst(msg.getSenderId());
				//inMST.add(msg.getSenderId());//single
				addInMST(msg.getSenderId());
				send(new Initiate(this.componentId,LN,FN,SN),msg.getSenderId());
				if(SN.equals(State.Find))
				{
					find_count++;
				}
			}
			else{
				if(this.unknown_inMST.contains(msg.getSenderId()))
				{
					if(!msg.inQueue)
					{
						msg.inQueue=true;
						this.queue.offer(msg);
					}
					System.out.println(this.componentId+"my level"+this.LN+msg.getSenderId()+"level"+msg.getL()+"my in MST"+this.inMST+" unknown MST"+this.unknown_inMST+"best edge"+this.best_edge+"find count "+this.find_count+"this state "+this.SN);
					return false;
				}
				else{
					send(new Initiate(this.componentId,LN+1,adjacent[msg.getSenderId()],State.Find),msg.getSenderId());
				}
			}
		}
		return true;
	}
	public boolean processTest(Test msg)
	{////System.out.println(this.componentId+"process msg test from"+msg.getSenderId());
		
		if(msg.inQueue)
			System.out.println(this.componentId+"process msg test again from"+msg.getSenderId()+"seq"+msg.seq_id+"level"+msg.getL());
		else
			System.out.println(this.componentId+"process msg test from"+msg.getSenderId()+"seq"+msg.seq_id+"level"+msg.getL());
		synchronized(this){
			if(SN.equals(State.Sleep))
			{
				wakeup();
			}
			if(msg.getL()>LN)
			{
				if(!msg.inQueue)
				{
					msg.inQueue=true;
					queue.offer(msg);
				}
				return false;
			}else{
				if(msg.getF()!=FN)
				{
					send(new Accept(this.componentId),msg.getSenderId());
				}else{
					/*if (SE(j) = ?_in_MST) then SE(j) := not_in_MST 
	               if (test-edge ¡Ù j) then send(reject) on edge j  
	               else test()*/
					
					if(this.unknown_inMST.contains(msg.getSenderId()))
					{
						//System.out.println(this.componentId+" unknown list"+unknown_inMST);
						//this.unknown_inMST.remove(msg.getSenderId());
						this.delUnknownInMst(msg.getSenderId());
						//this.inMST.remove(msg.getSenderId());
						this.delInMst(msg.getSenderId());
						//this.not_inMST.add(msg.getSenderId());//single
						addNotInMST(msg.getSenderId());
					}
					if(test_edge!=msg.getSenderId())
					{
						send(new Reject(this.componentId),msg.getSenderId());
					}else{
						test();
					}
				}
			}
		}
		return true;
	}
	public boolean processAccept(Accept msg)
	{//System.out.println(this.componentId+"process msg accept"+msg.getSerialversionuid());
		
		if(msg.inQueue)
			System.out.println(this.componentId+"process msg accept again from"+msg.getSenderId()+"seq"+msg.seq_id);
		else
			System.out.println(this.componentId+"process msg accept from"+msg.getSenderId()+"seq"+msg.seq_id);	
		synchronized(this){
			test_edge=Accept.Initial;
			if(adjacent[msg.getSenderId()]<best_weight)
			{
				best_edge=msg.getSenderId();
				best_weight=adjacent[msg.getSenderId()];
			}
			report();
			return true;
		}
	}
	public boolean processReject(Reject msg)
	{////System.out.println(this.componentId+"process msg reject from"+msg.getSenderId());
		
		if(msg.inQueue)
			System.out.println(this.componentId+"process msg reject again from"+msg.getSenderId());
		else
			System.out.println(this.componentId+"process msg reject from"+msg.getSenderId());
		synchronized(this){
			if(this.unknown_inMST.contains(msg.getSenderId()))
			{	//System.out.println("still unknown");
				//System.out.println(this.componentId+" unknown list "+unknown_inMST);
				//this.unknown_inMST.remove(msg.getSenderId());
				this.delUnknownInMst(msg.getSenderId());
				//this.inMST.remove(msg.getSenderId());
				this.delInMst(msg.getSenderId());
				//this.not_inMST.add(msg.getSenderId());//single
				addNotInMST(msg.getSenderId());
			}
			test();
			return true;
		}
	}
	public void run() {
	
		java.util.Timer timer = new java.util.Timer();
		timer.schedule(new Cleaner(this), 1000, 1000);


		
		while(SN.equals(State.Sleep))
		{
			wakeup();
		}
		
		while(true){
			synchronized(this){
				
			}
					
		}
	}
	
	public int getComponentId() {
		return componentId;
	}





	public void setComponentId(int componentId) {
		this.componentId = componentId;
	}





	public boolean checkTerminate()
	{
		if(printed){return true;}
		synchronized(this){
			
			if(unknown_inMST.size()==0&&!printed)
			{
				System.out.println("for "+componentId);
				System.out.println(inMST+" are in MST");
				System.out.println(not_inMST+" are not in MST");
				SN=State.Found;
				printed=true;
				for(int i=0;i<adjacent.length;i++)
				{
					if(adjacent[i]!=Float.MAX_VALUE)
					{
						send(new CheckTerminate(this.componentId),i);
					}
				}
			}
			
		}
		return true;
	}
	
	public  void receive(Message message) throws RemoteException {
		// notify the clock
	
	boolean temp=true;
	
	synchronized(this){
		
		if(message instanceof Connect)
		{
			temp=this.processConnect((Connect)message);
	//		processRequest((Request)message);
		}
		if(message instanceof ChangeRoot)
		{
			temp=this.processChangeRoot();
	//		processRequest((Request)message);
		}
		if(message instanceof Accept)
		{
			temp=this.processAccept((Accept)message);
	//		processRequest((Request)message);
		}
		if(message instanceof Initiate)
		{
			temp=this.processInitial((Initiate)message);
	//		processRequest((Request)message);
		}
		if(message instanceof Reject)
		{
			temp=this.processReject((Reject)message);
	//		processRequest((Request)message);
		}
		if(message instanceof Report)
		{
			temp=this.processReport((Report)message);
	//		processRequest((Request)message);
		}
		if(message instanceof Test)
		{
			temp=this.processTest((Test)message);
	//		processRequest((Request)message);
		}
		if(message instanceof CheckTerminate)
		{
			temp=checkTerminate();
		}
		/*if(temp){
			if(queue.size()==0){
				
			}else{
				while(tryAll())
				{
					System.out.println(this.componentId+"try");
				}
				
			}
		}*/
	}
	
	}
	public boolean tryAll()
	{
		synchronized(this){
		if(queue.size()==0)return false;
		   Object[] array=queue.toArray();
					for(Object o:array){
						Message m=(Message)o;
						if(m!=null)
						{	
							boolean result=tryMessage(m);
							if(result)
							{	queue.remove(m);
								return true;
							}
							
						}
					}
				
		System.out.println(this.componentId+"cannot delivered");
		return false;
		}
	}
	public boolean tryMessage(Message message)
	{
		if(message instanceof Connect)
		{
			return this.processConnect((Connect)message);
	//		processRequest((Request)message);
		}
		if(message instanceof ChangeRoot)
		{
			return this.processChangeRoot();
	//		processRequest((Request)message);
		}
		if(message instanceof Accept)
		{
			return this.processAccept((Accept)message);
	//		processRequest((Request)message);
		}
		if(message instanceof Initiate)
		{
			return this.processInitial((Initiate)message);
	//		processRequest((Request)message);
		}
		if(message instanceof Reject)
		{
			return this.processReject((Reject)message);
	//		processRequest((Request)message);
		}
		if(message instanceof Report)
		{
			return this.processReport((Report)message);
	//		processRequest((Request)message);
		}
		if(message instanceof Test)
		{
			return this.processTest((Test)message);
	//		processRequest((Request)message);
		}
		if(message instanceof CheckTerminate)
		{
			return checkTerminate();
		}
		return true;
	}
	private long generateDelay()
	{
		return Math.round(Math.random() * this.maxDelay);
	}
	public int adjacentMinimalEdge()
	{
		synchronized(this){
			int index=0;
			float min=Float.MAX_VALUE;
			for(int j=0;j<this.adjacent.length;j++)
			{
				if(this.adjacent[j]>0&&this.adjacent[j]<min&&unknown_inMST.contains(j)&&j!=componentId)
				{
					//careful
					index=j;
					min=this.adjacent[j];
				}
			}
			return index;
		}
	}
	public void delInMst(int item)
	{
		for(int i=0;i<this.inMST.size();i++)
		{
			if(inMST.get(i)==item)
			{
				inMST.remove(i);
				return;
			}
		}
	}
	public void showInfo()
	{
		System.out.println(this.componentId+"level"+LN+"inMST"+inMST+"not in MST"+not_inMST+"unknown"+unknown_inMST+"state"+SN+"find count"+find_count+"best edge"+best_edge+"in branch"+in_branch);
	}
	public void delNotInMst(int item)
	{
		for(int i=0;i<this.not_inMST.size();i++)
		{
			if(not_inMST.get(i)==item)
			{
				not_inMST.remove(i);
				return;
			}
		}
	}
	public void delUnknownInMst(int item)
	{
		for(int i=0;i<this.unknown_inMST.size();i++)
		{
			if(unknown_inMST.get(i)==item)
			{
				unknown_inMST.remove(i);
				return;
			}
		}
	}
	public void addNotInMST(int item)
	{
		if(!not_inMST.contains(item))
		{
			not_inMST.add(item);
		}
	}
	public void addInMST(int item)
	{
		if(!inMST.contains(item))
		{
			inMST.add(item);
		}
	}
	public int unknowMinimumEdge()
	{
		synchronized(this){
			int index=this.unknown_inMST.get(0);
			//index: adjacent node number of minimum weight; 
			float min=Float.MAX_VALUE;
			for(int i:this.unknown_inMST)
			{
				if(this.adjacent[i]<min)
				{
					index=i;
					min=adjacent[i];
				}
			}
			return index;
		}
	}
	
}
