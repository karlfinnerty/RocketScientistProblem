import java.util.Scanner;

public class CLI {
    StarSystem solarSystem;
    Controller theController;
    EventLog eventLog;

    public CLI(Controller controller, EventLog eventLog, StarSystem starSystem){
        this.theController = controller;
        this.eventLog = eventLog;
        this.solarSystem = starSystem;
    }

    public boolean validDestination(String dest){
        return this.solarSystem.getSystemObjects().containsKey(dest);
    }

    public void run() {

        Scanner userInput = new Scanner(System.in);   
        // Initialise output file
        
        this.theController.start();
        
        boolean exec = true;
        while (exec) { 
            System.out.println("\nGreetings Commander, \n" 
                                + "Type an instruction (shortened): \n"
                                + "     new mission (nm) \n"
                                + "     list missions (ls) \n"
                                + "     print event log (log)"
            );
            
            String instr = userInput.nextLine().toLowerCase();

            switch(instr){
                case "t":
                case "test":
                    System.out.println("TEST");
                    break; 
                case "nm":
                case "new mission":
                    System.out.println("\nEnter mission name:");
                    String name = userInput.nextLine().toLowerCase();
                    String destination = "none";
                    while(!validDestination(destination) && !destination.equals("cancel")){
                        System.out.println("\nEnter mission destination:");
                        destination = userInput.nextLine().toLowerCase();
                        if(validDestination(destination)){
                            theController.createMission(name, destination);
                        }else if(destination.equals("cancel")){
                            System.out.println("Mission aborted.");
                        }else {
                            System.out.println("Invalid destination.");
                            System.out.println("Choose from the following: mars, neptune, cancel");
                        }
                    }
                    break; 
                case "ls":
                case "list missions":
                    theController.getMissions();
                    break; 
                case "log":
                case "print event log":
                    this.eventLog.readFile();
                    break;
                case "exit":
                    exec = false;
                    break;
                default:
                    System.out.println("Not a valid instruction sir!");
            }        
        }      
    }
}
