import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.LinkedBlockingQueue; 
import java.lang.Math;

public class Mission extends Thread{
    String id;                  // Unique mission ID assigned by controller
    String name;                // Mission alias given by user
    Controller controller;      // Controller that created mission
    Spacecraft spacecraft;      // Spacecraft assigned to mission by controller
    Network toMissionNetwork;
    Network fromMissionNetwork;        
    Celestial source;           // Source location of mission
    Celestial destination;      // Destination location of mission
    int currentStage;
    long startTime;             // The value of Clock (number of ticks passed) when mission is created
    long startTime;             // The value of Clock (number of ticks passed) when stage goes from preflight -> boost
                                // The stage mission is currently in e.g. prelaunch, boost, transit, landing, exploration
    Stage stage;          
    EventLog eventLog;
    LinkedBlockingQueue<DataTransmission>  inbox;

    double G = 6.673 * Math.pow(10, -11);
    double tof;                 // Time of flight for transit stage of mission
    double finalPhaseAngle;    // Angle needed between source and destination at time of launch
    double launchWindow;        // Time of launch i.e. number of seconds from start until angle between source and destination = finalPhaseAngle

    Boolean missionComplete;
    

    Mission(Controller controller, String id, String name, Celestial source, Celestial destination, long startTime, EventLog eventLog){
        this.id = id;
        this.name = name;
        this.controller = controller;
        //this.spacecraft = spacecraft;
        this.source = source;
        this.destination = destination;
        this.stage = new Stage(this);
        this.startTime = startTime;
        this.eventLog = eventLog;
        this.inbox = new LinkedBlockingQueue<DataTransmission>(); ; 
        //mission creates two networks, one for connections from M to C, the other fro connections from C to M 
        this.toMissionNetwork = new Network(controller, this, eventLog);
        toMissionNetwork.start();
        this.fromMissionNetwork = new Network(controller, this, eventLog);
        fromMissionNetwork.start();
        this.missionComplete = false;
    }

    public void run(){
        while (missionComplete==false){
            // check stage duration
            checkStageDuration();
            //System.out.println(destination);
            // Check inbox
            for (DataTransmission dataTransmission : inbox) {
                eventLog.writeFile(dataTransmission + " recieved by " + this.name );  
                switch(dataTransmission.getType()){
                    case "telemetry":
                        this.checkTelemetry(dataTransmission);
                        break; 
                    case "swUpdate":
                        this.implementSwUpdate(dataTransmission);
                        break; 
                }
                inbox.remove(dataTransmission);
            }

            DataTransmission report = new DataTransmission(this, "telemetry", "this is the content", "controller");
            sendDataTransmission(report);
            
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        eventLog.writeFile(this.name + " has completed!");
    }

    private void checkStageDuration() {
        Instant now = Instant.now();
        long s = Duration.between(stage.getStartTime(), now).toSeconds();
        if (s > stage.getDuration()){
            stage.incrementStage();
            eventLog.writeFile(name + " moved onto " + stage.getStage() + " stage!");
        }
    }

    private void implementSwUpdate(DataTransmission dataTransmission) {
    }

    private void checkTelemetry(DataTransmission dataTransmission) {
        // Read telemetry content
        String content = dataTransmission.getContent();
        int i = content.indexOf(' ');
        String keyword = content.substring(0, i);

        if (keyword.equals("Stage")){           // "Stage change request accepted"
            this.changeMissionStage(dataTransmission);
        }
        if (keyword.equals("Component")){        // "Component <name of component> seems OK"
        
        }
    }

    private void changeMissionStage(DataTransmission dataTransmission) {
    }

    public String toString(){
        return this.id + " "+ getStage();
       }
       
    public String getMissionId(){
        return this.id;
    }

    public String getStage(){
        return this.stage.getStage();
    }

    public Network getToMissionNetwork() {
        return this.toMissionNetwork;
    }

    private void sendDataTransmission(DataTransmission dataTransmission) {    
        Network network = this.fromMissionNetwork;
        network.postFiles(dataTransmission);
    }

    public void recieveFile(DataTransmission dataTransmission){
        this.inbox.add(dataTransmission);
    }

    // Calculate optimal transit to destination
    // frame = arbitrary frame of reference where we want target to be in relation to source in degrees
    public void calculateHohmannTransfer(double frame){
        // Need orbital radius of source R1 (likely earth) and target R2. Also G times mass of sun -> GM
        double r1 = this.source.getOrbitalRadius();
        double r2 = this.destination.getOrbitalRadius();
        double gm = this.source.getPrimary().getMass() * G;

        // Get orbital period of source and target to seconds
        double p1 = this.source.getOrbitalPeriod();
        double p2 = this.destination.getOrbitalPeriod();

        // Calculate semi major axis (distance from centre of ellipse to most distant edge) of transfer orbit: a(transfer) = (R1 + R2 ) / 2
        double a = (r1 + r2) / 2;
        // Calculate period of transfer orbit using formula: P(transfer) =√(4π²·a³/GM )
        double p = Math.sqrt(((4*Math.pow(Math.PI, 2))*Math.pow(a, 3)) / (gm));

        // Calculate velocity of source and target orbit
        double v1 = (Math.PI * 2 * r1) / p1;
        double v2 = (Math.PI * 2 * r2) / p2;

        // Find v of transfer orbit at closest and furtherst point to sun (perihelion & aphelion): vp = (2π x a(transfer) / P(transfer) ) x √( (2a(transfer) / R1) - 1)
        double vp = ((Math.PI * 2 * a)/p) * Math.sqrt(((a * 2) / r1) - 1);
        double va = ((Math.PI * 2 * a)/p) * Math.sqrt(((a * 2) / r2) - 1);

        // Calculate amount of delta v (change in velocity) needed to enter and exit transfer orbit. This will be used to calculate amount of fuel required.
        double dv1 = vp - v1;
        double dv2 = v2 - va;
        
        // Calculate time of flight (half the period of transfer orbit)
        double tof = p/2;
        this.tof = tof;

    public void completeMission(){
        missionComplete = true;
    }

        // Arbitrary frame of reference where we want target to be in relation to source in degrees
        // Angular distance travelled by target during tof
        double leadAngle = this.destination.getAngularVelocty() * tof;
        double initAngle = this.source.getPosition().getAzimuth() - this.destination.getPosition().getAzimuth();
        double finalPhaseAngle = initAngle - leadAngle;
        double launchWindow = (finalPhaseAngle - initAngle)/(v2 - v1);

        // If tof is 100 days, when is target 100 days from aphelion?
        // How do we find out position of spacecraft at aphelion
    } 

    public void calculateLaunchWindow(double tof, double finalPhaseAngle){

        return 
    }
}