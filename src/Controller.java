import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import physics.Clock;
import physics.StarSystem;

class Controller extends Thread{
    ArrayList<Mission> missions;
    EventLog eventLog;
    LinkedBlockingQueue<DataTransmission> inbox;
    StarSystem solarSystem;
    Clock clock;
    int nextId = 0;

    Controller(EventLog eventLog, StarSystem starSystem, Clock clock){
        this.missions = new ArrayList<Mission>();
        this.eventLog = eventLog;
        this.inbox = new LinkedBlockingQueue<DataTransmission>();
        this.solarSystem = starSystem;
        this.clock = clock;
    }   

    public void run(){
        eventLog.writeFile("Command centre initialising...");
        while (true){
            if(!this.clock.isPaused()){
                    // Check inbox CRITICAL SECTION
                for (DataTransmission dataTransmission : inbox) {
                    eventLog.writeFile(dataTransmission + " recieved by controller." );     
                    readInboxItem(dataTransmission);
                    // Pop item from queue
                    inbox.remove(dataTransmission);
                }
                clock.increment();
                this.solarSystem.updatePositions();
            }
            
            try{
                Thread.sleep(clock.getDelay());
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }   
    }

    // Decide what to do with inbox item
    private void readInboxItem(DataTransmission dataTransmission) {
        switch(dataTransmission.getType()){
            case "telemetry":
                this.checkTelemetry(dataTransmission);
                break; 
            case "report":
                Mission mission = dataTransmission.getMission();
                this.createSoftwareUpdate(mission);
                break; 
            case "spacecraftUpdate":
                // Check if stage is changing 
                String content = dataTransmission.getContent();
                int i = content.indexOf(' ');
                if (content.substring(0, i).equals("Stage")){
                    this.changeMissionStage(dataTransmission);
                }
                break;
        }
    }

    private void changeMissionStage(DataTransmission dataTransmission) {
    }

    private void checkTelemetry(DataTransmission dataTransmission) {
        // Read telemetry content
        String content = dataTransmission.getContent();
        int i = content.indexOf(' ');
        String keyword = content.substring(0, i);
        Mission mission = dataTransmission.getMission();

    
        if (keyword.equals("Stage")){           // "Stage change request accepted"
            DataTransmission report = new DataTransmission(mission, "telemetry", "Stage change request accepted", "mission");
            sendDataTransmission(mission, report);
            eventLog.writeFile("Stage change request from " + mission.getMissionId() + " accepted");
        }
    }

    private void sendDataTransmission(Mission mission, DataTransmission dataTransmission) {
        Network network = mission.getSpacecraftToControllerNet();
        network.postFiles(dataTransmission);
    }

    public void getMissions(){
        System.out.println("...gathering missions data...");
        for (Mission mission : missions) { 		      
            System.out.println(mission); 		
       }
    }

    public String generateId(String name){
        this.nextId ++;
        return name + "-" + nextId;
    }

    public void createMission(String name, String destination){
        // Get mission details from commander (user)

        Mission newMission = new Mission(
            this, 
            generateId(name),
            name,
            this.solarSystem.getSystemObjects().get("earth"), 
            this.solarSystem.getSystemObjects().get(destination),
            this.clock, 
            this.eventLog);
        // Add to active missions
        this.missions.add(newMission);
        // Start mission thread
        newMission.start();
        System.out.println("\nNew mission to\n" + newMission.destination + "\nFlight Parameters\n" + newMission.tof + "\nInitiation successful");
        eventLog.writeFile("Mission '" +  newMission.name + "' to destination " + newMission.destination + " created.");
    }

    public DataTransmission createSoftwareUpdate(Mission mission){
        String content = mission.name + " software upgrade ";
        String type = "upgrade";
        DataTransmission swUpdate = new DataTransmission(mission, type, content, "mission");
        //............building sw update..........
        // Create new thread to burn CPU time? New class?
        return swUpdate;
    }

    public void recieveFile(DataTransmission dataTransmission){
        this.inbox.add(dataTransmission);
    }
}