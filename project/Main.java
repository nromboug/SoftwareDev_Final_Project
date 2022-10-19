import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

class Main {
    private static void runTests() throws FileNotFoundException{
        File dir = new File("tests");
        File[] directoryListing = dir.listFiles();
        IoT_HTL iot;
        int speed;
        boolean cc_active;
        String lht;
        int lhd;
        int lhrs;

        String rtht;
        int rthd;
        int rthrs;

        String fht;
        int fhd;
        int fhrs;

        String rht;
        int rhd;
        int rhrs;
        int count = 1;
        for (File child : directoryListing) {

            System.out.println("----------------\nTEST CASE " + count + "\nFile: " + child.getName() + "\n----------------");
            iot = new IoT_HTL();
            Scanner scanner = new Scanner(child);

            speed = scanner.nextInt(); scanner.nextLine();
            cc_active = ((scanner.nextLine().equals("active")) ? true : false);
            iot.setSpeed(speed);
            iot.cc_active = cc_active;

            lht = scanner.nextLine();
            lhd = Integer.parseInt(scanner.nextLine());
            lhrs = Integer.parseInt(scanner.nextLine());

            rtht = scanner.nextLine();
            rthd = Integer.parseInt(scanner.nextLine());
            rthrs = Integer.parseInt(scanner.nextLine());

            fht = scanner.nextLine();
            fhd = Integer.parseInt(scanner.nextLine());
            fhrs = Integer.parseInt(scanner.nextLine());

            rht = scanner.nextLine();
            rhd = Integer.parseInt(scanner.nextLine());
            rhrs = Integer.parseInt(scanner.nextLine());

            iot.leftSensor.checkForHazard(lhd, lht, 2, lhrs);
            iot.rightSensor.checkForHazard(rthd, rtht, 3, rthrs);
            iot.frontSensor.checkForHazard(fhd, fht, 0, fhrs);
            iot.rearSensor.checkForHazard(rhd, rht, 1, rhrs);
            iot.sensorFusion();
            iot.decision(iot.detectedHazards);



            scanner.close();
            count++;
        }
    }

    private static void logonScenario(Scanner scanner) {

        IoT_HTL iot = new IoT_HTL();
        iot.userMap.put(1234, new Driver("John Doe", 8079));
        iot.technicianMap.put(555, new Technician("Professional Tech", 56789));


        System.out.print("ENTER PIN: ");
        User user = iot.verifyUser(scanner.nextInt());
        scanner.nextLine();

        if (user == null) {
            System.out.println("Invalid Pin.");
        }
        else {
            if (user.getClass() == Driver.class) {
                Driver driver = (Driver) user;
                System.out.println("Name: " + driver.getName() + "\nID: " + driver.getIdNum());

            }
            else if (user.getClass() == Technician.class) {
                Technician tech = (Technician) user;
                System.out.println("Technician logged in.\nName: " + tech.getName() + "\nID: " + tech.getEmployeeId());
            }
        }

    }

    public static void main(String[] args) throws FileNotFoundException {
        Scanner input = new Scanner(System.in);
        String cmd = "";

        while (!cmd.equals("quit")) {
            System.out.print("Enter Testing Scenario [driving|logon]>_");
            cmd = input.nextLine().trim().toLowerCase();
            switch(cmd) {
                case "driving":
                    runTests();
                    break;
                case "logon":
                    logonScenario(input);
                    break;
                case "quit":
                    break;
                default:
                    System.out.println("Please enter valid command, or \'quit\' to exit.");
            }
            
        }


        input.close();
      
    }

    
}
