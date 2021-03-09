public class Mission extends Thread{
    String id;                  // Unique mission ID assigned by controller
    String name;                // Mission alias given by user
    //Controller controller;      // Controller that created mission
    Spacecraft spacecraft;      // Spacecraft assigned to mission by controller
    Network [] networks;        // Networks assigned to mission by controller
    Celestial source;           // Source location of mission
    Celestial destination;      // Destination location of mission
    int currentStage;
    long startTime;             // The value of Clock (number of ticks passed) when stage goes from preflight -> boost
                                // The stage mission is currently in e.g. prelaunch, boost, transit, landing, exploration
    String stages[] = {"prelaunch", "boost", "transit", "landing", "exploration"};          
    EventLog eventLog;

    Mission(String id, String name, Celestial source, Celestial destination, long startTime, EventLog eventLog){
        this.id = id;
        this.name = name;
        //this.controller = controller;
        //this.spacecraft = spacecraft;
        //this.networks = networks;
        this.source = source;
        this.destination = destination;
        this.currentStage = 0;
        this.startTime = startTime;
        this.eventLog = eventLog;
    }

    public void run(){
        while (true){
            //System.out.println(destination);
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            eventLog.writeFile("Mission still running!");
        }
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

    // Vector calculateTrajectory(){

    // }
}