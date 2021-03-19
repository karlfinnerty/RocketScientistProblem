import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import physics.Clock;
import physics.StarSystem;

class Controller extends Thread{
    String controllerId;
    ArrayList<Mission> missions;
    EventLog eventLog;
    LinkedBlockingQueue<DataTransmission> inbox;
    LinkedBlockingQueue<DataTransmission> outbox;
    StarSystem solarSystem;
    Clock clock;
    int nextId = 0;
    boolean exec = true;
    // System.out.println(Runtime.getRuntime().availableProcessors()); // Amount of cores varies depending on intel architeure... 
    ThreadPoolExecutor missionExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);    
    Antenna antenna;

    Controller(EventLog eventLog, StarSystem starSystem, Clock clock){
        this.missions = new ArrayList<Mission>();
        this.eventLog = eventLog;
        this.inbox = new LinkedBlockingQueue<DataTransmission>();
        this.outbox = new LinkedBlockingQueue<DataTransmission>();
        this.solarSystem = starSystem;
        this.clock = clock;
        this.controllerId = "ground_control";
        this.antenna = new Antenna(this, eventLog); 
    }   

    public void run(){
        eventLog.writeFile("Command centre initialising...");
        while (exec){
            if(!this.clock.isPaused()){
                    // Check inbox CRITICAL SECTION
                for (DataTransmission dataTransmission : inbox) {
                    eventLog.writeFile(dataTransmission + " recieved by controller." );     
                    readInboxItem(dataTransmission);
                    // Pop item from queue
                    inbox.remove(dataTransmission);
                }
                if(outbox.size() > 0){
                    processOutboxItems();
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
        missionExecutor.shutdown();   
    }

    // Process inbox item
    private void readInboxItem(DataTransmission dataTransmission) {
        switch(dataTransmission.getType()){
            case "stageChange":
                changeStage(dataTransmission);
                break;
            case "telemetry":
                this.checkTelemetry(dataTransmission);
                break; 
            case "report":
                Mission mission = dataTransmission.getMission();
                this.createSoftwareUpdate(mission);
                break; 
        }
    }

    private void processOutboxItems(){
        for (DataTransmission dataTransmission : outbox) {
            // figure out what connection is needed 
            antenna.runner(dataTransmission);
            this.outbox.remove(dataTransmission);
        }
    }

    private void changeStage(DataTransmission dataTransmission){
        Mission mission = dataTransmission.getMission();
        DataTransmission report = new DataTransmission(mission, "telemetry", "Stage change request accepted", mission.spacecraft.getSpacecraftId(), this.getControllerId());
        sendDataTransmission(mission, report);
        eventLog.writeFile("Stage change request from " + mission.getMissionId() + " accepted");
    }

    private void checkTelemetry(DataTransmission dataTransmission) {
        String content = dataTransmission.getContent();
        int i = content.indexOf(' ');
        String keyword = content.substring(0, i);
        Mission mission = dataTransmission.getMission();
        
        // Read content of telemetry to know what to do with it
        if (keyword.equals("SOS")){
            eventLog.writeFile("SOS request from " + mission.getMissionId() + " recieved");
            createSoftwareUpdate(mission);
        }
    }

    private void sendDataTransmission(Mission mission, DataTransmission dataTransmission) {
        outbox.add(dataTransmission);   
        mission.spacecraft.recieveFile(dataTransmission);
    }

    public void closeController(){
        this.exec = false;
    }

    public String getControllerId(){
        return this.controllerId;
    }

    public void getMissions(){
        System.out.println("...gathering missions data...");
        for (Mission mission : missions) { 		      
            System.out.println(mission.toString()); 		
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
        missionExecutor.execute(newMission);
        System.out.println("\nNew mission to\n" + newMission.destination + "\nFlight Parameters\n" + newMission.tof + "\nInitiation successful");
        eventLog.writeFile("Mission '" +  newMission.name + "' to destination " + newMission.destination + " created.");
    }

    public void createSoftwareUpdate(Mission mission){
        String content = mission.getMissionId() + " software update ";
        DataTransmission swUpdate = new DataTransmission(mission, "swUpdate", content, mission.spacecraft.getSpacecraftId(), this.getControllerId());
        sendDataTransmission(mission, swUpdate);
    }

    public void recieveFile(DataTransmission dataTransmission){
        this.inbox.add(dataTransmission);
    }
}