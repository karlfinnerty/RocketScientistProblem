import java.time.Instant;
import java.util.Random;

public class Stage {
    Mission mission;
    String stages[] = {"prelaunch", "boost", "transit", "landing", "exploration"};
    Integer currentStage;
    double duration[];
    long stageStartTime;

    Stage(Mission mission){
        this.mission = mission;
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

        return new double[]{prelaunch, boost, transit, landing, exploration};
    }

    public void incrementStage(){
        if (currentStage < 4){
            currentStage++;
            // reset stage time counter 
            stageStartTime = this.mission.clock.getTicks();
        }
        else{
            completeMission();
        }
    }

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
