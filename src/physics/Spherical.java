package physics;

import java.lang.Math;
// Class for 3d spherical co-ordinate system
public class Spherical extends Vector{
	public Spherical(double[] dimensions){
		super(dimensions);
	}

	double distance = dimensions[0]; 		// radial distance - r
	double azimuth 	= dimensions[1];		// angle on x-y plane - theta  	RANGES FROM 0-360 degrees (0 < θ < 2π rad)
	double polar	= dimensions[2];		// elevation angle - phi 		RANGES FROM 0-180 degrees (0 < ϕ < 1π rad)

	public double getAzimuth(){
		return this.azimuth;
	}

	public void setAzimuth(double value){
		this.azimuth = value;
	}

	public String toString(){
		return "distance: " + this.distance + " azimuth: " + this.azimuth + " polar angle: " + this.polar;
	}

	// Magnitude = distance 
	public double magnitude(){
		return this.distance;
	}

	// Get distance between this and other spherical co-ordinates vector using following equation:
	// ∥r−r′∥ = √r2+r′2−2rr′[sin(θ)sin(θ′)cos(ϕ−ϕ′)+cos(θ)cos(θ′)]
	public double distance(Spherical other){
		// Rename variables for readability in equation, sticking with math conventions
		double r = this.distance;
		double theta = Math.toRadians(this.azimuth);
		double phi = Math.toRadians(this.polar);
		double rPrime = other.distance;
		double thetaPrime = Math.toRadians(other.azimuth);
		double phiPrime = Math.toRadians(other.polar);
		// Break down equation for readability
		double x1 = Math.pow(r, 2) + Math.pow(rPrime, 2.0); 
		double x2 = 2.0 * r * rPrime; 
		double x3 = Math.sin(theta)*Math.sin(thetaPrime)*Math.cos(phi-phiPrime); 
		double x4 = Math.cos(theta)*Math.cos(thetaPrime); 
		return Math.sqrt(x1 - x2 * (x3 + x4));
	}

	
	// Convert to cartesian co-ordinates vector
	// x = r(sin(phi)cos(theta))
	// y = r(sin(phi)sin(theta))
	// z = r(cos(phi))

}