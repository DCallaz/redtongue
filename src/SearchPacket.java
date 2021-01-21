import java.net.*;
import java.nio.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class SearchPacket {
  public static final char SEARCH = 's';
  public static final char RETURN = 'r';
  public static final char PAIR = 'p';
  public static final char ACCEPT = 'a';

  private int len;
  private char control;
  private byte[] body;
  private ByteBuffer buff;
  private InetAddress addr;

  //Constructor for search
  public SearchPacket(char control) {
    this.control = control;
    this.body = "Search".getBytes();
    this.len = this.body.length;
  }

  //Constructor for return
  public SearchPacket(char control, String name) {
    this.control = control;
    this.body = name.getBytes();
    this.len = this.body.length;
  }

  //Constructor for pair
  public SearchPacket(char control, int num) {
    this.control = control;
    this.len = 4;
    this.body = ByteBuffer.allocate(4).putInt(num).array();
  }

  //Constructor for accept
  public SearchPacket(char control, int port, boolean temp) {
    this.control = control;
    this.len = 4;
    this.body = ByteBuffer.allocate(4).putInt(port).array();
  }

  public SearchPacket() {
  }

  public void send(InetAddress addr, int port, DatagramSocket sock) {
    buff = ByteBuffer.allocate(6+len);
    buff.putInt(len);
    buff.putChar(control);
    buff.put(body);
    byte[] arr = buff.array();
    try {
      sock.send(new DatagramPacket(arr, arr.length, addr, port));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void recv(DatagramSocket sock, int timeout) throws SocketTimeoutException,
         SocketException {
    byte[] recv = new byte[1024];
    DatagramPacket pack = new DatagramPacket(recv, recv.length);
    try {
      sock.setSoTimeout(timeout);
      sock.receive(pack);
      sock.setSoTimeout(0);
    } catch (SocketTimeoutException e) {
      sock.setSoTimeout(0);
      throw e;
    } catch (SocketException e) {
      sock.setSoTimeout(0);
      throw e;
    } catch (IOException e) {
      e.printStackTrace();
    }
    buff = ByteBuffer.wrap(recv);
    len = buff.getInt();
    control = buff.getChar();
    body = new byte[len];
    buff.get(body);
    addr = pack.getAddress();
  }

  public void recv(DatagramSocket sock) throws SocketException {
    try {
      recv(sock, 0);
    } catch (SocketTimeoutException e) {
    }
  }

  public InetAddress getAddress() {
    return addr;
  }

  public String getName() {
    if (control == 'r') {
      try {
        return new String(body, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public int getPort() {
    if (control == 'a') {
      return ByteBuffer.wrap(body).getInt();
    }
    return -1;
  }

  public int getNum() {
    if (control == 'p') {
      return ByteBuffer.wrap(body).getInt();
    }
    return -1;
  }

  public char getControl() {
    return control;
  }
}
