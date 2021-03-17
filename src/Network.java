import java.util.concurrent.LinkedBlockingQueue;

public class Network extends Thread{
	Controller controller;
    Mission mission;
    LinkedBlockingQueue<DataTransmission> buffer;
    Connection highBWConnection;
    Connection lowBWConnection;
    Connection midBWConnection;
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
                    choosenConnection.sendFile(dataTransmission);
                    buffer.remove(dataTransmission);
                }
                
            }
        }
    }

    private Connection chooseConnection(DataTransmission dataTransmission) {
        // Small essential or small telemtries are sent across most reliable network
        if (dataTransmission.getBitSize() < 8400){
            return this.lowBWConnection;
        }
        if (dataTransmission.getType().equals("report") || dataTransmission.getType().equals("temelemtry") ){
            return this.midBWConnection;
        }
        if (dataTransmission.getType().equals("update")){
            return this.highBWConnection;
        }
        // Default
        return highBWConnection;
    }

    

}