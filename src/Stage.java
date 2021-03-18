import java.time.Instant;
import java.util.Random;

public class Stage {
    Mission mission;
    EventLog eventLog;
    String stages[] = {"boost", "transit", "landing", "exploration"};
    Integer currentStage;
    double duration[];
    long stageStartTime;
    Boolean didSWUpdateWork = true;

    Stage(Mission mission, EventLog eventLog){
        this.mission = mission;
        this.eventLog = eventLog;
        this.currentStage = 0;
        this.stageStartTime = this.mission.clock.getTicks();
        this.duration = initDuration();
    }

    public double[] initDuration(){
        Random rand = new Random();
        double prelaunch = 0;
        double boost = 180 + rand.nextInt(1200);                       // Launch takes as little as 3 minutes and up to 23 minutes (random factors could be earths rotation, fragile payload etc)
        double transit = this.mission.getTof();                        // Transit time is calculated at mission start by finding shortest possible travel time given the planetary positions and spacecraft acceleration 
        double landing = 300 + rand.nextInt(3600);                     // Landing probably requires more time, slower descent, possbile delays due to atmospheric factors
        double exploration = 157 + rand.nextInt(15768);        // Exploration ranges from 6 months to 5 years

        return new double[]{boost, transit, landing, exploration};
    }

    public void incrementStage(){
        if (currentStage < 3){
            Boolean stageOk = changeStageAttempt();
            if (stageOk) {
                currentStage++;
                // Reset stage time counter
                stageStartTime = this.mission.clock.getTicks();
            }
            else {
                didSWUpdateWork = false;
                // The stage has failed. Attempt recovery by software update
                
                boolean recovered = this.mission.spacecraft.implementSwUpdate();
                if (!recovered) {
                    this.mission.setMissionFailed(true);
                } else{
                    didSWUpdateWork = true;
                }
                
                
            }
        }
        else{
            completeMission();
        }
    }

    private Boolean changeStageAttempt() {
        // 10% chance of failing per stage 
        double chance = 0.1;
		Random rand = new Random();
		double failRandomNumber = rand.nextDouble();
		if(failRandomNumber >= chance){
			return true;
		}
        System.out.println(this.mission.getMissionId() + " has failed! at " + this.mission.clock.getTicks());
        eventLog.writeFile(this.mission.getMissionId() + " stage " + getStage() + "failed." + " Requesting Software Update...");
        return false;
    }

    // private 

    private void completeMission() {
        mission.missionComplete = true;
    }

    public long getStartTime() {
        return stageStartTime;
    }

    public double getDuration(){
        return duration[currentStage];
    }

    public String getStage(){
        return stages[currentStage];
    }

}
