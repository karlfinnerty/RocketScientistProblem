public class Mission extends Thread{
    String destination;
    String stage;
    String name;
    EventLog eventLog;

    Mission(String name, String dest, EventLog eventLog){
        this.destination = dest;
        this.name = name;
        this.eventLog = eventLog;
    }

    public void run(){
        while (true){
            //System.out.println(destination);
            
        }
        
    }

    public String toString(){
        return this.name + " "+ this.destination;
       }
       
    //stages should change in order based on controller list?
    public void changeStage(String stage){
        this.stage = stage;
    }
}