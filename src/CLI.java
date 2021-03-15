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
                    theController.createMission(userInput);   
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
