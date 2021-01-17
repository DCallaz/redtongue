public class RedTongue {

  public static final boolean SEND = true;
  public static final boolean RECV = false;

  private boolean mode;
  private UI ui;
  private String name;
  private Finder f = null;
  private Host h = null;

  public RedTongue() {
    ui = new Tui(this);
    mode = SEND;
    name = "UnknownUser";
  }

  public void start(boolean mode) {
    this.mode = mode;
    f = new Finder(mode, ui);
    if (mode == SEND) {
      f.search();
    } else {
      h = f.listen(name);
      ui.changeMode(Mode.TRANSFER);
    }
  }

  public void pair(String name) {
    h = f.pair(name);
    ui.changeMode(Mode.TRANSFER);
  }

  public static void main(String[] args) {
    RedTongue r = new RedTongue();
    if (args.length == 1) {
      r.name = args[0];
    }
  }
}
