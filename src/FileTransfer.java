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

	private FileTransfer(RedTongue red, boolean send_recv, File file, TCP t) throws FileNotFoundException {
		this.mode = send_recv;
		if (mode == SEND) {
      t.sendName(file.getName());
			//open file for read
      f = new RandomAccessFile(file, "r");
		} else {
      String fname = t.recvName();
      if (file == null) {
        if (red != null && red.getDefaultFile() != null) {
          file = new File(red.getDefaultFile(), fname);
        } else {
          file = new File(fname);
        }
      } else if (file.isFile()) {
        file = new File(file.getParent(), fname);
      } else {
        file = new File(file, fname);
      }
			//open file for write
			try {
        if (file.exists()) {
          file.delete();
        }
				//Files.deleteIfExists(file.toPath());
        //file.mkdirs();
        file.createNewFile();
        this.f = new RandomAccessFile(file, "rw");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void send(TCP t, Progress prog) {
		byte[] array = new byte[50000000];
		int size = 0;
		try {
			int reps = (int)(f.length()/array.length)+1;
      prog.start(reps);
			t.sendSize(reps);
			while ((size = f.read(array)) > 0) {
				t.send(array, size, prog, 1024);
				prog.incRep();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void recv(TCP t, Progress prog) {
		byte[] array;
		int reps = t.recvSize();
		System.out.println("Reps: "+reps);
    prog.start(reps);
		int i = 0;
		try {
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

	public static void transfer(RedTongue red, boolean send_recv, File file, TCP t, Progress p)
      throws FileNotFoundException {
		FileTransfer f = new FileTransfer(red, send_recv, file, t);
		if (f.mode == SEND) {
			f.send(t, p);
		} else {
			f.recv(t, p);
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

    File file = null;
    if (args.length >= 2) {
      file = new File(args[1]);
    } else if (args.length < 2 && mode == SEND) {
      System.out.println("NOTE: Sender needs to specify a file to send");
      System.exit(0);
    }

    try {
      TCP t = getTCP(mode);
		  FileTransfer.transfer(null, mode, file, t, new TuiProgress());
      t.close();
    } catch (FileNotFoundException e) {
      System.out.println("Could not find file "+e.getMessage());
    }
	}
}
