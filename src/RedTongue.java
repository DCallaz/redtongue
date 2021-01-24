import java.io.FileNotFoundException;

public class RedTongue {

  private boolean mode;
  private UI ui;
  private String name;
  private Finder f = null;
  private Host h = null;

  public RedTongue(String name) {
    if (name != "UnknownUser") {
      ui = new Gui(this, name);
    } else {
      ui = new Gui(this);
    }
    this.name = name;
  }

  public RedTongue() {
    this("UnknownUser");
  }

  public void start(boolean mode) {
    if (f == null) {
      newFinder(mode);
    } else if (this.mode != mode) {
      ui.display(UI.INFO, "New finder starting");
      f.close();
      newFinder(mode);
    }
    this.mode = mode;
    search_listen();
  }

  public void search_listen() {
    if (mode == FileTransfer.SEND) {
      f.search();
    } else {
      h = f.listen();
      if (h != null) {
        ui.changeMode(Mode.FILE_R);
      }
    }
  }

  public void pair(String name) {
    h = f.pair(name);
    ui.changeMode(Mode.FILE_S);
  }

  public void transfer(String path) {
    ui.display(UI.INFO, "Starting transfer...");
    try {
      ui.changeMode(Mode.TRANSFER);
      FileTransfer.transfer(mode, path, h.getTCP(), ui.getProg());
    } catch (FileNotFoundException e) {
      System.out.println("File \""+path+"\" not found.");
    }
    if (mode == FileTransfer.SEND) {
      ui.changeMode(Mode.FILE_S);
    } else {
      ui.changeMode(Mode.FILE_R);
    }
  }

  public void updateName(String name) {
    this.name = name;
  }

  //<----------- Helper Functions ------------>
  private void newFinder(boolean mode) {
    if (mode == FileTransfer.SEND) {
      f = new Finder(ui);
    } else {
      f = new Finder(ui, name);
    }
  }

  public static void main(String[] args) {
    if (args.length == 1) {
      RedTongue r = new RedTongue(args[0]);
    } else {
      RedTongue r = new RedTongue();
    }
  }
}
