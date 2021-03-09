import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;


class Controller extends Thread{
    ArrayList<Mission> missions;
    EventLog eventLog;
    Queue<DataTransmission> inbox;
    StarSystem solarSystem;
    Clock clock;
    int nextId = 0;

    Controller(EventLog eventLog, StarSystem starSystem){
        this.missions = new ArrayList<Mission>();
        this.eventLog = eventLog;
        this.inbox = new LinkedList<DataTransmission>(); 
        this.solarSystem = starSystem;
        this.clock = new Clock("clock");
    }   

    public void run(){
        eventLog.writeFile("Command centre initialising...");
        while (true){
            // Check inbox
            for (DataTransmission dataTransmission : inbox) {
                eventLog.writeFile(dataTransmission + " recieved by controller." );        // Need tostring for dataTransmission
                switch(dataTransmission.getType()){
                    case "telemetry":
                        this.checkTelemtry(dataTransmission);
                        break; 
                    case "SOS_Report":
                        Mission mission = dataTransmission.getMission();
                        this.createSoftwareUpdate(mission);
                        break; 
                    case "spacecraftUpdate":
                        // Check if stage is changing 
                        String content = dataTransmission.getContent();
                        int i = content.indexOf(' ');
                        if (content.substring(0, 2).equals("Stage change required")){
                            this.changeMissionStage(dataTransmission);
                        }
                        break;
                }

            }
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            

        }
    }

    private void changeMissionStage(DataTransmission dataTransmission) {
    }

    private void checkTelemtry(DataTransmission dataTransmission) {
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

    public void createMission(Scanner userInput){
        // Get mission details from commander (user)
        System.out.println("\nEnter mission name:");
        String name = userInput.nextLine();  

        System.out.println("\nEnter mission destination:");
        String destination = userInput.nextLine();

        Mission newMission = new Mission(
            generateId(name),
            name,
            this.solarSystem.getSystemObjects().get("Earth"), 
            this.solarSystem.getSystemObjects().get(destination), 
            clock.getTicks(), 
            eventLog);
        // Add to active missions
        this.missions.add(newMission);
        // Start mission thread
        newMission.start();
        System.out.println("\nNew mission to\n" + newMission.destination + "\nInitiation successful");
        eventLog.writeFile("Mission '" +  newMission.name + "' to destination " + newMission.destination + " created.");
    }

    public DataTransmission createSoftwareUpdate(Mission mission){
        String content = mission.name + " software upgrade ";
        String type = "upgrade";
        DataTransmission swUpdate = new DataTransmission(mission, type, content);
        //............building sw update..........
        // Create new thread to burn CPU time? New class?
        return swUpdate;
    }
}