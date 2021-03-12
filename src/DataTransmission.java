import java.util.Random;

public class DataTransmission {
    String content;
    String type;
    Mission mission;
    Integer bitSize;
    String reciever;

    // Types are "report", "telemetry", "update"
    
    DataTransmission(Mission mission, String type, String content, String reciever) {
        this.content = content;
        this.type = type;
        this.mission = mission;
        this.reciever = reciever;
        setBitSize(type);
    }

    private void setBitSize(String dTransmissionType) {
        Integer byteSize = 0;
        if (dTransmissionType.equals("telemetry")){
            byteSize = getRandom(100, 10000);
        }
        if (dTransmissionType.equals("report")){
            byteSize = getRandom(100000, 100000000);
        }
        if (dTransmissionType.equals("update")){
            byteSize = getRandom(1000000, 1000000000);
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
        return "DataTransmission of type" + this.getType() + " regarding " + this.getContent();
       }

    private Integer getRandom(Integer min, Integer max){
        Random ran = new Random();
        Integer range = max - min + 1;
        Integer randomNum =  ran.nextInt(range) + min;
        return randomNum;
    }

   
}
