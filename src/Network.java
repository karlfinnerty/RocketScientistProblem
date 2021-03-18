import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class Network implements Runnable{
	Controller controller;
    Mission mission;
    LinkedBlockingQueue<DataTransmission> buffer;
    Connection lowBWConnection;
    Connection midBWConnection;
    Connection highBWConnection;
    EventLog eventLog;

    Network(Controller controller, Mission mission, EventLog eventLog){
        this.controller = controller;
        this.mission = mission;
        this.buffer = new LinkedBlockingQueue<DataTransmission>(); ; 
        this.eventLog = eventLog;
        // Set up connections 
        this.lowBWConnection = new Connection(0.999, 20, controller, mission); //20bits
        this.midBWConnection = new Connection(0.9, 16000 , controller, mission); //2kb
        this.highBWConnection = new Connection(0.8, 16000000 , controller, mission); //2mb
    }

    public void postFiles(DataTransmission dataTransmission) {
        buffer.add(dataTransmission);        
    }

    public void run() {
        while (true){
            // Is this a potential concurrency issue? Fairness maybe. 
            for (DataTransmission dataTransmission : buffer) {
                // figure out what connection is needed 
                Connection choosenConnection = chooseConnection(dataTransmission);
                // calculate arrivalTime of dataTransmission
                //dataTransmission.calculateArrivalTime(choosenConnection.bandwidth);

                
                if (dataTransmission.arrivalTime < this.mission.clock.getTicks()){
                    eventLog.writeFile("Sending " + dataTransmission.getType() + " across network " + choosenConnection);
                    Boolean connected = false;
                    while(!connected){
                        connected = transmitAttempt(dataTransmission, choosenConnection);
                    } 
                    choosenConnection.sendFile(dataTransmission);
                    buffer.remove(dataTransmission);
                }
            }
        }
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

    

}