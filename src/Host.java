import java.net.*;

public class Host {
  private InetAddress IP;
  private int port = 0;
  private String name;
  private TCP t;

  public Host(InetAddress ip, String hostName) {
    this.IP = ip;
    this.name = hostName;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void enableTCP(boolean send_recv) {
    t = new TCP(send_recv, IP.getHostAddress(), port);
    if (port == 0) {
      port = t.getPort();
    }
  }

  public InetAddress getIP() {
    return IP;
  }

  public int getPort() {
    return port;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object test) {
    return test.equals(name);
  }
}
