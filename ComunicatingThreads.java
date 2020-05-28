package mmn15_1;

import java.util.Scanner;

import javax.swing.*;

/**
 *  * create a list of threads.<br>
	 * each thread change his value in every round according to the rule:
	 * 	<ul><li><b>if</b> thread.val is <b>bigger</b> then thread.left.val and thread.right.val
	 * 		<blockquote> thread.val--</blockquote></li>
	 * 		<li><b>if</b> thread.val is <b>between</b> thread.left.val and thread.right.val
	 * 		<blockquote> thread.val--</blockquote></li>
	 * 		<li><b>if</b> thread.val is <b>smaller </b>then thread.left.val and thread.right.val
	 * 		<blockquote> thread.val++</blockquote></li></ul><br>
 * @author Dvir Avikasis
 *
 */
public class ComunicatingThreads extends JFrame{
	private ValThread[] _threadArr;
	private JTextArea _txtArea;
	private int N,M;
	private Controller ctrl;
	protected boolean _cycleFinished,_exitNeeded,_isLast;
	public static final int MAX_VAL = 100;
	
	
	/**
	 * initialize communicating threads group of n threads and m communicating cycles
	 * @param m - cycles/rounds of the communication
	 * @param n - the number of threads 
	 */
	public ComunicatingThreads(int m,int n) { // m Rounds and n threads
		super("Comunicating Threads");
		this._cycleFinished=true;
		this._exitNeeded=false;
		this.N = n;
		this.M = m;
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this._txtArea = new JTextArea();
		this._txtArea.setEditable(false);
		JScrollPane scrollP = new JScrollPane(_txtArea);
		this.add(scrollP);
		this._threadArr = new ValThread[n];
		ctrl = new Controller(this,N,M);
		for(int i=0;i<n;i++) {
			this._threadArr[i] = new ValThread(i);
		}
	}
	@Override
	public synchronized String toString() {
		String str = "";
		for (ValThread valThread : _threadArr) {
			str+=(valThread._val+" ,");
		}
		str+="\n\n";
		return new String(str);
	}
	
	/**
	 * start the communication and manage the cycles
	 */
	public synchronized void startComunication() {
		_txtArea.append(toString());
		for (ValThread valThread : _threadArr) {
			valThread.start(); 
		}
		while(!ctrl.isDone()) {
			try {
				while(ctrl.getWaitingCnt()<N) {
					wait();
				}
				this._cycleFinished=false;
				this._txtArea.append(this.toString());
				if(--M==0) {
					_exitNeeded=true;
				}
				ctrl = new Controller(this,N, M);
				this._cycleFinished=true;
				for (ValThread valThread : _threadArr) {
					valThread.interrupt();
				}
			} catch (InterruptedException e) {}
		}
	}
	
	/**
	 *signal 
	 */
	public synchronized void signalAll() {
		notifyAll();
	}
	/**
	 *  Thread with value
	 */
	private class ValThread extends Thread{
		private int _val;
		private int _index;
		
		/**
		 * initialize a new thread with value between 0 to MAX_VAL
		 * @param ind - the index of the thread in thread's array
		 */
		public ValThread(int ind) {
			super();
			this._val= ((int)(Math.random()*MAX_VAL));
			this._index = ind;
		}
		
		@Override
		public void run() {
			while(!ctrl.isDone()) {
				ctrl.waitForSycle();
				if(_exitNeeded) {
					return;
				}
				final int cmp = cmp();
				ctrl.isCmp();
				ctrl.waitForThreads();
				if(cmp>0) {
					dec();
				}else if(cmp<0) {
					inc();
				}
				
				ctrl.isFinished();
				signalAll();
				ctrl.waitForAll();
			}
		}
		/**
		 * increase the value of this thread by one,
		 * if the value is equal to the max value, then
		 * the value stay unchanged.
		 */
		public  void inc() {
				this._val++;
		}
		/**
		 * decrease the value of this thread by one,
		 * if the value is equal to 0 ,then
		 * the value stay unchanged.
		 */
		public void dec() {
				this._val--;
		}
		
		/** compare this thread val to the val of his neighbors 
		 * @return <ul><li> 1 if this thread val bigger then val of his neighbors </li>
		 * 			   <li>-1 if this thread val smaller then val of his neighbors </li>
		 *  		   <li>0 if this thread val between the val of his neighbors </li></ul>
		 */
		public  int cmp() {
			final int pervCmpRes = _threadArr[this._index]._val-_threadArr[pervInd()]._val;
			final int nextCmpRes = _threadArr[this._index]._val-_threadArr[nextInd()]._val;
			if(pervCmpRes > 0 && nextCmpRes > 0) {
				return 1;
			}else if(pervCmpRes < 0 && nextCmpRes < 0) {
				return -1;
			}
			return 0;
		}
		
		/**
		 * Calculate the next index in the circular list of threads
		 * @return the next index in the circular list of threads
		 */
		public int nextInd() {
			final int ind =this._index+1;
			if(ind >= N) {
				return 0;
			}
			return ind;
		}
		/**
		 * Calculate the previous index in the circular list of threads
		 * @return the previous index in the circular list of threads
		 */
		public int pervInd() {
			final int ind=this._index-1;
			if(ind < 0) {
				return (N-1);
			}
			return ind;
		}
	}
	/**
	 * Set the cycle state (finished or not).
	 * @param cycleFinished - indicate the cycle state(finished or not).
	 */
	public synchronized void setCycleFinished(boolean cycleFinished) {
		this._cycleFinished = cycleFinished;
	}
	
	
	public static void main(String[] args) {
		int n,m;
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter the number of threds : ");
		n = scan.nextInt();
		System.out.println("Enter the number of rounds : ");
		m = scan.nextInt();
		scan.close();
		ComunicatingThreads comunT = new ComunicatingThreads(m, n);
		comunT.setSize(600, 600);
		comunT.setVisible(true);
		comunT.startComunication();
	}

}
