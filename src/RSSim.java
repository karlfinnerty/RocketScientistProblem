// Round 1 Simplification: DONE
// All bodies are stationary (no temporal dimension) 
// No orbital inclination

// Round 2 Simplification: DONE
// Basic orbital physics
// Bodies follow circular orbits (velocity is constant)
// No orbital inclination

// Round 3 Final: DO IF LOADS OF TIME AT THE END (MAYBE SHOULDN'T DO THIS)
// Elliptical orbits
// Orbital inclination

public class RSSim{
	// Initialise solar system
	double G = 6.673 * Math.pow(10, -11); // Define the constant of gravitation in Nm^2/kg^2
	static double DAY = 86400.0;

	public static void main(String[] args) {
		Clock clock = new Clock("universe");
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
			5.98 * Math.pow(10, 24), 
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

		Celestial neptune = new Celestial(
			"neptune", 
			sol, 
			new Spherical(new double[]{4498396441.0, 0.0, 0.0}), 
			1.024 * Math.pow(10, 26), 
			24622.0, 
			4498396441.0, 
			4498396441.0);

		StarSystem solarSystem = new StarSystem(new Celestial[]{sol, earth, mars, neptune});

		// Do tests
		System.out.println("!!!!TEST START!!!!");
		System.out.println(earth.slDistance(sol));
		System.out.print("Earth speed(m/s): ");
		System.out.println(earth.coVelocity());
		System.out.print("Earth orbital period(days): ");
		System.out.println(earth.getOrbitalPeriod()/DAY);
		System.out.println();
		System.out.println("Satellite test: ");
		System.out.println(satellite.coVelocity());
		System.out.println(satellite.coOrbit());
		System.out.println();
		System.out.println("Earth stats at day 1: ");
		System.out.println(earth);
		System.out.println("Mars stats at day 1: ");
		System.out.println(mars);
		System.out.println();

		for(int ticks=0; ticks<175; ticks++){
			earth.incrementAz(earth.getAngularVelocity()*DAY);
			mars.incrementAz(mars.getAngularVelocity()*DAY);
		}

		System.out.println("Earth stats at day 175: ");
		System.out.println(earth);
		System.out.println("Mars stats at day 175: ");
		System.out.println(mars);
		System.out.println("!!!!TEST FINISH!!!!");
		System.out.println();

		// Initialise CLI
		EventLog eventLog = new EventLog();
    	Controller theController = new Controller(eventLog, solarSystem);
		CLI simUI = new CLI(theController, eventLog, solarSystem);
		simUI.run();
	}


	
}