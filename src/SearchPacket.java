import java.net.*;
import java.nio.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class SearchPacket {
  public static final char SEARCH = 's';
  public static final char RETURN = 'r';
  public static final char PAIR = 'p';

  private int len;
  private char control;
  private byte[] body;
  private ByteBuffer buff;
  private InetAddress addr;

  public SearchPacket(char control) {
    this.control = control;
    this.body = "Search".getBytes();
    this.len = this.body.length;
  }

  public SearchPacket(char control, String name) {
    this.control = control;
    this.body = name.getBytes();
    this.len = this.body.length;
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

  public void recv(DatagramSocket sock) {
    byte[] recv = new byte[1024];
    DatagramPacket pack = new DatagramPacket(recv, recv.length);
    try {
      sock.receive(pack);
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

  public char getControl() {
    return control;
  }
}
