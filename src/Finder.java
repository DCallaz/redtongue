import java.net.*;
import java.io.*;

public class Finder {
	public static final boolean SEARCH = true;
	public static final boolean LISTEN = false;

  public static InetAddress search() {
    InetAddress addr = null;
    try {
      DatagramSocket sock = new DatagramSocket(4447);
      InetAddress group = InetAddress.getByName("224.0.113.0");
      SearchPacket p = new SearchPacket(SearchPacket.SEARCH);
      p.send(group, 4446, sock);
      System.out.println("Sent message to broadcast group");

      p = new SearchPacket();
      p.recv(sock);
      if (p.getControl() == 'r') {
        addr = p.getAddress();
        System.out.println("Received name: \""+p.getName()+"\" from "+p.getAddress());
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return addr;
  }

  public static InetAddress listen(String name) {
    InetAddress addr = null;
    try {
      MulticastSocket sock = new MulticastSocket(4446);
      InetAddress group = InetAddress.getByName("224.0.113.0");
      sock.joinGroup(group);
      boolean paired = false;
      while (!paired) {
        SearchPacket p = new SearchPacket();
        p.recv(sock);
        if (p.getControl() == 's') {
          addr = p.getAddress();
          System.out.println("Received message from "+addr.getHostAddress());
          InetAddress mine = InetAddress.getLocalHost();
          p = new SearchPacket(SearchPacket.RETURN, name);
          p.send(addr, 4447, sock);
          System.out.println("Sent return message to "+addr.getHostAddress());
          paired = true;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return addr;
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
      search();
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
