import java.util.Random;

public class Component{
	String type;					// Type of component i.e. Fuel, Thrusters, Instruments, Control Systems, Power Plants
	boolean status;					// Indicate if component is working or broken
	Spacecraft spacecraft; 			// Spacecraft components are located on

	public Component(String type){
		this.type = type;
		this.status = true;
	}

	// Decide if component will fail given a chance. 0.5 = 50% chance of failure, 0.1 = 10% chance
	public void failure(double chance){
		Random rand = new Random();
		double failRng = rand.nextDouble();
		if(failRng < chance){
			this.status = false;
			report();
		}
	}

	// Send report to queue on spacecraft.
	public void report(){
		String reportString;
		reportString = this.spacecraft + "-" + this.type + "-" + this.status;
		// this.spacecraft.enqueueTransmit("report", reportString);
	}

}