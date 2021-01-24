public interface UI {
  public static final char INFO = 'i';
  public static final char MESSAGE = 'm';
  public static final char POPUP = 'p';
  public static final char WARNING = 'w';
  public static final char ERROR = 'e';

  public void display(char type, String s); 
  public void changeMode(Mode mode);
  public String getInput(String message);
  public Progress getProg();
}
