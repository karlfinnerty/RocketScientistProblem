import java.util.concurrent.LinkedBlockingQueue; 

public class Network extends Thread{
	Controller controller;
    Mission mission;
    LinkedBlockingQueue<DataTransmission> buffer;

    Network(Controller controller, Mission mission){
        this.controller = controller;
        this.mission = mission;
        this.buffer = new LinkedBlockingQueue<DataTransmission>(); ; 
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
                sendFile(dataTransmission);
                buffer.remove(dataTransmission);
            }
        }
    }

    private void sendFile(DataTransmission dataTransmission) {       //send file will also be parameterised by Connection once those are set up 
        String reciever = dataTransmission.getReciever();
        if (reciever.equals("mission")) {
            this.mission.recieveFile(dataTransmission);
        }
        if (reciever.equals("controller")) {
            this.controller.recieveFile(dataTransmission);
        }
    }

}