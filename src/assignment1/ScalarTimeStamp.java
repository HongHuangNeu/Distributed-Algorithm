package assignment1;

public class ScalarTimeStamp implements TimeStamp<Integer>{
	
	private Integer time;
	
	public ScalarTimeStamp(){
		this.time = 0;
	}
	
	public ScalarTimeStamp(int time){
		this.time = new Integer(time);
	}
	
	public ScalarTimeStamp(Integer time){
		this.time = time;
	}
	
	public Integer getTime() {
		return this.time;
	}
}
