public class Connection {
    double availability;
    Integer bandwidth; //in bits 
    Controller controller;

    Connection(double d, Integer bandwidth, Controller controller) {
        this.availability = d;
        this.bandwidth = bandwidth;
        this.controller = controller;
    }

    public void sendFile(DataTransmission dataTransmission){
        String reciever = dataTransmission.getReciever();
        String controllerId = this.controller.getControllerId();
        if(reciever.equals(controllerId)){
            // It is being sent to controller
            this.controller.recieveFile(dataTransmission); 
        } else {
            dataTransmission.mission.spacecraft.recieveFile(dataTransmission);
        }
    }

    public String toString(){
        if (this.bandwidth == 20){
            return "Low Bandwidth";
        }
        if (this.bandwidth == 16000){
            return "Med Bandwidth";
        }
        if (this.bandwidth == 16000000){
            return "High Bandwidth";
        }
        return "Unknown";
    }
}
