import javax.swing.JFrame;

public class Gui1 extends JFrame implements UI {

  private RedTongue red;
  private Mode mode;
  //private boolean amode;
  
  public Gui1(RedTongue red) {
    super("Redtongue");
    this.red = red;
    changeMode(Mode.MODE);
  }

  public void display(char type, String s) {

  }

  public void changeMode(Mode mode) {
    this.mode = mode;
    changeDisplay();
  }

  private void changeDisplay() {
    //TODO: paint GUI
    switch(mode) {
      case MODE:
        break;
      case NAME:
        break;
      case WAIT:
        break;
      case FILE_S:
        break;
      case FILE_R:
        break;
      default:
        //TODO: error
    }
  }

  public String getInput(String message) {
    return "";
  }
}
