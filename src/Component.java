/*
##########################################################################
CA4006 - Rocket Scientist Problem
Created by Paul Treanor and Karl Finnerty

Description:
	Is a part of Spacecraft.

##########################################################################
*/
import java.util.Random;

public class Component implements Runnable{
	String id;
	String type;					// Type of component i.e. Fuel, Thrusters, Instruments, Control Systems, Power Plants
	boolean status;					// Indicate if component is working or broken
	Spacecraft spacecraft; 			// Spacecraft components are located on
	double quantity;					// quantity of contents of component where applicable e.g. fuel
	long [] reportSchedule;

	public Component(String type, int id, double quantity, Spacecraft spacecraft){
		this.type = type;
		this.id = this.type + "-" + id;
		this.status = true;
		this.quantity = quantity;
		this.spacecraft = spacecraft;
	}

	public String getType(){
		return this.type;
	}

	public String getId(){
		return this.type;
	}

	public void createReportSchedule(){
		long initReport = 0;
		long midReport = (long) this.spacecraft.mission.getTof()/2;
		this.reportSchedule = new long[]{initReport, midReport};
	}

	public void run(){
		// long reportSchedule[] = createReportSchedule();
		// int i = 0;
		// System.out.println("COMPONENT RUN");
		// while (i < reportSchedule.length){
			// if(this.spacecraft.mission.controller.clock.getTicks() < reportSchedule[i] + this.spacecraft.mission.startTime){
				try {
					report();
					//i++;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			// }
		// }
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
		DataTransmission tx = new DataTransmission(
			this.spacecraft.mission, 
			"report", 
			reportString, 
			this.spacecraft.mission.controller.getControllerId(), 
			this.spacecraft.getSpacecraftId());
		this.spacecraft.sendDataTransmission(tx);
	}
}