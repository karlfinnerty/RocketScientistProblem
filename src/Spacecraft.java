import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class Spacecraft{
	String id;
	Mission mission;
	EventLog eventLog;
	HashMap<String, Component> components = new HashMap<String, Component>();
	LinkedBlockingQueue<DataTransmission> inbox = new LinkedBlockingQueue<DataTransmission>();
	LinkedBlockingQueue<DataTransmission> outbox = new LinkedBlockingQueue<DataTransmission>();
	double distance;
	double maxAcceleration;
	Connection spacecraftConnection; 
	Connection lowBWConnection; 
	Connection midBWConnection; 
	Connection highBWConnection; 

	//tranmsitQueue

	public Spacecraft(Mission mission, EventLog eventLog){
		this.mission = mission;
		this.eventLog = eventLog;
		this.id = "LE" + "-" + this.mission.getMissionId();
		this.spacecraftConnection = new Connection(1.0, 16000000, mission.controller);
		this.lowBWConnection = new Connection(0.999, 20, mission.controller); //20bits
        this.midBWConnection = new Connection(0.9, 16000 , mission.controller); //2kb
        this.highBWConnection = new Connection(0.8, 16000000 , mission.controller); //2mb
	}

	public String getSpacecraftId(){
		return this.id;
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

	public void processOutboxItems(){
        for (DataTransmission dataTransmission : outbox) {
            // figure out what connection is needed 
            Connection choosenConnection = chooseConnection(dataTransmission);//this.highBWConnection;
            // calculate arrivalTime of dataTransmission
            dataTransmission.calculateArrivalTime(choosenConnection.bandwidth);
            
            if (dataTransmission.arrivalTime < this.mission.clock.getTicks()){
                eventLog.writeFile("Sending " + dataTransmission.getType() + " across network " + choosenConnection);
                Boolean connected = false;
                while(!connected){
                    connected = transmitAttempt(dataTransmission, choosenConnection);
                } 
                choosenConnection.sendFile(dataTransmission);
                outbox.remove(dataTransmission);
            }
        }
    }

	private Connection chooseConnection(DataTransmission dataTransmission) {
        // Small essential or small telemtries are sent across most reliable network
        if (dataTransmission.getBitSize() < 8400){
            return this.lowBWConnection;
        }
        if (dataTransmission.getType().equals("report") || dataTransmission.getType().equals("telemetry") || dataTransmission.getType().equals("stageChange")){
            return this.midBWConnection;
        }
        if (dataTransmission.getType().equals("swUpdate")){
            return this.highBWConnection;
        }
        // Default
        return this.highBWConnection;
    }

	private Boolean transmitAttempt(DataTransmission dataTransmission, Connection connection) {
        double chance = connection.availability;
        // Decide if component will fail given a chance. 0.5 = 50% chance of failure, 0.1 = 10% chance
		Random rand = new Random();
		double failRandomNumber = rand.nextDouble();
		if(failRandomNumber <= chance){
			return true;
		}
        eventLog.writeFile(dataTransmission.toString() + " failed to send across network!");
        return false;
    }

	public void enqueueTransmit(String transmissionType, String content) throws InterruptedException{
		DataTransmission tx = new DataTransmission(
			this.mission, 
			transmissionType, 
			content, 
			this.mission.controller.getControllerId(), 
			getSpacecraftId());

		this.outbox.put(tx);
	}

	public void sendDataTransmission(DataTransmission dataTransmission) {    
		eventLog.writeFile(dataTransmission.toString());
		outbox.add(dataTransmission);  
		this.mission.controller.recieveFile(dataTransmission);
        // Network network = this.mission.getSpacecraftToControllerNet();
        // network.postFiles(dataTransmission);
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

	public Boolean requestSoftwareUpdate(){
		
		// make request
		DataTransmission swUpdateRequest = new DataTransmission(mission, "telemetry", "SOS - SW update needed", this.mission.controller.getControllerId(), this.id);
		// send request 
		sendDataTransmission(swUpdateRequest);
		// check inbox for swupdate (pause mission)
		Boolean response = false;
		DataTransmission update = new DataTransmission(mission, "", "", "", "");
		while(!response){
			for (DataTransmission dataTransmission : this.inbox) {
				if (dataTransmission.getType().equals("swUpdate")) {
					System.out.println("SWUPDATE RETURNED!");
					response = true;
					update = dataTransmission;
					this.inbox.remove(dataTransmission);
				}		
			}
		}
		return implementSwUpdate();
	}

	// All software updates are sent, only some pass 
	public Boolean implementSwUpdate() {
		// check type 
		double chance = 0.25;
        // Decide if component will fail given a chance. 0.5 = 50% chance of failure, 0.1 = 10% chance
		Random rand = new Random();
		double failRandomNumber = rand.nextDouble();
		if(failRandomNumber <= chance){
			eventLog.writeFile("Software Update Succeeded");
			// fail the mission
			return true;
		}
        eventLog.writeFile("Software Update Failed");
        return false;
    }
}