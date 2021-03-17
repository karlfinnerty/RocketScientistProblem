// It's a clock - it keeps track of time
package physics;

public class Clock{
	String id;		// Clock id
	long ticks;		// Number of ticks (time units) passed since instantiation
	boolean paused;
	long delay;		// Artificially slow down simulation for testing
	
	public Clock(String id){
		this.id = id;
		this.ticks = 0;
		this.paused = true;
		this.delay = 1;
	}

	public String toString(){
        return this.ticks + " tu passed" + " paused:" + this.paused;
       }

	public String getId(){
		return this.id;
	}

	public long getTicks(){
		return this.ticks;
	}

	public long getDelay(){
		return this.delay;
	}

	public void setDelay(long delay){
		this.delay = delay;
	}

	public void increment(){
		this.ticks ++;
	}

	public boolean isPaused(){
		return this.paused;
	}

	public void pause(){
		this.paused = true;
	}

	public void unpause(){
		this.paused = false;
	}
}