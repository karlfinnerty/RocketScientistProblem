/*
##########################################################################
CA4006 - Rocket Scientist Problem
Created by Paul Treanor and Karl Finnerty

Description:
	Manages resources for a mission.

##########################################################################
*/
import java.lang.Math;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import physics.Celestial;
import physics.Clock;

public class Mission implements Runnable{
    String id;                  // Unique mission ID assigned by controller
    String name;                // Mission alias given by user
    Controller controller;      // Controller that created mission
    Spacecraft spacecraft;      // Spacecraft assigned to mission by controller     
    Celestial source;           // Source location of mission
    Celestial destination;      // Destination location of mission
    Clock clock;                // 
    long startTime;             // The value of Clock (number of ticks passed) when mission is created
                                // The stage mission is currently in e.g. boost, transit, landing, exploration
    Stage stage;          
    EventLog eventLog;

    double G = 6.673 * Math.pow(10, -11);
    static double DAY = 86400.0;

    double distance;            // Distance of transit stage
    double tof;                 // Time of flight for transit stage of mission
    Boolean missionComplete;
    Boolean stageChangeRequest = false;
    Boolean missionFailed;
    ThreadPoolExecutor networkExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

    Mission(Controller controller, String id, String name, Celestial source, Celestial destination, Clock clock, EventLog eventLog){
        this.id = id;
        this.name = name;
        this.controller = controller;
        this.source = source;
        this.destination = destination;
        this.clock = clock;
        this.spacecraft = new Spacecraft(this, eventLog);
        buildSpacecraft(this.spacecraft);
        double flightParams[] = brachistochroneTrajectory(this.source, this.destination, this.spacecraft);
        this.tof = flightParams[0];
        this.distance = flightParams[1];
        assignFuel(spacecraft, tof);
        this.startTime = clock.getTicks();
        this.eventLog = eventLog;
        this.stage = new Stage(this, eventLog);
        this.missionComplete = false;
        this.missionFailed = false;
        
    }

    // Build spacecraft with a variable number of components based on mission details
    public void buildSpacecraft(Spacecraft spacecraft){
        Random rand = new Random();
        int thruster;

        // Add thrusters
        if(this.source == this.destination.getPrimary()){   // If destination is orbiting source (is a moon) assign 1-2 thrusters
            thruster = 1 + rand.nextInt(1);    

        } else {                                            // If destination is further away assign 2-4 thrusters
            thruster = 2 + rand.nextInt(2); 
        }
        spacecraft.setAcceleration(thruster * 10.0);        // calculate max acceleration of craft assuming each thruster produces ~1G 

        int control = (2 + rand.nextInt(2)) * 3;    // emulate triple modular redundency 6-12
        int instrument = 1 + rand.nextInt(5);       // arbitrary number of scientific instruments, 1-6
        int power = 2 + rand.nextInt(3);            // 1 power plant for spacecraft, 1 for rover, and a random number of backup plants

        for(int i = 0; i < thruster; i++){
            spacecraft.addComponent(new Component("thruster", i, 0));
        }

        for(int i = 0; i < control; i++){
            spacecraft.addComponent(new Component("control", i, 0));
        }

        for(int i = 0; i < instrument; i++){
            spacecraft.addComponent(new Component("instrument", i, 0));
        }

        for(int i = 0; i < power; i++){
            spacecraft.addComponent(new Component("power", i, 0));
        }

    }

    // Make the assumption that amount of fuel required is 1 per G of acceleration per second
    public void assignFuel(Spacecraft spacecraft, double tof){
        double fuel = tof*spacecraft.getAcceleration();
        spacecraft.addComponent(new Component("fuel", 0, fuel));
    }

    // Do the mission stuff
    public void run(){
        while (missionComplete==false && missionFailed==false){
            if(!this.clock.isPaused()){
                //System.out.println(destination);
                // Check inbox
                this.spacecraft.checkInbox();
                if(this.spacecraft.outbox.size() > 0){
                    this.spacecraft.processOutboxItems();
                }
                
                // check stage duration
                checkStageDuration();
            }
            try {
                Thread.sleep(clock.getDelay());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(missionComplete){
            eventLog.writeFile(this.id + " has completed :)");
        }else{
            eventLog.writeFile(this.id + " has failed :(");
        }
        
    }

    private void checkStageDuration() {
        long now = this.clock.getTicks();
        long s = now - stage.getStartTime();
        if (s > stage.getDuration() && stageChangeRequest == false ){
            // request stage change 
            this.spacecraft.createStageChangeRequest();
            stageChangeRequest = true;
        }
    }

    public double getTransitPercent(){
        return 0.0;
    }

    public void changeMissionStage() {
        stage.incrementStage();
        stageChangeRequest = false;
        eventLog.writeFile("***********" + this.id + " moved onto " + stage.getStage() + " stage! ***********");
    }

    public String toString(){
        if(this.missionFailed){
            return this.id + " " + "failed " +  "elapsed:" + (this.clock.getTicks() - this.startTime);
        }
        if(this.missionComplete){
            return this.id + " " + "complete " +  "elapsed:" + (this.clock.getTicks() - this.startTime);
        }
        return this.id + " "+ getStage()+ " " +  "elapsed:" + (this.clock.getTicks() - this.startTime);
       }
       
    public String getMissionId(){
        return this.id;
    }

    public String getStage(){
        return this.stage.getStage();
    }

    public double getTof(){
        return this.tof;
    }

    public void setMissionFailed(boolean value){
        this.missionFailed = value;
    }

    public double getDistance(){
        return this.distance;
    }

    // To simplify trajectory calculations, we will assume spacecraft use future technology that allows for constant acceleration, making the journey happen in as little time as possible
    public double [] brachistochroneTrajectory(Celestial source, Celestial destination, Spacecraft spacecraft){
        // calulate time of flight
        double maxA = 20.0; //spacecraft.getAcceleration();

        // Find optimal transit parameters (d = at^2/2)
        Celestial sourceCopy = source.getCopy();
        Celestial destCopy = destination.getCopy();
        double d =0 ;
        int t = 0;
        int timeOut = 1000000;  // if transit stage of mission cannot be completed within this time, 
        while(t < timeOut){
            destCopy.incrementAz(destCopy.getAngularVelocity());
            d = sourceCopy.slDistance(destCopy);
            if(d <= (maxA*Math.pow(t, 2))){ // Since acceleration = deceleration, simplify equation to d = at^2/2
                //System.out.println("Trajectory found...");
                return new double[]{t, d};
            }
            t++;
        }
        System.out.println("Trajectory not found...");
        return new double[]{0, 0};
        
    }

    public void completeMission(){
        this.missionComplete = true;
    }
}