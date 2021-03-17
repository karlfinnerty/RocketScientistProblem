/*
##########################################################################
This file is part of the CA4006 - Rocket Scientist Problem
Created by Paul Treanor and Karl Finnerty
Subject to standard MIT License

description:
	This class is at the top level of the class hierarchy and instantiates 
	all classes required to run simulation.

##########################################################################
*/
import java.io.File;

import physics.StarSystem;
import physics.Clock;
import physics.Celestial;
import physics.Spherical;
import physics.Satellite;

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

		Satellite satellite = new Satellite(earth, 100, 100);
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

		StarSystem solarSystem = new StarSystem(new Celestial[]{sol, earth, mars, venus, neptune}, clock);

		// // Do tests
		// System.out.println("!!!!TEST START!!!!");
		// System.out.println(earth.slDistance(sol));
		// System.out.print("Earth speed(m/s): ");
		// System.out.println(earth.coVelocity());
		// System.out.print("Earth orbital period(days): ");
		// System.out.println(earth.getOrbitalPeriod()/DAY);
		// System.out.println();
		// System.out.println("Satellite test: ");
		// System.out.println(satellite.coVelocity());
		// System.out.println(satellite.coOrbit());
		// System.out.println();
		// System.out.println("Earth stats at day 1: ");
		// System.out.println(earth);
		// System.out.println("Mars stats at day 1: ");
		// System.out.println(mars);
		// System.out.println();

		// for(int ticks=0; ticks<175; ticks++){
		// 	earth.incrementAz(earth.getAngularVelocity()*DAY);
		// 	//mars.incrementAz(mars.getAngularVelocity()*DAY);
		// }
		// mars.incrementAz(mars.getAngularVelocity()*DAY*175);

		// System.out.println("Earth stats at day 175: ");
		// System.out.println(earth);
		// System.out.println("Mars stats at day 175: ");
		// System.out.println(mars);
		// System.out.println("!!!!TEST FINISH!!!!");
		// System.out.println();

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