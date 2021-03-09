import java.lang.Math;
// Cestial class is used to instiate any celestial body e.g. Earth, the Moon or Sol
public class Celestial{

    Celestial primary;              // body being orbited
    String name;
    Spherical position;             // 3d spherical co-ordinates position vector in au
    Vector positionCartesian;
    double mass;                    // mass of body in kg
    double radius;                  // radius of body in km
    double orbitalRadius;           // average distance from primary
    double period;                  // orbital period in seconds i.e. amount of time it takes to do one revolution around primary
    double periapsis;               // au
    double apoapsis;                // au
    double azimuthIncrement;        // The number of degrees the azimuth increases per second
    double G = 6.673 * Math.pow(10, -11);

    public Celestial(String name, Celestial primary, Spherical position, double mass, double radius, double periapsis, double apoapsis){
        // The primary of an astrobody is its 'parent' i.e. the cestial body it orbits.
        // Primary = null signifies that astronomical body is stationary.
        this.name = name;
        this.primary = primary;
        this.mass = mass;
        this.radius = radius;
        this.periapsis = periapsis;
        this.apoapsis = apoapsis;
        if(this.periapsis == this.apoapsis && this.periapsis > 0.0){
            this.orbitalRadius = coRadius();
        }
        if (primary == null){
            this.position = new Spherical(new double[]{0.0, 0.0, 0.0});
            this.positionCartesian = new Vector(new double[]{0.0, 0.0, 0.0});
            this.period = 0.0;
            this.azimuthIncrement = 0.0;

        } else {
            this.position = position;
            this.period = coOrbit();
            this.azimuthIncrement = 360.0 / this.period;
            // this.positionCartesian = position.toCartesian();
        }
           
    }

    public String getName(){
        return this.name;
    }

    public Spherical getPosition(){
        return this.position;
    }

    public double getRadius(){
        return this.radius;
    }

    public double getMass(){
        return this.mass;
    }

    public double getOrbitalPeriod(){
        return this.period;
    }

    public double getAzimuthIncrement(){
        return this.azimuthIncrement;
    }

    public String toString(){
        return "name: " + this.name + "\norbiting: " + this.primary.getName() + "\nmass: " + this.mass + "\n" + this.position;
    }


    // Convert astronimcal units to metres
    public double auTom(double au){
        return Math.pow(au, 11) * 1.496;
    }

    // // Return straight line distance between two celestial bodies  NOTE cartesian co-ordiantes required for this to work
    // double slDistance(Celestial other){
    //  Vector resultant = this.position.subtract(other.getPosition());
    //  return resultant.magnitude();
    // }

    // Return straight line distance between two celestial bodies
    public double slDistance(Celestial other){
        return this.position.distance(other.position);
    }

    // Calculate circualr orbital radius given height and radius of primary
    public double coRadius(){
        return (this.primary.getRadius() + this.periapsis) * 1000;
    }

    // Calculate circular orbit velocity of object. 
    // v: velocity(m/s), G: constant of gravitation(Nm^2/kg^2), M: mass of primary(kg), R: orbital radius(m)
    // v = sqrt(G*M/R)
    public double coVelocity(){
        assert this.periapsis == this.apoapsis;
        return Math.sqrt((G * this.primary.getMass()) / this.orbitalRadius);
    }

    // Calculate orbital period of object
    // T: period of orbit(s), R: orbital radius(m), G: constant of gravitation(Nm^2/kg^2), M: mass of primary(kg),
    // T = sqrt((4*pi^2*R^3)/(G*M))
    public double coOrbit(){
        assert this.periapsis == this.apoapsis;
        double r = this.orbitalRadius;
        double m = this.primary.getMass();
        return Math.sqrt((4*(Math.pow(Math.PI, 2))*(Math.pow(r, 3)))/(G*m));
    }

    public void incrementAz(double value){
        this.position.setAzimuth((this.position.getAzimuth() + value) % 360);
    }
}
