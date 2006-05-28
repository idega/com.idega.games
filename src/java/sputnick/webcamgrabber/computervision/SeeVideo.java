package sputnick.webcamgrabber.computervision;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import vxp.PixelSource;
import vxp.QTLivePixelSource;

public class SeeVideo extends Frame {

	static int kWidth; // The overall size of your video
	static int kHeight;
	static long elapsedTime;
	static long now;
	static PixelSource ps;
	static SeeVideo myWindow;
	static Image myImage;
	static boolean scanning = true;

	SeeVideo() { // this is like startmovie
		kWidth = 640;
		kHeight = 480;
	}

	public static void main(String args[]) // this is like exit frame
	{
		myWindow = new SeeVideo();
		myWindow.setSize(kWidth, kHeight);
		myWindow.show();
		myWindow.toFront();
		myWindow.setLayout(null);
		// This is the object that give you the video pixels, the last parameter
		// is for dialog box
		ps = new QTLivePixelSource(kWidth, kHeight, 100);
		// ps.videoSettings();//pop up the settings window
		// add a listener for shutting the window, give it a method to call
		// (thisWindowClosing)
		myWindow.addWindowListener(new java.awt.event.WindowAdapter() {

			public void windowClosing(java.awt.event.WindowEvent e) {
				myWindow.thisWindowClosing(e);
			}
		});
		myWindow.addKeyListener(new java.awt.event.KeyAdapter() {

			public void keyPressed(java.awt.event.KeyEvent e) {
				myWindow.KeyPressed(e);
			}
		});
		// ///////In every frame (like exit frame)///////
		while (scanning) // you may want to farm this out to a thread
		{
			LookAtAFrame(); // we look through all the pixels
			myWindow.repaint(); // we paint the winner
		}
	}

	static void LookAtAFrame() // this is where the action is, where we go
								// pixel by pixel through the video
	{
		// ps.idleIt(100);
		ps.grabFrame(); // grab a frame
		try {
			Thread.sleep(1);
		}
		catch (InterruptedException e) {
		}
		myImage = ps.getImage();
		elapsedTime = System.currentTimeMillis() - now;
		now = System.currentTimeMillis();
	} // end of lookat frame

	public void update(Graphics g) {
		paint(g); // remove flicker
	}

	public void paint(Graphics g) { // this is where we paint
		if (myImage != null) {
			g.drawImage(myImage, 0, 0, this);// the background video
		}
	}

	public boolean KeyPressed(KeyEvent e) { // brings up the dialog box
		// System.out.println ( "whichkey" + e.getKeyText(e.getKeyCode()));
		String whichKey = KeyEvent.getKeyText(e.getKeyCode());
		if (whichKey.equals("S")) {
			ps.videoSettings();
		}
		else if (whichKey.equals("T")) {
			System.out.println("Time: " + elapsedTime + " ms per frame including painting");
		}
		return (true);
	}

	void thisWindowClosing(java.awt.event.WindowEvent e) {
		scanning = false;
		System.out.println("quit");
		dispose();
		ps.killSession();
		System.exit(5);
	}
}
