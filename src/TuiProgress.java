public class TuiProgress implements Progress {
	private int reps;
	private int currentRep = 0;
	private int fragment;
	private int prog = 0;

	public TuiProgress() {
		this.reps = 1;
		this.fragment = 100;
		System.out.println("Percent complete: ");
	}

	public TuiProgress(int reps) {
		this.reps = reps;
		this.fragment = 100/reps;
	}

	public void updateProgress(short percent) {
		if (currentRep == reps -1 && percent == 100) {
			System.out.println(percent);
			prog = percent;
		} else {
			int tempProg = currentRep*fragment + (percent*fragment/100);
			if (tempProg != prog) {
				System.out.println(tempProg);
				prog = tempProg;
			}
		}
	}

	public void incRep() {
		if (currentRep < reps - 1) {
			currentRep++;
		}
	}

	public static void main(String[] args) {
		System.out.println("simple progress:");
		TuiProgress t1 = new TuiProgress();
		for (short i=1; i<=100; i++) {
			t1.updateProgress(i);
		}
		t1 = new TuiProgress(4);
		System.out.println("Compounded progress:");
		for (short j=0; j<4; j++) {
			for (short i=0; i<=100; i += 4) {
				t1.updateProgress(i);
			}
			t1.incRep();
		}
	}
}
