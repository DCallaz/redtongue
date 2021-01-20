import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class Tui implements UI {

  private RedTongue red;
  private Mode mode;
  private volatile boolean input = false;
  private String temp = null;
  private volatile boolean tempWait = true;
  private boolean amode;

  public Tui(RedTongue red) {
    this.red = red;
    this.mode = Mode.MODE;
    changeDisplay();
    Thread t = new Thread(new Runnable() {
      public void run() {
        startListner();
      }
    });
    t.start();
  }

  public void startListner() {
    Scanner sc = new Scanner(System.in);
    boolean cont = true;
    while (true) {
      String next = "";
      try {
        next = sc.nextLine();
      } catch (NoSuchElementException e) {
        e.printStackTrace();
        break;
      }
      System.out.println(input);
      if (input) {
        temp = next;
        tempWait = false;
        System.out.println("Notifying");
        synchronized(this) {
          notifyAll();
        }
      } else if (next.equals("help")) {
        System.out.println("HELP MENU:");//TODO
      } else if (next.startsWith("cs")) {
        String m = next.split(" ")[1];
        switch(m) {
          case "mode":
            changeMode(Mode.MODE);
            break;
          case "name":
            changeMode(Mode.NAME);
            break;
          case "file":
            if (amode == FileTransfer.SEND) {
              changeMode(Mode.FILE_S);
            } else {
              changeMode(Mode.FILE_R);
            }
            break;
          case "transfer":
            changeMode(Mode.TRANSFER);
            break;
          default:
            display(ERROR, "Unrecognized mode");
        }
      } else {
        Exe exe = null;
        final String s = next;
        switch(mode) {
          case MODE:
            if (next.equals("send")) {
              changeMode(Mode.NAME);
              exe = () -> red.start(FileTransfer.SEND);
              amode = FileTransfer.SEND;
            } else if (next.equals("receive")) {
             changeMode(Mode.WAIT);
              exe = () -> red.start(FileTransfer.RECV);
              amode = FileTransfer.RECV;
            } else {
              display(UI.INFO, "Unrecognized mode. Try again");
            }
            break;
          case NAME:
            exe = () -> red.pair(s);
            break;
          case FILE_S:
          case FILE_R:
            System.out.println("Input received");
            exe = () -> red.transfer(s);
            break;
          default:
            System.out.println("ERROR: no such mode "+mode);
        }
        Thread t = new Thread(exe);
        t.start();
      }
    }
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
      case FILE_S:
        System.out.println("Enter the file location of the file you would like to send:");
        break;
      case FILE_R:
        System.out.println("Enter a file location if you would like to change the save location\n\telse press enter:");
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
    System.out.println("Waiting...");
    synchronized(this) {
      while (tempWait) {
        try {
          wait();
          System.out.println("Awoken");
        } catch (Exception e) {
          System.out.println(e);
        }
      }
    }
    System.out.println("Got input "+temp);
    input = false;
    String ret = temp;
    temp = null;
    return ret;
  }
}
