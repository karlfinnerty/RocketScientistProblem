import java.util.concurrent.LinkedBlockingQueue; 

public class Network extends Thread{
	Controller controller;
    Mission mission;
    LinkedBlockingQueue<DataTransmission> buffer;
    Connection highBWConnection;
    Connection lowBWConnection;
    Connection midBWConnection;

    Network(Controller controller, Mission mission){
        this.controller = controller;
        this.mission = mission;
        this.buffer = new LinkedBlockingQueue<DataTransmission>(); ; 
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
                // getConnection() - Figure out what connection it needs to be sent across on 
                // sendFile - realistically will be part of the Connection class/method
                this.highBWConnection.sendFile(dataTransmission);
                buffer.remove(dataTransmission);
            }
        }
    }

    

}