import java.util.HashMap; 
import java.util.concurrent.LinkedBlockingQueue; 


public class Spacecraft{
	HashMap<String, Component> components = new HashMap<String, Component>();
	LinkedBlockingQueue<DataTransmission> inbox = new LinkedBlockingQueue<DataTransmission>();
	double distance;
	double maxAcceleration;
	//tranmsitQueue

	public double getAcceleration(){
		return this.maxAcceleration;
	}

	public void setAcceleration(double acceleration){
		this.maxAcceleration = acceleration;
	}


	public void addComponent(Component component){
		this.components.put(component.getId(), component);
	}
	
}