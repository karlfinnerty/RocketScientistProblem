import java.util.HashMap; 
// System contains a collection of celestial bodies
public class StarSystem{

	HashMap<String, Celestial> systemObjects = new HashMap<String, Celestial>();
	Celestial systemPrimary;
	Clock clock;
	long lastUpdate = 0;

	public StarSystem(Celestial [] systemObjects, Clock clock){
		this.systemPrimary = systemObjects[0];
		for(Celestial element : systemObjects){
			this.systemObjects.put(element.getName(), element);
		}
		this.clock = clock;
		// The primary celestial body in the system. All co-ordinates will be defined as relative position to PC so Heliospheric co-ordinate system will be used.
	}

	public HashMap<String, Celestial> getSystemObjects(){
		return this.systemObjects;
	}

	public void updatePositions(){
		long time = this.clock.getTicks();
		long timePassed = time - this.lastUpdate;
		this.lastUpdate	= time;
		for(Celestial planet : this.systemObjects.values()){
			planet.updatePosition(timePassed);
		}

	}
}