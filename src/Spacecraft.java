import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue; 


public class Spacecraft{
	String id;
	Mission mission;
	EventLog eventLog;
	HashMap<String, Component> components = new HashMap<String, Component>();
	LinkedBlockingQueue<DataTransmission> inbox = new LinkedBlockingQueue<DataTransmission>();
	double distance;
	double maxAcceleration;
	//tranmsitQueue

	public Spacecraft(Mission mission, EventLog eventLog){
		this.mission = mission;
		this.eventLog = eventLog;
		this.id = "LE" + "-" + this.mission.getMissionId();
	}

	public double getAcceleration(){
		return this.maxAcceleration;
	}

	public String getSpacecraftId(){
		return this.id;
	}

	public void setAcceleration(double acceleration){
		this.maxAcceleration = acceleration;
	}

	public void addComponent(Component component){
		this.components.put(component.getId(), component);
	}

	public void createStageChangeRequest(){
		DataTransmission report = new DataTransmission(this.mission, "stageChange", "Stage change request", this.mission.controller.getControllerId(), getSpacecraftId());
        sendDataTransmission(report);
	}

	public void checkInbox(){
		for (DataTransmission dataTransmission : this.inbox) {
			eventLog.writeFile(dataTransmission + " recieved by " + this.mission.getMissionId());  
			switch(dataTransmission.getType()){
				case "telemetry":
					checkTelemetry(dataTransmission);
					break; 
				case "swUpdate":
					//implementSwUpdate();
					break; 
			}
			this.inbox.remove(dataTransmission);
		}
	}

	public void sendDataTransmission(DataTransmission dataTransmission) {    
		eventLog.writeFile(dataTransmission.toString());  
        Network network = this.mission.getSpacecraftToControllerNet();
        network.postFiles(dataTransmission);
    }

	public void recieveFile(DataTransmission dataTransmission){
        this.inbox.add(dataTransmission);
    }

	private void checkTelemetry(DataTransmission dataTransmission) {
        // Read telemetry content
        String content = dataTransmission.getContent();
        int i = content.indexOf(' ');
        String keyword = content.substring(0, i);

        if (keyword.equals("Stage")){           // "Stage change request accepted"
            this.mission.changeMissionStage();
        }
        if (keyword.equals("Component")){        // "Component <name of component> seems OK"
        
        }
    }

	public void requestSoftwareUpdate(DataTransmission dataTransmission){

	}

	public Boolean implementSwUpdate() {
		double chance = 1.0;
        // Decide if component will fail given a chance. 0.5 = 50% chance of failure, 0.1 = 10% chance
		Random rand = new Random();
		double failRandomNumber = rand.nextDouble();
		if(failRandomNumber <= chance){
			eventLog.writeFile("Software Update Succeeded");
			return true;
		}
        eventLog.writeFile("Software Update Failed");
        return false;
    }
}