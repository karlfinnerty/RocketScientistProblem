public class DataTransmission {
    String content;
    String type;
    Mission mission;
    
    DataTransmission(Mission mission, String type, String content) {
        this.content = content;
    }

    public String getType(){
        return this.type;
    }

    public Mission getMission() {
        return this.mission;
    }

    public String getContent() {
        return this.content;
    }

   
}
