import java.io.FileNotFoundException;

public class RedTongue {

  private boolean mode;
  private UI ui;
  private String name;
  private Finder f = null;
  private Host h = null;

  public RedTongue() {
    ui = new Tui(this);
    mode = FileTransfer.SEND;
    name = "UnknownUser";
  }

  public void start(boolean mode) {
    this.mode = mode;
    f = new Finder(mode, ui);
    if (mode == FileTransfer.SEND) {
      f.search();
    } else {
      h = f.listen(name);
      ui.changeMode(Mode.FILE_R);
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

  public static void main(String[] args) {
    RedTongue r = new RedTongue();
    if (args.length == 1) {
      r.name = args[0];
    }
  }
}
