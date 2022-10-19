

public class Sensor {
  int side;
  Hazard detectedHazard;

  public Sensor(int side) {
    this.side = side;
  }

  public Hazard checkForHazard(int distance, String name, int location, int rs) {
    detectedHazard = new Hazard(distance, name, rs, location);
    return detectedHazard;
  }

  
  public String getSensorLocation() {
    if (side == IoT_HTL.LEFT) {
      return "left";
    }
    if (side == IoT_HTL.RIGHT) {
      return "right";
    }
    if (side == IoT_HTL.FRONT) {
      return "front";
    }
    if (side == IoT_HTL.REAR) {
      return "rear";
    }
    return null;   //case should never be reached
  }

  
}