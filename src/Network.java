import java.util.ArrayList;

public class Network{
	Controller controller;
    Mission mission;
    ArrayList<DataTransmission> buffer;

    Network(Controller controller, Mission mission){
        this.controller = controller;
        this.mission = mission;
        this.buffer = new ArrayList<DataTransmission>();
    }

    public void postFiles(DataTransmission dataTransmission) {
        // Add DT to buffer 
        // New method...for item in buffer
            // getConnection() - Figure out what connection it needs to be sent across on 
            // sendFile
        sendFile(dataTransmission);
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