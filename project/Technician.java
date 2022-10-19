

class Technician extends User {
  private int employeeId;
  
  public Technician(String name, int employeeId) {
    super(name);
    this.employeeId = employeeId;
  }

  public int getEmployeeId() {
    return employeeId;
  }

  public void generateLog() {
    
  }
  
}