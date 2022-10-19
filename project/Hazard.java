

class Hazard {

  private int distance;
  private String hazardType;
  private int relativeSpeed;
  private int side;

  public Hazard(int dist, String type, int rs, int loc) {
    this.distance = dist;
    this.hazardType = type;
    this.relativeSpeed = rs;
    this.side = loc;
  }

  public int getDistance() {
    return distance;
  }

  public String getType() {
    return hazardType;
  }

  public int getRelativeSpeed() {
    return relativeSpeed;
  }

  public int getSide() {
    return side;
  }

  public void printHazard(){
    System.out.println("Type: " 
                       + getType()
                       + "\tDistance: "
                       + getDistance()
                       + "\tRelative Speed: "
                       + getRelativeSpeed());

  }


  
}