import java.util.ArrayList;
import java.util.Scanner;


class Controller {
    ArrayList<Mission> missions;
    EventLog eventLog;

    Controller(EventLog eventLog){
        this.missions = new ArrayList<Mission>();
        this.eventLog = eventLog;
    }   

    public void getMissions(){
        System.out.println("...gathering missions data...");
        for (Mission mission : missions) { 		      
            System.out.println(mission); 		
       }
    }

    public void createMission(Scanner userInput){
        // Get mission details from commander (user)
        System.out.println("\nEnter mission name:");
        String name = userInput.nextLine();  

        System.out.println("\nEnter mission destination:");
        String destination = userInput.nextLine();

        Mission newMission = new Mission(name, destination, eventLog);
        // Add to active missions
        this.missions.add(newMission);
        // Start mission thread
        newMission.start();
        System.out.println("\nNew mission to " + newMission.destination + " successfully made");
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