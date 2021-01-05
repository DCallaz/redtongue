import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;

public class Finder {
	public static final boolean SEARCH = true;
	public static final boolean LISTEN = false;

  private static volatile boolean lock = false;
  private static volatile boolean cont = true;

  public static Host search(UI ui) {
    Host addr = null;
    try {
      DatagramSocket sock = new DatagramSocket(4447);
      InetAddress group = InetAddress.getByName("224.0.113.0");
      SearchPacket p = new SearchPacket(SearchPacket.SEARCH);
      p.send(group, 4446, sock);
      System.out.println("Sent message to broadcast group");

      ArrayList<Host> hosts = new ArrayList<Host>();

      Thread t = new Thread(new Runnable() {
        public void run() {
          while (cont) {
            SearchPacket p = new SearchPacket();
            try {
              p.recv(sock, 1000);
            } catch (SocketTimeoutException e) {
              cont = false;
            }
            if (p.getControl() == 'r') {
              InetAddress addr = p.getAddress();
              ui.display(p.getName()+" ("+p.getAddress()+")");
              while(testAndSet());
              hosts.add(new Host(p.getAddress(), p.getName()));
              unlock();
              System.out.println(p.getName()+" added");
            }
          }
        }
      });
      t.start();
      boolean valid = false;
      String name = null;
      Scanner in = new Scanner(System.in);
      while (!valid) {
        name = in.next();
        valid = checkValid(name, hosts) != -1;
        System.out.println("Valid? "+valid);
      }
      cont = false;
      t.join();
      addr = hosts.get(checkValid(name, hosts));

      p = new SearchPacket(SearchPacket.PAIR, 1024);//TODO: get number here
      p.send(addr.getIP(), 4446, sock);

      p = new SearchPacket();
      p.recv(sock);
      System.out.println("control: "+p.getControl());
      addr.setPort(p.getPort());
      System.out.println("Recieved port number: "+addr.getPort());
      addr.enableTCP(TCP.SEND);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return addr;
  }

  public static void listen(String name) {
    InetAddress addr = null;
    try {
      Host mine = new Host(InetAddress.getLocalHost(), name);
      Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          mine.setPort(8199);
          mine.enableTCP(TCP.RECV);
        }
      });
      t.start();
      MulticastSocket sock = new MulticastSocket(4446);
      InetAddress group = InetAddress.getByName("224.0.113.0");
      sock.joinGroup(group);
      boolean paired = false;
      while (!paired) {
        SearchPacket p = new SearchPacket();
        p.recv(sock);
        addr = p.getAddress();
        if (p.getControl() == 's') {
          System.out.println("Received message from "+addr.getHostAddress());
          p = new SearchPacket(SearchPacket.RETURN, name);
          p.send(addr, 4447, sock);
          System.out.println("Sent return message to "+addr.getHostAddress());
        } else if (p.getControl() == 'p') {
          int num = p.getNum();
          //TODO: get num from user
          int user_num = 1024;
          if (num == user_num) {
            System.out.println("Numbers match, sending accept with port "+mine.getPort());
            p = new SearchPacket(SearchPacket.ACCEPT, mine.getPort());
            p.send(addr, 4447, sock);
            paired = true;
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static int checkValid(String name, ArrayList<Host> hosts) {
    for (int i=0; i<hosts.size(); i++) {
      if (hosts.get(i).equals(name)) {
        return i;
      }
    }
    return -1;
  }

  private static boolean testAndSet() {
    boolean temp = lock;
    lock = true;
    return temp;
  }

  private static void unlock() {
    lock = false;
  }

  public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("USAGE: Finder <mode 0/1> [<name>]");
			System.exit(0);
		}
		int modei = Integer.parseInt(args[0]);
		boolean mode = (modei == 0) ? SEARCH : LISTEN;

    System.out.println("Search? "+mode);
    if (mode == SEARCH) {
      search(new Tui());
    } else {
      String name = null;
      if (args.length >= 2) {
        name = args[1];
      } else {
        System.out.println("NOTE: listener needs to specify a name");
        System.exit(0);
      }
      listen(name);
	  }
  }
}
