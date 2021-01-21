import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Random;

public class Finder {
	public static final boolean SEARCH = true;
	public static final boolean LISTEN = false;

  private volatile boolean lock = false;
  private volatile boolean cont = true;
  private ArrayList<Host> hosts = new ArrayList<Host>();
  private DatagramSocket sock = null;
  private InetAddress group;
  private UI ui;
  private Host mine;

  public Finder(UI ui) {
    this.ui = ui;
    try {
      group = InetAddress.getByName("224.0.113.0");
        sock = new DatagramSocket(4447);
    } catch (UnknownHostException e) {
      System.out.println("Group not valid");
    } catch (SocketException e) {
      System.out.println("Datagram socket could not be opened: "+e);
    }
    mine = null;
  }

  public Finder(UI ui, String name) {
    this.ui = ui;
    try {
      group = InetAddress.getByName("224.0.113.0");
      MulticastSocket sock = new MulticastSocket(4446);
      sock.joinGroup(group);
      this.sock = sock;
      Host mine = new Host(InetAddress.getLocalHost(), name);
      Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          mine.setPort(8199);
          mine.enableTCP(TCP.RECV);
        }
      });
      t.start();
      this.mine = mine;
    } catch (UnknownHostException e) {
      System.out.println("Group not valid");
    } catch (SocketException e) {
      System.out.println("Datagram socket could not be opened: "+e);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void close() {
    sock.close();
  }

  public void search() {
    SearchPacket p = new SearchPacket(SearchPacket.SEARCH);
    p.send(group, 4446, sock);
    if (ui != null) {
      ui.display(UI.INFO, "Sent message to broadcast group");
    } else {
      System.out.println("Sent message to broadcast group");
    }
    cont = true;
    while (cont) {
      p = new SearchPacket();
      try {
        p.recv(sock, 1000);
        if (p.getControl() == 'r') {
          InetAddress addr = p.getAddress();
          if (ui != null) {
            ui.display(UI.MESSAGE, p.getName()+" ("+addr+")");
          } else {
            System.out.println(p.getName()+" ("+addr+")");
          }
          while(testAndSet());
          hosts.add(new Host(addr, p.getName()));
          unlock();
          if (ui != null) {
            ui.display(UI.INFO, p.getName()+" added");
          } else {
            System.out.println(p.getName()+" added");
          }
        }
      } catch (SocketTimeoutException e) {
        cont = false;
        //ui.display(UI.INFO, "Receive timed out");
      } catch (SocketException e) {
        System.err.println("Unknown socket exception");
        cont = false;
      }
    }
  }

  public Host pair(String name) {
    boolean valid = false;
    valid = checkValid(name, hosts) != -1;
    if (!valid) {
      return null;
    }
    cont = false;
    Host addr = hosts.get(checkValid(name, hosts));

    int num = (new Random()).nextInt(10000);
    if (ui != null) {
      ui.display(UI.POPUP, "Number: "+num);
    } else {
      System.out.println("Number: "+num);
    }

    SearchPacket p = new SearchPacket(SearchPacket.PAIR, num);
    p.send(addr.getIP(), 4446, sock);

    p = new SearchPacket();
    try {
      p.recv(sock);
    } catch (SocketException e) {
      ui.display(UI.WARNING, "Illegal socket exception in finder pair function");
    }
    if (ui != null) {
      ui.display(UI.INFO, "control: "+p.getControl());
    } else {
      System.out.println("control: "+p.getControl());
    }
    addr.setPort(p.getPort());
    if (ui != null) {
      ui.display(UI.INFO, "Recieved port number: "+addr.getPort());
    } else {
      System.out.println("Recieved port number: "+addr.getPort());
    }
    addr.enableTCP(TCP.SEND);
    return addr;
  }

  public Host listen() {
    InetAddress addr = null;
    boolean paired = false;
    boolean error = false;
    while (!paired && !error) {
      SearchPacket p = new SearchPacket();
      try {
        p.recv(sock);
      } catch (SocketException e) {
        error = true;
      }
      addr = p.getAddress();
      if (p.getControl() == 's') {
        if (ui != null) {
          ui.display(UI.INFO, "Received message from "+addr.getHostAddress());
        } else {
          System.out.println("Received message from "+addr.getHostAddress());
        }
        p = new SearchPacket(SearchPacket.RETURN, mine.getName());
        p.send(addr, 4447, sock);
        if (ui != null) {
          ui.display(UI.INFO, "Sent return message to "+addr.getHostAddress());
        } else {
          System.out.println("Sent return message to "+addr.getHostAddress());
        }
      } else if (p.getControl() == 'p') {
        int num = p.getNum();
        String input = ui.getInput("Enter the security number displayed on the connecting device");
        int user_num = Integer.parseInt(input);
        if (num == user_num) {
          if (ui != null) {
            ui.display(UI.INFO, "Numbers match, sending accept with port "+mine.getPort());
          } else {
            System.out.println("Numbers match, sending accept with port "+mine.getPort());
          }
          p = new SearchPacket(SearchPacket.ACCEPT, mine.getPort());
          p.send(addr, 4447, sock);
          paired = true;
        }
      }
    }
    if (error) {
      return null;
    } else {
      return mine;
    }
  }

  private int checkValid(String name, ArrayList<Host> hosts) {
    while(testAndSet());
    for (int i=0; i<hosts.size(); i++) {
      if (hosts.get(i).equals(name)) {
        unlock();
        return i;
      }
    }
    unlock();
    return -1;
  }

  private boolean testAndSet() {
    boolean temp = lock;
    lock = true;
    return temp;
  }

  private void unlock() {
    lock = false;
  }

  public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("USAGE: Finder <mode 0/1> [<name>]");
			System.exit(0);
		}
		int modei = Integer.parseInt(args[0]);
		boolean mode = (modei == 0) ? SEARCH : LISTEN;

    if (mode == SEARCH) {
      final Finder f = new Finder(new Tui(null));
      Thread t = new Thread(new Runnable() {
        public void run() {
          f.search();
        }
      });
      t.start();
      Scanner s = new Scanner(System.in);
      boolean valid = false;
      while(!valid) {
        String name = s.next();
        Host h = f.pair(name);
        if (h != null) {
          valid = true;
        }
      }
    } else {
      if (args.length >= 2) {
        String name = args[1];
        Finder f = new Finder(new Tui(null), name);
        f.listen();
      } else {
        System.out.println("NOTE: listener needs to specify a name");
        System.exit(0);
      }
    }
  }
}
