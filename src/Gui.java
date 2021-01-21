import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JTabbedPane;
import javax.swing.JButton;

public class Gui extends JFrame {
  private static Gui theGui;

  private RedTongue red;

  private JPanel Frame;
  private JTabbedPane tabbedPane;

  private JPanel panel1;
  private JButton but2;
  private JButton but3;

  private JPanel panel2;
  private JPanel panel3;
  private JPanel panel4;

  public static void main(String args[]) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (ClassNotFoundException e) {
    }
    catch (InstantiationException e) {
    }
    catch (IllegalAccessException e) {
    }
    catch (UnsupportedLookAndFeelException e) {
    }
    theGui = new Gui(null);
  }

  public Gui(RedTongue red) {
    super("Redtongue");

    Frame = new JPanel();
    GridBagLayout gbFrame = new GridBagLayout();
    GridBagConstraints gbcFrame = new GridBagConstraints();
    Frame.setLayout(gbFrame);

    tabbedPane = new JTabbedPane();

    panel1 = new JPanel();
    GridBagLayout gbpanel1 = new GridBagLayout();
    GridBagConstraints gbcpanel1 = new GridBagConstraints();
    panel1.setLayout(gbpanel1);

    but2 = new JButton("Receive");
    gbcpanel1.gridx = 5;
    gbcpanel1.gridy = 12;
    gbcpanel1.gridwidth = 10;
    gbcpanel1.gridheight = 4;
    gbcpanel1.fill = GridBagConstraints.BOTH;
    gbcpanel1.weightx = 1;
    gbcpanel1.weighty = 1;
    gbcpanel1.anchor = GridBagConstraints.CENTER;
    gbpanel1.setConstraints(but2, gbcpanel1);
    panel1.add(but2);

    but3 = new JButton("Send");
    but3.setActionCommand("Receive");
    gbcpanel1.gridx = 5;
    gbcpanel1.gridy = 4;
    gbcpanel1.gridwidth = 10;
    gbcpanel1.gridheight = 4;
    gbcpanel1.fill = GridBagConstraints.BOTH;
    gbcpanel1.weightx = 1;
    gbcpanel1.weighty = 1;
    gbcpanel1.anchor = GridBagConstraints.CENTER;
    gbpanel1.setConstraints(but3, gbcpanel1);
    panel1.add(but3);
    tabbedPane.addTab("Mode",panel1);

    panel2 = new JPanel();
    GridBagLayout gbpanel2 = new GridBagLayout();
    GridBagConstraints gbcpanel2 = new GridBagConstraints();
    panel2.setLayout(gbpanel2);
    tabbedPane.addTab("Pair",panel2);
    tabbedPane.setEnabledAt(1, false);

    panel3 = new JPanel();
    GridBagLayout gbpanel3 = new GridBagLayout();
    GridBagConstraints gbcpanel3 = new GridBagConstraints();
    panel3.setLayout(gbpanel3);
    tabbedPane.addTab("Files",panel3);
    tabbedPane.setEnabledAt(2, false);

    panel4 = new JPanel();
    GridBagLayout gbpanel4 = new GridBagLayout();
    GridBagConstraints gbcpanel4 = new GridBagConstraints();
    panel4.setLayout(gbpanel4);
    tabbedPane.addTab("Transfer",panel4);
    tabbedPane.setEnabledAt(3, false);

    gbcFrame.gridx = 0;
    gbcFrame.gridy = 0;
    gbcFrame.gridwidth = 20;
    gbcFrame.gridheight = 20;
    gbcFrame.fill = GridBagConstraints.BOTH;
    gbcFrame.weightx = 1;
    gbcFrame.weighty = 1;
    gbcFrame.anchor = GridBagConstraints.NORTH;
    gbFrame.setConstraints(tabbedPane, gbcFrame);
    Frame.add(tabbedPane);

    setDefaultCloseOperation(EXIT_ON_CLOSE);

    setContentPane(Frame);
    setSize(750, 650);
    setLocation(600, 200);
    setVisible(true);
  } 
} 
