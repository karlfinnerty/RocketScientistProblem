/*
##########################################################################
CA4006 - Rocket Scientist Problem
Created by Paul Treanor and Karl Finnerty

Description:
	Is a part of Spacecraft.

##########################################################################
*/
import java.util.Random;

public class Component{
	String id;
	String type;					// Type of component i.e. Fuel, Thrusters, Instruments, Control Systems, Power Plants
	boolean status;					// Indicate if component is working or broken
	Spacecraft spacecraft; 			// Spacecraft components are located on
	double quantity;					// quantity of contents of component where applicable e.g. fuel

	public Component(String type, int id, double quantity){
		this.type = type;
		this.id = this.type + "-" + id;
		this.status = true;
		this.quantity = quantity;
	}

	public String getType(){
		return this.type;
	}

	public String getId(){
		return this.type;
	}

	// Decide if component will fail given a chance. 0.5 = 50% chance of failure, 0.1 = 10% chance
	public void failure(double chance){
		Random rand = new Random();
		double failRng = rand.nextDouble();
		if(failRng < chance){
			this.status = false;
			//report();
		}
	}

	// Send report to queue on spacecraft.
	public void report() throws InterruptedException{
		String reportString;
		reportString = this.spacecraft.getSpacecraftId() + "-" + this.type + "-" + this.status;
		this.spacecraft.enqueueTransmit("report", reportString);
	}

}