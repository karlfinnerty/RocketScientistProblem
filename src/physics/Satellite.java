package physics;

import java.lang.Math;
// A object in orbit with negligible mass and radius
public class Satellite{
	Celestial primary;		// body being orbited
	double periapsis;		// closest approach to primary in km
	double apoapsis;		// furthest approach from primary in km
	double orbitalRadius;
	double G = 6.673 * Math.pow(10, -11); // Define the constant of gravitation in Nm^2/kg^2

	public Satellite(Celestial primary, double periapsis, double apoapsis){
		// The primary of an astrobody is its 'parent' i.e. the cestial body it orbits.
		// Primary = null signifies that astrobody is stationary.
		this.primary = primary;
		this.periapsis = periapsis;
		this.apoapsis = apoapsis;
		if(this.periapsis == this.apoapsis){
			this.orbitalRadius = coRadius();
		}
	}

	// Calculate circualr orbital radius given height and radius of primary
	public double coRadius(){
		return (this.primary.getRadius() + this.periapsis) * 1000;
	}

	// Calculate circular orbit velocity of object. 
	// v: velocity(m/s), G: constant of gravitation(Nm^2/kg^2), M: mass of primary(kg), R: orbital radius(m)
	// v = sqrt(G*M/R)
	public double coVelocity(){
		assert this.periapsis == this.apoapsis;
		return Math.sqrt((G * this.primary.getMass()) / this.orbitalRadius);
	}

	// Calculate orbital period of object
	// T: period of orbit(s), R: orbital radius(m), G: constant of gravitation(Nm^2/kg^2), M: mass of primary(kg),
	// T = sqrt((4*pi^2*R^3)/(G*M))
	public double coOrbit(){
		assert this.periapsis == this.apoapsis;
		double r = this.orbitalRadius;
		double m = this.primary.getMass();
		return Math.sqrt((4*(Math.pow(Math.PI, 2))*(Math.pow(r, 3)))/(G*m));
	}
}