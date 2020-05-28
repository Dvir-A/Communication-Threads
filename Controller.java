package mmn15_1;



/**
 * An Controller for ComunicatingThreads
 * @author Dvir
 *
 */
public class Controller{
	private int _numOfThreads,_threadsFinished,_numOfRepeatingRemains,_numOfWaiting;
	private ComunicatingThreads _mainT;
	
	/**
	 * create a new controller for ComunicatingThreads
	 * @param mainComun - the ComunicatingThreads to control
	 * @param numOfThreads - the number of threads in this ComunicatingThreads
	 * @param numOfRepeating - the number of communication
	 */
	public Controller(ComunicatingThreads mainComun,int numOfThreads,int numOfRepeating) {
		this._numOfThreads = numOfThreads;
		this._threadsFinished=0;
		this._numOfRepeatingRemains = numOfRepeating;
		_numOfWaiting=0;
		_mainT=mainComun;
	}
	
	/**
	 * count the compersion in this round and notify when the count get to the limit.
	 */
	public synchronized void isCmp() {
		this._threadsFinished++;
		if(this._threadsFinished>=this._numOfThreads) {
			notifyAll();
		}
	}
	/**
	 * count the finished in this round and notify when the count get to the limit.
	 */
	public synchronized void isFinished() {
		this._numOfWaiting++;
		if(this._numOfWaiting>=this._numOfThreads) {
			notifyAll();
		}
	}
	
	/**
	 * wait for all the compersion in this round to end.
	 */
	public synchronized void waitForThreads() {
		while(this._threadsFinished < this._numOfThreads){
			try {
				wait();
			} catch (InterruptedException e) {}
		}
	}
	
	/**
	 * wait for the ComunicatingThreads reset the cycle
	 */
	public synchronized void waitForSycle() {
		if(_mainT._cycleFinished) {
			notifyAll();
		}
		while(!_mainT._cycleFinished) {
			try {
				wait();
			} catch (InterruptedException e) {}
		}
	}
	
	/**
	 *  wait for all threads to finish this cycle
	 */
	public synchronized void waitForAll() {
		if(this._numOfWaiting==1) {
			this._mainT.setCycleFinished(false);
		}
		if(this._numOfWaiting <this._numOfThreads){
			try {
				wait();
			} catch (InterruptedException e) {}
		}
	}
	
	/**
	 * indicate if the communication is done or not
	 * @return false if number of repeating is > 0 else return true
	 */
	public boolean isDone() {
		if(this._numOfRepeatingRemains>0) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return the number of threads that finish this cycle and now waiting
	 */
	public int getWaitingCnt() {
		return this._numOfWaiting;
	}
}
