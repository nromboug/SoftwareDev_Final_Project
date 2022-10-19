
import java.util.HashMap;

class IoT_HTL {
  
  static final int FRONT = 0;
  static final int REAR = 1;
  static final int LEFT = 2;
  static final int RIGHT = 3;

  static final int NORMAL = 0;
  static final int ERROR = 1;
  
  Hazard [] detectedHazards;
  
  int speed;
  boolean cc_active;

  Sensor frontSensor;
  Sensor rearSensor;
  Sensor leftSensor;
  Sensor rightSensor;
  HashMap<Integer, Driver> userMap = new HashMap<Integer, Driver>();
  HashMap<Integer, Technician> technicianMap = new HashMap<Integer, Technician>();

  /*
   * Default constructor
   */
  public IoT_HTL() {
    cc_active = false;

    frontSensor  = new Sensor(FRONT);
    rearSensor   = new Sensor(REAR);
    leftSensor   = new Sensor(LEFT);
    rightSensor  = new Sensor(RIGHT);

    detectedHazards = new Hazard[4];
  }

  User verifyUser(int pin) {
      User temp = userMap.get(pin);
      if (temp != null) {
          return temp;
      }
      return technicianMap.get(pin);

  }

  /*
   * Sets the speed of the vehicle. May be outside of the range of cruise control,
   * As this method may be used in emergency situations.
   */
  int setSpeed(int speed) {
    this.speed = speed;
    return this.speed;
  }

  /*
   * Increases the CC speed. 
   * Returns the old speed value.
   */
  int increaseSpeed(int amt) {
    int temp = this.speed;
    this.speed += amt;
    return temp;
  }

  /*
   * Decreases the CC speed. 
   * Returns the old speed value.
   */
  int decreaseSpeed(int amt) {
    int temp = this.speed;
    this.speed -= amt;
    return temp;
  }

  /*
   * Sets the cruise control to a speed. Activates cruise control if not already
   * Active.
   * Returns true if successful, false otherwise.
   *
  */
  boolean set_cc(int speed) {
    
    if (this.speed < 25) {
      System.out.println("Unable to set cruise control. Invalid current speed\n");
      return false;
    }
    if (speed < 25 || speed > 95) {
      System.out.println("cc must be between 25 and 95 mph");
      return false;
    }
    return false;
  }

  
  int changeLanes(int direction) {
    return direction;
  }
  // Bring sensor data together into usable format.
  void sensorFusion() {
    detectedHazards[FRONT] = frontSensor.detectedHazard;
    detectedHazards[REAR] = rearSensor.detectedHazard;
    detectedHazards[LEFT] = leftSensor.detectedHazard;
    detectedHazards[RIGHT] = rightSensor.detectedHazard;
  }

  //Print some current state info about IoT_HTL
  void printIotState() {
    System.out.println("Speed: " + speed + " CC: " + cc_active);
  }

  /*
        Standardized data printout.
  */
  void displayNormal(String decision, Hazard [] currentHazards) {
    System.out.println("***************************************************");
    System.out.print("Front:\t\t");
    currentHazards[FRONT].printHazard();
    System.out.print("Rear:\t\t");
    currentHazards[REAR].printHazard();
    System.out.print("Left:\t\t");
    currentHazards[LEFT].printHazard();
    System.out.print("Right:\t\t");
    currentHazards[RIGHT].printHazard();
    System.out.println(decision);
    System.out.print("New ");
    printIotState();
    System.out.println("**************************************************");
  }

  /*
    Check for issues that require lane corrects, or for hazards on the side.
  */
  void laneCheck(StringBuilder string) {
      if (detectedHazards[LEFT].getRelativeSpeed() != -(detectedHazards[RIGHT].getRelativeSpeed())) {
          if (detectedHazards[LEFT].getRelativeSpeed() < 0) {
              string.append("WARNING: " + detectedHazards[LEFT].getType() + " approaching from left.\n");
          }
          if (detectedHazards[RIGHT].getRelativeSpeed() < 0) {
            string.append("WARNING: " + detectedHazards[RIGHT].getType() + " approaching from right.\n");
        }
      }
      else
      {
          if (detectedHazards[LEFT].getRelativeSpeed() > 0) {
              string.append("Lane Correction Left\n");
          }
          else if (detectedHazards[RIGHT].getRelativeSpeed() > 0) {
              string.append("Lane Correction Right\n");
          }
      }
  }


