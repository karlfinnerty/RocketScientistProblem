/*
##########################################################################
CA4006 - Rocket Scientist Problem
Created by Paul Treanor and Karl Finnerty

Description:
	Routes DataTranmission through appropriate connection

##########################################################################
*/
import java.util.Random;

public class Antenna{
    EventLog eventLog;
    Connection lowBWConnection; 
	Connection midBWConnection; 
	Connection highBWConnection;
    Controller controller;

    public Antenna(Controller controller, EventLog eventLog){
        this.controller = controller;
        this.eventLog = eventLog;
        this.lowBWConnection = new Connection(0.999, 20, controller); //20bits
        this.midBWConnection = new Connection(0.9, 16000 , controller); //2kb
        this.highBWConnection = new Connection(0.8, 16000000 , controller); //2mb
    }

    public void runner(DataTransmission dataTransmission){
        Connection choosenConnection = chooseConnection(dataTransmission);
            // Calculate arrivalTime of dataTransmission
            dataTransmission.calculateArrivalTime(choosenConnection.bandwidth);
            
            if (dataTransmission.arrivalTime < controller.clock.getTicks()){
                eventLog.writeFile("Sending " + dataTransmission.getType() + " to " + dataTransmission.reciever +  "across network " + choosenConnection);
                Boolean connected = false;
                while(!connected){
                    connected = transmitAttempt(dataTransmission, choosenConnection);
                } 
                choosenConnection.sendFile(dataTransmission);
            }
    }

    private Boolean transmitAttempt(DataTransmission dataTransmission, Connection connection) {
        double chance = connection.availability;
        // Decide if component will fail given a chance based off connection availability
		Random rand = new Random();
		double failRandomNumber = rand.nextDouble();
		if(failRandomNumber <= chance){
			return true;
		}
        eventLog.writeFile(dataTransmission.toString() + " failed to send across network!");
        return false;
    }

    private Connection chooseConnection(DataTransmission dataTransmission) {
        // Small telemtries are sent across most reliable network
        String dataType = dataTransmission.getType();
        if (dataTransmission.getBitSize() < 8400){
            return this.lowBWConnection;
        }
        
        if (dataType.equals("report") || dataType.equals("telemetry") || dataType.equals("stageChange")){
            return this.midBWConnection;
        }
        if (dataType.equals("swUpdate")){
            return this.highBWConnection;
        }
        // Default
        return this.highBWConnection;
    }


}