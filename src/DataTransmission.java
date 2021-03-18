import java.util.Random;

public class DataTransmission {
    String content;
    String type;
    Mission mission;
    Integer bitSize;
    String reciever;
    String sender;
    boolean requiresAck;
    long arrivalTime = 0;
    Float lightSpeed = (float) 299792.458;

    // Types are "report", "telemetry", "swUpdate" "changeStage"
    
    DataTransmission(Mission mission, String type, String content, String reciever, String sender) {
        this.content = content;
        this.type = type;
        this.mission = mission;
        this.reciever = reciever;
        this.sender = sender;
        setBitSize(type, this);
        this.arrivalTime = calculateArrivalTime(20);
    }

    private void setBitSize(String dTransmissionType, DataTransmission dataTransmission) {
        Integer byteSize = 0;
        if (dTransmissionType.equals("telemetry") || dTransmissionType.equals("stageChange")){
            byteSize = getRandom(100, 10000);
        }
        if (dTransmissionType.equals("report")){
            byteSize = getRandom(100000, 100000000);
        }
        if (dTransmissionType.equals("swUpdate")){
            byteSize = getRandom(1000000, 1000000000);
        }
        if (dTransmissionType == ""){
            byteSize = 1;
        }
        this.bitSize = byteSize*8;
    }

    public String getType(){
        return this.type;
    }

    public Integer getBitSize(){
        return this.bitSize;
    }

    public Mission getMission() {
        return this.mission;
    }

    public String getContent() {
        return this.content;
    }

    public String getReciever(){
        return this.reciever;
    }

    public String toString(){
        return "from: " + this.sender + "- to: " + this.reciever + "- arrival: " + this.arrivalTime + " - type: " + this.type + "- content: " + this.content;
       }

    private Integer getRandom(Integer min, Integer max){
        Random ran = new Random();
        Integer range = max - min + 1;
        Integer randomNum =  ran.nextInt(range) + min;
        return randomNum;
    }

    private long calculateDataTransitTime(){
        if(this.mission.stage.getStage().equals("prelaunch") || this.mission.stage.getStage().equals("boost")){
            return 1;
        } else if(this.mission.stage.getStage().equals("landing") || this.mission.stage.getStage().equals("exploration")){
            return (long) this.mission.destination.slDistance(this.mission.source);
        }
        long transitStart = this.mission.stage.getStartTime();
        double tof = this.mission.getTof();
        long now = this.mission.clock.getTicks();
        return (long) ((now - transitStart)/tof);
    }

    public long calculateArrivalTime(Integer bandwidth){
        long currentTime = this.mission.clock.getTicks();
        Integer burstTime = bitSize/bandwidth;        

        //System.out.println(bitSize + " " + burstTime);
        long transitTime = calculateDataTransitTime();
        transitTime = (long) (transitTime/lightSpeed);
        // arrivalTime of data is current time + (distance/lightspeed) + (dataSize/bandwidth)
        return (long) (currentTime + transitTime + burstTime); 
    }

   
}
