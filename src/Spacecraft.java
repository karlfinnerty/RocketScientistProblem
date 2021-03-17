import java.util.HashMap; 
import java.util.concurrent.LinkedBlockingQueue; 


public class Spacecraft{
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
	}

	public double getAcceleration(){
		return this.maxAcceleration;
	}

	public void setAcceleration(double acceleration){
		this.maxAcceleration = acceleration;
	}

	public void addComponent(Component component){
		this.components.put(component.getId(), component);
	}

	public void checkInbox(){
		//System.out.println("TEEEEEEST");
		for (DataTransmission dataTransmission : this.inbox) {
			eventLog.writeFile(dataTransmission + " recieved by " + this.mission.getMissionId());  
			switch(dataTransmission.getType()){
				case "telemetry":
					checkTelemetry(dataTransmission);
					break; 
				case "swUpdate":
					implementSwUpdate(dataTransmission);
					break; 
			}
			this.inbox.remove(dataTransmission);
		}
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

	private void implementSwUpdate(DataTransmission dataTransmission) {

    }
}