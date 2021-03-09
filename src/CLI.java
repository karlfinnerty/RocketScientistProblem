import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class CLI {
    Queue<String> eventLog;

    CLI(){
        eventLog = new LinkedList<>();
    }
    public static void main(String[] args) {
        CLI cli = new CLI();

        Scanner userInput = new Scanner(System.in);   
        // Initialise output file
        EventLog eventLog = new EventLog();
        eventLog.writeFile("Command centre initialising...");

        Controller theController = new Controller(eventLog);
        
        while (true) { 
            System.out.println("\nGreetings Commander, \n" 
                                + "Type an instruction: \n"
                                + "     -'new mission' \n"
                                + "     -'list missions' \n"
                                + "     -'print event log'"
            );
            String instr = userInput.nextLine();

            switch(instr){
                case "new mission":
                    theController.createMission(userInput);   
                    break; 
                case "list missions":
                    theController.getMissions();
                    break; 
                case "print event log":
                    eventLog.readFile();
                    break;
                default:
                    System.out.println("Not a valid instruction sir!");
            }        
        }      
    }
}
