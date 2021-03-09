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

        Celestial sol = new Celestial(
            "Sol", 
            null, 
            null, 
            1.9885 * Math.pow(10, 30), 
            696342.0, 
            0.0, 
            0.0);

        Celestial earth = new Celestial(
            "Earth", 
            sol, 
            new Spherical(new double[]{149600000, 0.0, 0.0}), 
            5.98 * Math.pow(10, 24), 
            6371.0, 
            149600000, 
            149600000);

        Celestial mars = new Celestial(
            "Mars", 
            sol, 
            new Spherical(new double[]{249200000, 0.0, 0.0}), 
            6.4171 * Math.pow(10, 23), 
            3389.0, 
            249200000, 
            249200000);

        StarSystem solarSystem = new StarSystem(new Celestial[]{sol, earth, mars});
        Controller theController = new Controller(eventLog, solarSystem);
        
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
