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

	private FileTransfer(boolean send_recv, String file) {
		this.mode = send_recv;
		if (mode == SEND) {
			//TODO: open file for read
			try {
				f = new RandomAccessFile(file, "r");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			//TODO: open file for write
			try {
				File f = new File(file);
				Files.deleteIfExists(f.toPath());
				this.f = new RandomAccessFile(file, "rw");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void send() {
		TCP t = new TCP(TCP.SEND, null, 8199);
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

	private void recv() {
		TCP t = new TCP(TCP.RECV, null, 8199);
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

	public static void transfer(boolean send_recv, String file) {
		FileTransfer f = new FileTransfer(send_recv, file);
		if (f.mode == SEND) {
			f.send();
		} else {
			f.recv();
		}
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("USAGE: FileTransfer <mode 0/1> <filename>");
			System.exit(0);
		}
		int modei = Integer.parseInt(args[0]);
		boolean mode = (modei == 0) ? RECV : SEND;
		FileTransfer.transfer(mode, args[1]);
	}
}
