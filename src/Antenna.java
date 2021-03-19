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

    Antenna(Controller controller, EventLog eventLog){
        this.controller = controller;
        this.eventLog = eventLog;
        this.lowBWConnection = new Connection(0.999, 20, controller); //20bits
        this.midBWConnection = new Connection(0.9, 16000 , controller); //2kb
        this.highBWConnection = new Connection(0.8, 16000000 , controller); //2mb
    }

    public void runner(DataTransmission dataTransmission){
        Connection choosenConnection = chooseConnection(dataTransmission);
            // calculate arrivalTime of dataTransmission
            dataTransmission.calculateArrivalTime(choosenConnection.bandwidth);
            
            if (dataTransmission.arrivalTime < controller.clock.getTicks()){
                eventLog.writeFile("Sending " + dataTransmission.getType() + " across network " + choosenConnection);
                Boolean connected = false;
                while(!connected){
                    connected = transmitAttempt(dataTransmission, choosenConnection);
                } 
                choosenConnection.sendFile(dataTransmission);
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