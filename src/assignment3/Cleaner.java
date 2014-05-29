package assignment3;
import java.util.*;
public class Cleaner extends TimerTask {

	Component c;
	public Cleaner(Component c)
	{
		this.c=c;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		c.showInfo();
		System.out.println(c.getComponentId()+"start cleaning");
			while(c.tryAll())
			{
				
			}
		
	}

}
