import java.time.Instant;

public class Stage {
    Mission mission;
    String stages[] = {"prelaunch", "boost", "transit", "landing", "exploration"};
    Integer currentStage;
    Integer duration[] = {1000, 1000, 1000, 1000, 1000,};
    Instant stageStartTime;

    Stage(Mission mission){
        this.mission = mission;
        this.currentStage = 0;
        this.stageStartTime = Instant.now();
    }

    public void incrementStage(){
        if (currentStage <= 3){
            currentStage++;
            // reset stage time counter 
            stageStartTime = Instant.now();
        }
        else{
            completeMission();
        }
    }

    private void completeMission() {
        mission.missionComplete = true;
    }

    public Instant getStartTime() {
        return stageStartTime;
    }

    public Integer getDuration(){
        return duration[currentStage];
    }

    public String getStage(){
        return stages[currentStage];
    }

}
