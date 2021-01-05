import java.net.*;

public class Host {
  private InetAddress IP;
  private int port;
  private String name;

  public Host(InetAddress ip, String hostName) {
    this.IP = ip;
    this.name = hostName;
  }

  public void setPort(int port) {
    this.port = port;
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
