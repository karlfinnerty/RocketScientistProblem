import java.time.Instant;

public class Stage {
    Mission mission;
    String stages[] = {"prelaunch", "boost", "transit", "landing", "exploration"};
    Integer currentStage;
    Integer duration[] = {2, 2, 10, 2, 10,};
    Instant stageStartTime;
    Integer transitDistance;

    Stage(Mission mission){
        this.mission = mission;
        this.currentStage = 0;
        this.stageStartTime = Instant.now();
        this.transitDistance = settransitDistance();
        setTransitDuration();
    }

    private void setTransitDuration() {
        Integer transitTime = transitDistance/10000000;
        duration[2] = transitTime;
    }

    private Integer settransitDistance(){
        Celestial destination = mission.destination;
        Celestial source = mission.source;
        return (int) destination.slDistance(source);
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
