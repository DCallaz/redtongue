import java.io.FileNotFoundException;

public class RedTongue {

  private boolean mode;
  private UI ui;
  private String name;
  private Finder f = null;
  private Host h = null;

  public RedTongue() {
    ui = new Tui(this);
    name = "UnknownUser";
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
      FileTransfer.transfer(mode, path, h.getTCP());
    } catch (FileNotFoundException e) {
      System.out.println("File \""+path+"\" not found.");
    }
    if (mode == FileTransfer.SEND) {
      ui.changeMode(Mode.FILE_S);
    } else {
      ui.changeMode(Mode.FILE_R);
    }
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
    RedTongue r = new RedTongue();
    if (args.length == 1) {
      r.name = args[0];
    }
  }
}
