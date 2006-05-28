package sputnick.webcamgrabber.computervision;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import vxp.PixelSource;
import vxp.QTLivePixelSource;

public class RemoveBackManual extends Frame {

	static int kWidth; // The overall size of your video
	static int kHeight;
	static PixelSource ps;
	static RemoveBackManual myWindow;
	static int backgroundThreshold = 50;
	static Image myImage;
	static boolean scanning = true;

	RemoveBackManual() { // this is like startmovie
		kWidth = 320;
		kHeight = 240;
	}

	public static void main(String args[]) // this is like exit frame
	{
		myWindow = new RemoveBackManual();
		myWindow.resize(kWidth, kHeight);
		myWindow.show();
		myWindow.toFront();
		myWindow.setLayout(null);
		// This is the object that give you the video pixels, the last parameter
		// is for dialog box
		ps = new QTLivePixelSource(kWidth, kHeight, 24);
		 ps.videoSettings();//pop up the settings window
		// add a listener for shutting the window, give it a method to call
		// (thisWindowClosing)
		myWindow.addWindowListener(new java.awt.event.WindowAdapter() {

			public void windowClosing(java.awt.event.WindowEvent e) {
				myWindow.thisWindowClosing(e);
			}
		});
		// add a listener for pressing a key , give it a method to call
		// (KeyPressed)
		myWindow.addKeyListener(new java.awt.event.KeyAdapter() {

			public void keyPressed(java.awt.event.KeyEvent e) {
				myWindow.KeyPressed(e);
			}
		});
		// try {Thread.sleep(5000);} catch(InterruptedException e) {}
		ps.grabBackground();
		System.out.println("REMEMBER TO PRESS 'b' WITH NOTHING IN THE FOREGROUND TO SET BACKGROUND (other window must be in front)");
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
		int[] rgb;
		int[] brgb;
		ps.grabFrame(); // grab a frame
		// /REMEMBER TO PRESS "B" WITH NOTHING IN THE FOREGROUND TO REMEMBER
		// BACKGROUND
		// REPEAT FOR EACH ROW OF PIXELS
		for (int row = 0; row < kHeight; row++) {
			// REPEAT FOR EACH PIXEL IN THE ROW
			for (int column = 0; column < kWidth; column++) {
				rgb = ps.getPixel(column, row);
				brgb = ps.getBackGroundPixel(column, row);
				// System.out.println ( "bp " + brgb[2] );
				// if any of the colors are different by more than 50
				// if ((Math.abs(brgb[0]-rgb[0]) + Math.abs(brgb[1]-rgb[1]) +
				// Math.abs(brgb[2]-rgb[2]))> backgroundThreshold)
				if ((Math.abs(brgb[1] - rgb[1]) > backgroundThreshold)
						|| (Math.abs(brgb[2] - rgb[2]) > backgroundThreshold)
						|| (Math.abs(brgb[3] - rgb[3]) > backgroundThreshold))
				// if ((Math.abs(brgb[0]-rgb[0])> backgroundThreshold) &&
				// (Math.abs(brgb[1]-rgb[1])> backgroundThreshold) &&
				// (Math.abs(brgb[2]-rgb[2])> backgroundThreshold))
				{
					ps.setPixel(column, row, 255); // change the alpha
				}
				else {
					ps.setPixel(column, row, 255, 0, 0, 255);
				}
				// }
			}// END FOR EACH PIXEL IN A ROW
		}// END FOR EACH ROW OF PIXELS
		try {
			Thread.sleep(33);
		}
		catch (InterruptedException e) {
		}
		myImage = ps.getImage();
	} // end of lookat frame

	// public void update(Graphics g){ //smoother picture
	// paint(g);
	// }
	public void paint(Graphics g) { // this is where we paint
		if (myImage != null) {
			g.drawImage(myImage, 0, 0, this);// the background video
		}
	}

	public boolean KeyPressed(java.awt.event.KeyEvent e) { // pop up dialog
		// System.out.println ( "whichkey" + e.getKeyText(e.getKeyCode()));
		String whichKey = e.getKeyText(e.getKeyCode());
		if (whichKey.equals("S")) {
			ps.videoSettings();
		}
		else if (whichKey.equals("B")) {
			ps.grabBackground();
		}
		return (true);
	}

	void thisWindowClosing(java.awt.event.WindowEvent e) {
		scanning = false;
		System.out.println("quit");
		ps.killSession();
		dispose();
		System.exit(5);
	}
}