  /*
   * This method is where driving decisions are made.
   * Takes into account hazard data from sensors
   */
  void decision(Hazard [] hazards) {
    String frontHazardType = hazards[FRONT].getType();
    String rearHazardType = hazards[REAR].getType();
    String leftHazardType = hazards[LEFT].getType();
    String rightHazardType = hazards[RIGHT].getType();
    
    int frontHazardRS = hazards[FRONT].getRelativeSpeed();
    int rearHazardRS = hazards[REAR].getRelativeSpeed();
    int rightHazardRS = hazards[RIGHT].getRelativeSpeed();
    int leftHazardRS = hazards[LEFT].getRelativeSpeed();

    int frontHazardDistance = hazards[FRONT].getDistance();
    int rearHazardDistance = hazards[REAR].getDistance();    
    int leftHazardDistance = hazards[LEFT].getDistance();
    int rightHazardDistance = hazards[RIGHT].getDistance();
    StringBuilder string = new StringBuilder(256);
    laneCheck(string);
    
    
    if (frontHazardType.equals("null") || frontHazardRS >= 0) { 
      printIotState();
      string.append("Maintain course.");
      displayNormal(string.toString(), hazards);
      return;    //This will exit the method, avoiding any double decisions.
    }

    //Vehicle case
    if (frontHazardType.equals("vehicle")) {
      if (frontHazardDistance >= 400) {
        if (leftHazardType.equals("lane")) {
          printIotState();
          string.append("Vehicle detected: Change lanes left, maintain speed.");
          displayNormal(string.toString(), hazards);
          return;
        }
        else if (rightHazardType.equals("lane")) {
          printIotState();
          string.append("Vehicle detected: Change lanes right, maintain speed.");
          displayNormal(string.toString(), hazards);
          return;
        }
        else {
          printIotState();
          decreaseSpeed(frontHazardRS/3);
          cc_active = false;
          string.append("Applying brakes: Consider further braking, or lane change if becomes possible.");
          displayNormal(string.toString(), hazards);
          return;
        }
      }
      else {
        //if the distance of the front vehicle is close (< 400), apply brakes
        if (leftHazardType.equals("lane")) {
          //apply brakes while merging left
          printIotState();
          decreaseSpeed(speed/4);
          cc_active = false;
          string.append("Close vehicle detected: Applying light brakes, change lanes left.");
          displayNormal(string.toString(), hazards);
          return;
        }
        else if (rightHazardType.equals("lane")) {
          //apply brakes while merging right
          printIotState();
          decreaseSpeed(speed/4);
          string.append("Close vehicle detected: Applying light brakes, change lanes right.");
          displayNormal(string.toString(), hazards);
          return;
        }
        else {
          //apply heavy brakes if driver cannot merge
          printIotState();
          decreaseSpeed(speed/3);
          cc_active = false;
          string.append("WARNING: CRASH DANGER\nApplying heavy brakes, change lanes if becomes available.");
          displayNormal(string.toString(), hazards);
          return;
        }
      }
        
    }
    //Object case
    else if(frontHazardType.equals("object")) {

      // object is suddenly too close, we do not attempt to swerve (not enough time)
      // In this case, heavily brake, disengage cc, warn user.
      if (frontHazardDistance < 200) {
        printIotState();
        decreaseSpeed(speed/3);
        cc_active = false;
        string.append("WARNING: CRASH DANGER\nApplying heavy brakes, change lanes if becomes available.");
        displayNormal(string.toString(), hazards);
        return;
      }
      
      //If driver cannot merge to the right lane, merge to the left lane
      if (leftHazardType.equals("lane")) {
        printIotState();
        string.append("Object detected: Changed lanes left, maintain speed");
        displayNormal(string.toString(), hazards);
        return;
      }
      //If driver cannot merge to the left lane, merge to the right lane
      else if (rightHazardType.equals("lane")) {
        printIotState();
        string.append("Object detected: Changed lanes right, maintain speed");
        displayNormal(string.toString(), hazards);
        return;
      }
      
      //If the driver cannot merge lanes, use heavy brakes. I made a mistake, we should use speed lol. Thats my bad. If we use this in some cases, it will be negative and actually INCREASE speed. There are some cases we should use rs speed tho but im going to go through and check them all. ohh true
      else {
        printIotState();
        decreaseSpeed(speed/3);
        cc_active = false;
        string.append("WARNING: CRASH DANGER\nApplying heavy brakes, change lanes if becomes available.");
        displayNormal(string.toString(), hazards);
        return;
      }
    }
  }
  
}