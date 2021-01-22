import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JOptionPane;

public class Gui extends JFrame implements UI {
  private static Gui theGui;

  private RedTongue red;
  private boolean amode;
  private Mode mode;

  private JPanel Frame;
  private JTabbedPane tabbedPane;

  private JPanel modePanel;
  private JButton butSend;
  private JButton butRecv;

  private JPanel pairPanel;
  private JPanel filesPanel;
  private JPanel transferPanel;

  public Gui(RedTongue red) {
    super("Redtongue");
    this.red = red;
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

    Frame = new JPanel();
    GridBagLayout gbFrame = new GridBagLayout();
    GridBagConstraints gbcFrame = new GridBagConstraints();
    Frame.setLayout(gbFrame);

    tabbedPane = new JTabbedPane();
    tabbedPane.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        int m = tabbedPane.getSelectedIndex();
        switch(m) {
          case 0:
            changeMode(Mode.MODE);
            break;
          case 1:
            changeMode(Mode.NAME);
            break;
          case 2:
            if (amode == FileTransfer.SEND) {
              changeMode(Mode.FILE_S);
            } else {
              changeMode(Mode.FILE_R);
            }
            break;
          case 3:
            changeMode(Mode.TRANSFER);
            break;
          default:
            display(UI.ERROR, "Unrecognized mode");
        }
      }
    });

    modePanel = new JPanel();
    GridBagLayout gbmodePanel = new GridBagLayout();
    GridBagConstraints gbcmodePanel = new GridBagConstraints();
    modePanel.setLayout(gbmodePanel);

    butRecv = new JButton("Receive");
    butRecv.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        amode = FileTransfer.RECV;
        changeMode(Mode.WAIT);
        if (red != null) {
          Thread t = new Thread(new Runnable() {
            public void run() {
              red.start(FileTransfer.RECV);
            }
          });
          t.start();
        }
      }
    });
    gbcmodePanel.gridx = 5;
    gbcmodePanel.gridy = 12;
    gbcmodePanel.gridwidth = 10;
    gbcmodePanel.gridheight = 4;
    gbcmodePanel.fill = GridBagConstraints.BOTH;
    gbcmodePanel.weightx = 1;
    gbcmodePanel.weighty = 1;
    gbcmodePanel.anchor = GridBagConstraints.CENTER;
    gbmodePanel.setConstraints(butRecv, gbcmodePanel);
    modePanel.add(butRecv);

    butSend = new JButton("Send");
    butSend.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        amode = FileTransfer.SEND;
        changeMode(Mode.NAME);
        if (red != null) {
          Thread t = new Thread(new Runnable() {
            public void run() {
              red.start(FileTransfer.SEND);
            }
          });
          t.start();
        }
      }
    });
    gbcmodePanel.gridx = 5;
    gbcmodePanel.gridy = 4;
    gbcmodePanel.gridwidth = 10;
    gbcmodePanel.gridheight = 4;
    gbcmodePanel.fill = GridBagConstraints.BOTH;
    gbcmodePanel.weightx = 1;
    gbcmodePanel.weighty = 1;
    gbcmodePanel.anchor = GridBagConstraints.CENTER;
    gbmodePanel.setConstraints(butSend, gbcmodePanel);
    modePanel.add(butSend);
    tabbedPane.addTab("Mode",modePanel);

    pairPanel = new JPanel();
    GridBagLayout gbpairPanel = new GridBagLayout();
    GridBagConstraints gbcpairPanel = new GridBagConstraints();
    pairPanel.setLayout(gbpairPanel);
    tabbedPane.addTab("Pair",pairPanel);
    tabbedPane.setEnabledAt(1, false);

    filesPanel = new JPanel();
    GridBagLayout gbfilesPanel = new GridBagLayout();
    GridBagConstraints gbcfilesPanel = new GridBagConstraints();
    filesPanel.setLayout(gbfilesPanel);
    tabbedPane.addTab("Files",filesPanel);
    tabbedPane.setEnabledAt(2, false);

    transferPanel = new JPanel();
    GridBagLayout gbtransferPanel = new GridBagLayout();
    GridBagConstraints gbctransferPanel = new GridBagConstraints();
    transferPanel.setLayout(gbtransferPanel);
    tabbedPane.addTab("Transfer",transferPanel);
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

  public void display(char type, String s) {
    switch (type) {
      case UI.INFO:
        System.out.println(s);
        break;
      case UI.MESSAGE:
        switch(mode) {
          case NAME:
            JLabel name = new JLabel(s);
            String n = s.split(" ")[0];
            name.addMouseListener(new MouseAdapter() {
              @Override
              public void mouseClicked(MouseEvent e) {
                if (red != null) {
                  Thread t = new Thread(new Runnable() {
                    public void run() {
                      red.pair(n);
                    }
                  });
                  t.start();
                }
              }
            });
            pairPanel.add(name);
            break;
          default:
            System.out.println("MESSAGE: "+s);
        }
        break;
      case UI.WARNING:
        System.err.println(s);
        break;
      case UI.ERROR:
        System.err.println(s);
        break;
      case UI.POPUP:
        JOptionPane.showMessageDialog(null, s);
        break;
      default:
        System.out.println(s);
    }
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
        tabbedPane.setEnabledAt(1, true);
        tabbedPane.setSelectedIndex(1);
        JLabel info = new JLabel("Choose a device to pair to by typing one of "+
            "the following names:\n");
        pairPanel.removeAll();
        pairPanel.add(info);
        break;
      case WAIT:
        tabbedPane.setEnabledAt(1, true);
        tabbedPane.setSelectedIndex(1);
        info = new JLabel("Searching for devices to connect to...");
        pairPanel.removeAll();
        pairPanel.add(info);
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
    String s = JOptionPane.showInputDialog("Enter pair number");
    try {
      int i = Integer.parseInt(s);
      return s;
    } catch (Exception e) {
      return "-1";
    }
  }

  public static void main(String args[]) {
    theGui = new Gui(null);
  }
} 
