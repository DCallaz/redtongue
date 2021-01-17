import java.util.Scanner;
import java.util.NoSuchElementException;

public class Tui implements UI {

  private RedTongue red;
  private Mode mode;
  private volatile boolean input = false;

  public Tui(RedTongue red) {
    this.red = red;
    this.mode = Mode.MODE;
    changeDisplay();
    startListner();
  }

  public void startListner() {
    Thread t = new Thread(new Runnable() {
      public void run() {
        Scanner sc = new Scanner(System.in);
        boolean cont = true;
        while (true) {
          String next = "";
          try {
            next = sc.next();
          } catch (NoSuchElementException e) {
            break;
          }
          if (!input) {
            switch(mode) {
              case MODE:
                if (next.equals("send")) {
                  changeMode(Mode.NAME);
                  red.start(RedTongue.SEND);
                } else if (next.equals("receive")) {
                  changeMode(Mode.WAIT);
                  red.start(RedTongue.RECV);
                } else {
                  display(UI.INFO, "Unrecognized mode. Try again");
                }
                break;
              case NAME:
                red.pair(next);
                break;
            }
          }
        }
      }
    });
    t.start();
  }

  public void changeMode(Mode mode) {
    this.mode = mode;
    changeDisplay();
  }

  public void changeDisplay() {
    switch(mode) {
      case MODE:
        System.out.println("Welcome to Redtongue! Please type the "+
            "mode you would like to use (send/receive)");
        break;
      case NAME:
        System.out.println("Choose a device to pair to by typing one of "+
            "the following names:\n");
        break;
      case WAIT:
        System.out.println("Searching for devices to connect to...");
        break;
      case NUMBER:
        System.out.println("Type the number that appears on the sender device:");
        break;
    }
  }

  public void display(char type, String s) {
    switch (type) {
      case UI.INFO:
        System.out.print("INFO: ");
        break;
      case UI.MESSAGE:
        System.out.print("MESSAGE: ");
        break;
      case UI.POPUP:
        System.out.println("POPUP: ");
        break;
      case UI.WARNING:
        System.out.print("WARNING: ");
        break;
      case UI.ERROR:
        System.out.print("ERROR: ");
    }
    System.out.println(s);
  }

  public String getInput(String message) {
    display(UI.MESSAGE, message);
    input = true;
    Scanner sc = new Scanner(System.in);
    String ret = sc.next();
    input = false;
    return ret;
  }
}
