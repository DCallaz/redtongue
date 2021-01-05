import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;

public class FileTransfer {
	public static final boolean SEND = true;
	public static final boolean RECV = false;

	private boolean mode;
	private RandomAccessFile f;

	private FileTransfer(boolean send_recv, String file, TCP t) throws FileNotFoundException {
		this.mode = send_recv;
		if (mode == SEND) {
      t.sendName(file);
			//open file for read
      f = new RandomAccessFile(file, "r");
		} else {
      String fname = t.recvName();
      if (file == null) {
        file = fname;
      }
			//open file for write
			try {
				File f = new File(file);
				Files.deleteIfExists(f.toPath());
        this.f = new RandomAccessFile(file, "rw");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void send(TCP t) {
		byte[] array = new byte[500000000];
		int size = 0;
		try {
			int reps = (int)(f.length()/array.length)+1;
			t.sendSize(reps);
			TuiProgress prog = new TuiProgress(reps);
			while ((size = f.read(array)) > 0) {
				t.send(array, size, prog, 100000);
				prog.incRep();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void recv(TCP t) {
		byte[] array;
		int reps = t.recvSize();
		System.out.println("Reps: "+reps);
		int i = 0;
		try {
			TuiProgress prog = new TuiProgress(reps);
			while (i < reps) {
				array = t.recv(prog);
				f.write(array);
				i++;
				prog.incRep();
			}
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void transfer(boolean send_recv, String file, TCP t)
      throws FileNotFoundException {
		FileTransfer f = new FileTransfer(send_recv, file, t);
		if (f.mode == SEND) {
			f.send(t);
		} else {
			f.recv(t);
		}
	}

  public static TCP getTCP(boolean send_recv) {
    return new TCP(send_recv, null, 8199);
  }

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("USAGE: FileTransfer <mode 0/1> <filename>");
			System.exit(0);
		}
		int modei = Integer.parseInt(args[0]);
		boolean mode = (modei == 0) ? RECV : SEND;

    String file = null;
    if (args.length >= 2) {
      file = args[1];
    } else if (args.length < 2 && mode == SEND) {
      System.out.println("NOTE: Sender needs to specify a file to send");
      System.exit(0);
    }

    try {
      TCP t = getTCP(mode);
		  FileTransfer.transfer(mode, file, t);
      t.close();
    } catch (FileNotFoundException e) {
      System.out.println("Could not find file "+e.getMessage());
    }
	}
}
