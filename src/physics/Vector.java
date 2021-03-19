/*
##########################################################################
CA4006 - Rocket Scientist Problem
Created by Paul Treanor and Karl Finnerty

Description:
	Generic vector class.

##########################################################################
*/
package physics;

import java.lang.Math;
// Class for n dimensional vectors
public class Vector{
	double[] dimensions;

	public Vector(double[] dimensions){
		this.dimensions = dimensions;
	}

	public double[] get(){
		return this.dimensions;
	}

	public void set(double[] dimensions){
		this.dimensions = dimensions;
	}

	// Add this vector with another vector
	public Vector add(Vector other){
		assert this.dimensions.length == other.dimensions.length;
		double[] resultantDimensions = new double[this.dimensions.length];
		for(int i = 0; i < this.dimensions.length; i++){
			resultantDimensions[i] = this.dimensions[i] + other.dimensions[i];
		}
		return new Vector(resultantDimensions);
	}

	// Subtract another vector from this vector
	public Vector subtract(Vector other){
		assert this.dimensions.length == other.dimensions.length;
		double[] resultantDimensions = new double[this.dimensions.length];
		for(int i = 0; i < this.dimensions.length; i++){
			resultantDimensions[i] = this.dimensions[i] - other.dimensions[i];
		}
		return new Vector(resultantDimensions);
	}

	// Get the dot product of two vectors
	public double dot(Vector other){
		assert this.dimensions.length == other.dimensions.length;
		double dotProduct = 0.0;
		for(int i = 0; i < this.dimensions.length; i++){
			dotProduct += this.dimensions[i] * other.dimensions[i];
		}
		return dotProduct;
	}

	// Get the cross product of two vectors
	public Vector cross(Vector other){
		assert this.dimensions.length == other.dimensions.length;
		double[] resultantDimensions = new double[this.dimensions.length];
		for(int i = 0; i < this.dimensions.length; i++){
			resultantDimensions[i] = this.dimensions[i] * other.dimensions[i];
		}
		return new Vector(resultantDimensions);
	}

	// Get magnitude
	public double magnitude(){
		double sum = 0.0;
		for(double component:this.dimensions){
			sum += Math.pow(component, 2);
		}
		return Math.sqrt(sum);
	}
}