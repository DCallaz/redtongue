import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

public class GuiProgress extends JPanel implements Progress {
	private int reps;
	private int currentRep = 0;
	private int fragment;
	private int prog = 0;
  private JProgressBar bar;

  public GuiProgress() {
    super();
    bar = new JProgressBar(0, 100);
    add(bar);
    this.reps = 1;
    this.fragment = 100;
  }

  public void start(int reps) {
    this.reps = reps;
    this.fragment = 100/reps;
    bar.setValue(0);
  }

  public void updateProgress(short percent)
  {
		if (currentRep == reps -1 && percent == 100) {
      bar.setValue(100);
			prog = percent;
		} else {
			int tempProg = currentRep*fragment + (percent*fragment/100);
			if (tempProg != prog) {
        bar.setValue(tempProg);
				prog = tempProg;
			}
		}
    repaintAll();
  }

	public void incRep() {
		if (currentRep < reps - 1) {
			currentRep++;
		}
	}

  public void repaintAll()
  {
    bar.revalidate();
    bar.repaint();
    revalidate();
    repaint();
  }

	public static void main(String[] args) {
		GuiProgress g1 = new GuiProgress();
    JFrame frame = new JFrame();
    frame.add(g1);
    frame.pack();
    frame.setVisible(true);
    System.out.println("Starting simple");
		for (short i=1; i<=100; i++) {
			g1.updateProgress(i);
      System.out.println(i);
      try {
        Thread.sleep(100);
      } catch (Exception e) {

      }
		}
    g1.start(4);
    System.out.println("Starting compound");
		for (short j=0; j<4; j++) {
			for (short i=0; i<=100; i += 4) {
        System.out.println(i);
				g1.updateProgress(i);
        try {
          Thread.sleep(100);
        } catch (Exception e) {

        }
			}
			g1.incRep();
		}
    System.out.println("End");
	}
}
