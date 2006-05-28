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
public class Idler extends TicklingThread  {


	Idler (PixelSource _ps,int _frameRate){
		super(_ps,_frameRate);

	}

	public void run(){
	

			while (running){
				ps.idleIt();
	
				try {Thread.sleep(interval);} catch (InterruptedException e) {}
				
			}
	}

}