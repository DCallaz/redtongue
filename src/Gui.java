import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class Gui extends JFrame implements UI {
  private static Gui theGui;

  private RedTongue red;
  private boolean amode;
  private Mode mode;
  private String name;

  private JPanel Frame;
  private JTabbedPane tabbedPane;

  private JPanel modePanel;
  private JButton butSend;
  private JButton butRecv;

  private JPanel pairPanel;
  private JPanel filesPanel;

  private JPanel transferPanel;
  private GuiProgress prog;

  private volatile File[] selected;

  public Gui(RedTongue red, String name) {
    super("Redtongue");
    if (!name.equals("")) {
      setTitle("Redtongue - "+name);
    }
    this.red = red;
    this.name = name;
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

    prog = new GuiProgress();
    transferPanel.add(prog);

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


    JMenuBar menuBar = new JMenuBar();

    JMenu menu = new JMenu("Edit");
    JMenuItem item = new JMenuItem("Change name");
    Gui g = this;
    item.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String name1 = JOptionPane.showInputDialog(null, "Enter new name:", "Name change", 0);
        red.updateName(name1);
        setTitle("RedTongue - "+name1);
        g.name = name1;
      }
    });
    menu.add(item);

    menuBar.add(menu);

    setJMenuBar(menuBar);

    setDefaultCloseOperation(EXIT_ON_CLOSE);

    setContentPane(Frame);
    setSize(750, 650);
    setLocation(600, 200);
    setVisible(true);
  } 

  public Gui(RedTongue red) {
    this(red, "");
  }

  public Progress getProg() {
    return prog;
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
        JOptionPane j = new JOptionPane(s);
        Thread t = new Thread(new Runnable() {
          @Override
          public void run() {
            JOptionPane.showMessageDialog(null, s, "Redtongue - Pair number", 0);
          }
        });
        t.start();
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
        tabbedPane.setEnabledAt(2, true);
        tabbedPane.setSelectedIndex(2);
        JLabel file = new JLabel(
            "Choose the file you would like to send:");
        JButton fileChoose = new JButton("Choose file");
        JLabel selectPrint = new JLabel("No file selected");
        JFileChooser fileInput;
        if (red.getDefaultFile() != null) {
          fileInput = new JFileChooser(red.getDefaultFile());
        } else {
          fileInput = new JFileChooser();
        }
        fileInput.setMultiSelectionEnabled(true);
        fileInput.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChoose.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
            fileInput.showDialog(null, "Select");
            selected = fileInput.getSelectedFiles();
            if (selected.length > 0) {
              selectPrint.setText(printNames());
              filesPanel.repaint();
              filesPanel.revalidate();
            }
          }
        });
        //JTextField fileInput = new JTextField();
        JButton send = new JButton("send");
        send.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
            if (red != null && selected != null) {
              Thread t = new Thread(new Runnable() {
                public void run() {
                  try {
                    for (int i=0; i<selected.length; i++) {
                      red.transfer(fileInput.getSelectedFile());
                    }
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }
              });
              t.start();
            }
          }
        });
        filesPanel.removeAll();
        filesPanel.add(file);
        filesPanel.add(fileChoose);
        filesPanel.add(selectPrint);
        filesPanel.add(send);
        break;
      case FILE_R:
        this.selected = null;
        tabbedPane.setEnabledAt(2, true);
        tabbedPane.setSelectedIndex(2);
        file = new JLabel("Choose a location if you would like to change "+
            "the save location\n\t(else default location will be used)");
        if (red != null && red.getDefaultFile() != null) {
          fileInput = new JFileChooser(red.getDefaultFile());
        } else {
          fileInput = new JFileChooser();
        }
        JButton folderChoose = new JButton("Choose location");
        selectPrint = new JLabel("No file selected");
        fileInput.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        folderChoose.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
            fileInput.showDialog(null, "Select");
            selected = new File[1];
            selected[0] = fileInput.getSelectedFile();
            selectPrint.setText(printNames());
            filesPanel.repaint();
            filesPanel.revalidate();
          }
        });
        send = new JButton("next");
        send.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent arg0) {
            if (red != null) {
              Thread t = new Thread(new Runnable() {
                public void run() {
                  try {
                    if (selected != null && selected.length > 0) {
                      red.transfer(selected[0]);
                    } else {
                      red.transfer(null);
                    }
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }
              });
              t.start();
            }
          }
        });
        filesPanel.removeAll();
        filesPanel.add(file);
        filesPanel.add(folderChoose);
        filesPanel.add(selectPrint);
        filesPanel.add(send);
        break;
      case TRANSFER:
        tabbedPane.setEnabledAt(3, true);
        tabbedPane.setSelectedIndex(3);
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

  private String printNames() {
    String ret = "";
    for (int i=0; i<selected.length; i++) {
      String name = selected[i].getName();
      ret += name + ",";
    }
    return ret.substring(0, ret.length()-1);
  }

  public static void main(String args[]) {
    theGui = new Gui(null);
  }
} 
