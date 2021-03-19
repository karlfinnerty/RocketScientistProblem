/*
##########################################################################
CA4006 - Rocket Scientist Problem
Created by Paul Treanor and Karl Finnerty

Description:
	This class is at the top level of the class hierarchy and instantiates 
	all classes required to run simulation.

##########################################################################
*/
import java.io.File;

import physics.StarSystem;
import physics.Clock;
import physics.Celestial;
import physics.Spherical;

public class RSSim{
	// Initialise solar system
	double G = 6.673 * Math.pow(10, -11); // Define the constant of gravitation in Nm^2/kg^2
	static double DAY = 86400.0;

	public static void main(String[] args) {
		Clock clock = new Clock("simulation");
		Celestial sol = new Celestial(
			"sol", 
			null, 
			null, 
			1.9885 * Math.pow(10, 30), 
			696342.0, 
			0.0, 
			0.0);

		Celestial earth = new Celestial(
			"earth", 
			sol, 
			new Spherical(new double[]{149598262.0, 0.0, 0.0}), 
			5.972 * Math.pow(10, 24), 
			6371.0, 
			149598262.0, 
			149598262.0);

		//Celestial luna = new Celestial(earth, new Vector(new double[]{1.002569, 0.0, 0.0}), 0.002569, 0.002569);
		Celestial mars = new Celestial(
			"mars", 
			sol, 
			new Spherical(new double[]{227943824.0, 0.0, 0.0}), 
			6.4171 * Math.pow(10, 23), 
			3389.0, 
			227943824.0, 
			227943824.0);

		Celestial venus = new Celestial(
			"venus", 
			sol, 
			new Spherical(new double[]{108209475.0, 0.0, 0.0}), 
			4.867 * Math.pow(10, 24), 
			6051.0, 
			108209475.0, 
			108209475.0);

		Celestial neptune = new Celestial(
			"neptune", 
			sol, 
			new Spherical(new double[]{4498396441.0, 0.0, 0.0}), 
			1.024 * Math.pow(10, 26), 
			24622.0, 
			4498396441.0, 
			4498396441.0);

		Celestial mercury = new Celestial(
			"mercury", 
			sol, 
			new Spherical(new double[]{57909227.0, 0.0, 0.0}), 
			3.285 * Math.pow(10, 23), 
			2439.0, 
			57909227.0, 
			57909227.0);

		Celestial jupiter = new Celestial(
			"jupiter", 
			sol, 
			new Spherical(new double[]{778340821.0, 0.0, 0.0}), 
			1.898 * Math.pow(10, 27), 
			69911.0, 
			778340821.0, 
			778340821.0);

		Celestial saturn = new Celestial(
			"saturn", 
			sol, 
			new Spherical(new double[]{1426666422.0, 0.0, 0.0}), 
			5.683 * Math.pow(10, 26), 
			58232.0, 
			1426666422.0, 
			1426666422.0);

		Celestial uranus = new Celestial(
			"uranus", 
			sol, 
			new Spherical(new double[]{2870658186.0, 0.0, 0.0}), 
			8.681  * Math.pow(10, 25), 
			25362.0, 
			2870658186.0, 
			2870658186.0);
		
		StarSystem solarSystem = new StarSystem(new Celestial[]{sol, earth, mars, venus, neptune, mercury, jupiter, saturn, uranus}, clock);

		// Initialise CLI
		try{
			new File("output.dat").delete();
		}catch (Exception e){
			e.printStackTrace();
		}
		EventLog eventLog = new EventLog(clock);
    	Controller theController = new Controller(eventLog, solarSystem, clock);
		CLI simUI = new CLI(theController, eventLog, solarSystem);
		simUI.run();
	}


	
}