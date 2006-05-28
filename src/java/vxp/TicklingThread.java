package vxp;

/*
 * Created on Feb 18, 2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * @author Administrator
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TicklingThread implements Runnable {

	public PixelSource ps;
	protected Thread st;
	protected int interval = 33;
	boolean running = true;

	TicklingThread(PixelSource _ps, int _frameRate) {
		ps = _ps;
		interval = 1000 / _frameRate;
	}

	public void startMe() {
		st = new Thread(this);
		st.start();
		System.out.println("starting tickle");
	}

	public void stopMe() {
		running = false;
		System.out.println("stop the tickle");
	}

	public void run() {
		while (running) {
			ps.idleIt();
			ps.tellVideoListeners();
			try {
				Thread.sleep(interval);
			}
			catch (InterruptedException e) {
			}
		}
	}
}
