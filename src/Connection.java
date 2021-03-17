public class Connection {
    double availability;
    Integer bandwidth; //in bits 
    Controller controller;
    Mission mission;
    Integer transmissionTime = 200;

    Connection(double d, Integer bandwidth, Controller controller, Mission mission) {
        this.availability = d;
        this.bandwidth = bandwidth;
        this.controller = controller;
        this.mission = mission;
    }

    public void sendFile(DataTransmission dataTransmission) {       //send file will also be parameterised by Connection once those are set up              
        String reciever = dataTransmission.getReciever();
        if (reciever.equals("mission")) {
            this.mission.recieveFile(dataTransmission);
        }
        if (reciever.equals("controller")) {
            this.controller.recieveFile(dataTransmission);
        }
    }

    public String toString(){
        if (this.bandwidth == 20){
            return "Low Bandwidth";
        }
        if (this.bandwidth == 16000){
            return "Low Bandwidth";
        }
        if (this.bandwidth == 16000000){
            return "Low Bandwidth";
        }
        return "Unknown";
    }
}
