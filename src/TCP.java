import java.net.*;
import java.io.*;
import java.nio.*;

public class TCP {
	public static final boolean SEND = true;
	public static final boolean RECV = false;

	private boolean mode;
	private ServerSocket serv;
	private Socket sock;
	private DataInputStream in;
	private DataOutputStream out;

	public TCP(boolean send_recv, String host, int port) {
		this.mode = send_recv;
		if (host == null) {
			host = "localhost";
		}
		if (mode == SEND) {
			try {
				sock = new Socket(host, port);
			} catch (IOException e) {
				System.out.println("Unable to open sender socket: "+e);
			}
		} else {
			try {
				serv = new ServerSocket(port);
				sock = serv.accept();
			} catch (IOException e) {
				System.out.println("Unable to open server (receiver) socket.");
			}
		}
		try {
			in = new DataInputStream(sock.getInputStream());
			out = new DataOutputStream(sock.getOutputStream());
		} catch (IOException e) {
			System.out.println("Could not start readers and writers");
		}
	}

  public int getPort() {
    if (serv != null) {
      return serv.getLocalPort();
    }
    return -1;
  }

	public void send(byte[] bytes, int length, Progress prog, int chunk) throws Exception {
		if (this.mode != SEND) {
			throw new Exception("Incompatible mode, receiver can't run send");
		}
		int size = (length == -1) ? bytes.length : length;
		int index = 0;
		System.out.println("Size: "+size+" chunk: "+chunk);
		out.writeInt(size);
		out.writeInt(chunk);
		while (index + chunk < size) {
			out.write(bytes, index, chunk);
			index += chunk;
			if (prog != null) {
				double div = (double)index/size;
				prog.updateProgress((short)(div*100));
			}
		}
		out.write(bytes, index, size - index);
		if (prog != null) {
			prog.updateProgress((short)100);
		}
	}

	public byte[] recv(Progress prog) throws Exception {
		if (this.mode != RECV) {
			throw new Exception("Incompatible mode, sender can't run receive");
		}
		int size = in.readInt();
		int chunk = in.readInt();
		System.out.println("Size: "+size+" chunk: "+chunk);
		int index = 0;
		//System.out.println("Size: "+size+" Chunk: "+chunk);
		byte[] ret = new byte[size];
		while (index + chunk < size) {
			in.read(ret, index, chunk);
			index += chunk;
			if (prog != null) {
				double div = (double)index/size;
				prog.updateProgress((short)(div*100));
			}
		}
		in.read(ret, index, size-index);
		if (prog != null) {
			prog.updateProgress((short)100);
		}
		return ret;
	}

	public void sendSize(int size) {
		try {
			out.writeInt(size);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public int recvSize() {
		try {
			return in.readInt();
		} catch (IOException e) {
			System.out.println(e);
		}
		return -1;
	}

	public void sendName(String path) {
    File file = new File(path);
    String name = file.getName();

		try {
			out.writeUTF(name);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public String recvName() {
		try {
			return in.readUTF();
		} catch (IOException e) {
			System.out.println(e);
		}
		return null;
	}

  public void close() {
    try {
      if (serv != null) {
        serv.close();
      }
      sock.close();
    } catch (IOException e) {
      System.out.println("Could not close sockets");
    }
  }

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("USAGE: TCP <mode 0/1>");
			System.exit(0);
		}
		int modei = Integer.parseInt(args[0]);
		boolean mode = (modei == 0) ? RECV : SEND;
		TCP t = new TCP(mode, null, 8199);
		try {
			if (mode == SEND) {
				if (args.length > 1) {
					t.send(args[1].getBytes(), -1, new TuiProgress(), 1);
				} else {
					t.send("Hello, world!".getBytes(), -1, new TuiProgress(), 1);
				}
			} else {
				byte[] ret;
				ret = t.recv(new TuiProgress());
				System.out.println(new String(ret, "UTF-8"));
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
