// It's a clock - it keeps track of time
public class Clock{
	String id;		// Clock id
	long ticks;		// Number of ticks (time units) passed since instantiation
	
	public Clock(String id){
		this.id = id;
		this.ticks = 0;
	}

	public String getId(){
		return this.id;
	}

	public long getTicks(){
		return this.ticks;
	}

	public void increment(){
		this.ticks ++;
	}
}