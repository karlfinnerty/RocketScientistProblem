import java.util.ArrayList;
import java.util.Scanner;


class Controller {
    ArrayList<Mission> missions;
    EventLog eventLog;
    StarSystem solarSystem;
    Clock clock;
    int nextId = 0;

    Controller(EventLog eventLog, StarSystem starSystem){
        this.missions = new ArrayList<Mission>();
        this.eventLog = eventLog;
        this.solarSystem = starSystem;
        this.clock = new Clock("clock");
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
        DataTransmission swUpdate = new DataTransmission(content);
        //............building sw update..........
        // Create new thread to burn CPU time? New class?
        return swUpdate;
    }
    
}