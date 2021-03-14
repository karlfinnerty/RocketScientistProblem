import java.util.concurrent.LinkedBlockingQueue; 

public class Mission extends Thread{
    String id;                  // Unique mission ID assigned by controller
    String name;                // Mission alias given by user
    Controller controller;      // Controller that created mission
    Spacecraft spacecraft;      // Spacecraft assigned to mission by controller
    Network toMissionNetwork;
    Network fromMissionNetwork;        
    Celestial source;           // Source location of mission
    Celestial destination;      // Destination location of mission
    int currentStage;
    long startTime;             // The value of Clock (number of ticks passed) when stage goes from preflight -> boost
                                // The stage mission is currently in e.g. prelaunch, boost, transit, landing, exploration
    String stages[] = {"prelaunch", "boost", "transit", "landing", "exploration"};          
    EventLog eventLog;
    LinkedBlockingQueue<DataTransmission>  inbox;
    

    Mission(Controller controller, String id, String name, Celestial source, Celestial destination, long startTime, EventLog eventLog){
        this.id = id;
        this.name = name;
        this.controller = controller;
        //this.spacecraft = spacecraft;
        this.source = source;
        this.destination = destination;
        this.currentStage = 0;
        this.startTime = startTime;
        this.eventLog = eventLog;
        this.inbox = new LinkedBlockingQueue<DataTransmission>(); ; 
        //mission creates two networks, one for connections from M to C, the other fro connections from C to M 
        this.toMissionNetwork = new Network(controller, this, eventLog);
        toMissionNetwork.start();
        this.fromMissionNetwork = new Network(controller, this, eventLog);
        fromMissionNetwork.start();
    }

    public void run(){
        while (true){
            //System.out.println(destination);
            // Check inbox
            for (DataTransmission dataTransmission : inbox) {
                eventLog.writeFile(dataTransmission + " recieved by " + this.name );  
                switch(dataTransmission.getType()){
                    case "telemetry":
                        this.checkTelemetry(dataTransmission);
                        break; 
                    case "swUpdate":
                        this.implementSwUpdate(dataTransmission);
                        break; 
                }
                inbox.remove(dataTransmission);

            }
            DataTransmission report = new DataTransmission(this, "telemetry", "this is the content", "controller");
            sendDataTransmission(report);
            
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            eventLog.writeFile(this.name + " is still running!");
        }
    }

    private void implementSwUpdate(DataTransmission dataTransmission) {
    }

    private void checkTelemetry(DataTransmission dataTransmission) {
        // Read telemetry content
        String content = dataTransmission.getContent();
        int i = content.indexOf(' ');
        String keyword = content.substring(0, i);

        if (keyword.equals("Stage")){           // "Stage change request accepted"
            this.changeMissionStage(dataTransmission);
        }
        if (keyword.equals("Component")){        // "Component <name of component> seems OK"
        
        }
    }

    private void changeMissionStage(DataTransmission dataTransmission) {
    }

    public String toString(){
        return this.id + " "+ getStage();
       }
       
    public String getMissionId(){
        return this.id;
    }

    public String getStage(){
        return this.stages[this.currentStage];
    }

    public void progressStage(){
        this.currentStage ++;
    }

    public Network getToMissionNetwork() {
        return this.toMissionNetwork;
    }

    private void sendDataTransmission(DataTransmission dataTransmission) {      // Mission always sends data transmissions back to controller, no need for extra parameter. 
        Network network = this.fromMissionNetwork;
        network.postFiles(dataTransmission);
    }

    public void recieveFile(DataTransmission dataTransmission){
        this.inbox.add(dataTransmission);
    }

    

    // Vector calculateTrajectory(){

    // }
}