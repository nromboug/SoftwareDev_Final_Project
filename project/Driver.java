class Driver extends User {
    private int idNum;
  
  public Driver(String name, int idNum) {
    super(name);
    this.idNum = idNum;
  }

  public int getIdNum() {
    return idNum;
  }
  
}