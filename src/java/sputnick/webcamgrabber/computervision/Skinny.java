package sputnick.webcamgrabber.computervision;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import vxp.QTLivePixelSource;

public class Skinny extends Frame {

	static int kWidth; // The overall size of your video
	static int kHeight;
	static QTLivePixelSource ps;
	static Skinny myWindow;
	static float hueUpper = .1f;
	static float hueLower = .06f;
	static float satUpper = .45f;
	static float satLower = .2f;
	static Image myImage;
	static boolean scanning = true;

	Skinny() { // this is like startmovie
		kWidth = 320;
		kHeight = 240;
	}

	public static void main(String args[]) // this is like exit frame
	{
		myWindow = new Skinny();
		myWindow.resize(kWidth, kHeight);
		myWindow.show();
		myWindow.toFront();
		myWindow.setLayout(null);
		// This is the object that give you the video pixels, the last parameter
		// is for dialog box
		ps = new QTLivePixelSource(kWidth, kHeight, 50);
		ps.videoSettings();// pop up the settings window
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
		System.out.println("USE ARROW AND SHIFT KEY TO ADJUST THRESHOLDS");
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
		float[] hsb;
		ps.grabFrame(); // grab a frame
		for (int row = 0; row < kHeight; row++) // REPEAT FOR EACH ROW OF PIXELS
		{
			for (int column = 0; column < kWidth; column++) // REPEAT FOR EACH
															// PIXEL IN THE ROW
			{
				hsb = ps.getPixelHSB(column, row);
				if ((hsb[0] < hueUpper) && (hsb[0] > hueLower) && (hsb[1] > satLower) && (hsb[1] < satUpper)) {
					ps.setPixel(column, row, 255); // change the alpha
				}
				else {
					ps.setPixel(column, row, 255, 0, 0, 255);
				}
				// }
			}// END FOR EACH PIXEL IN A ROW
		}// END FOR EACH ROW OF PIXELS
		try {
			Thread.sleep(3);
		}
		catch (InterruptedException e) {
		}
		myImage = ps.getImage();
	} // end of lookat frame

	public void update(Graphics g) { // smoother picture
		paint(g);
	}

	public void paint(Graphics g) { // this is where we paint
		if (myImage != null) {
			g.drawImage(myImage, 0, 0, this);// the background video
		}
	}

	public boolean KeyPressed(KeyEvent e) { // pop up dialog
		// System.out.println ( "whichkey" + e.getKeyText(e.getKeyCode()));
		String whichKey = KeyEvent.getKeyText(e.getKeyCode());
		if (whichKey.equals("S")) {
			ps.videoSettings();
		}
		else if (whichKey.equals("B")) {
			ps.grabBackground();
		}
		if (e.isShiftDown()) {
			if (whichKey.equals("Up")) {
				hueUpper = hueUpper + .01f;
				System.out.println("hueUpper " + hueUpper);
			}
			else if (whichKey.equals("Down")) {
				hueUpper = hueUpper - .01f;
				System.out.println("hueUpper " + hueUpper);
			}
			else if (whichKey.equals("Right")) {
				hueLower = hueLower + .01f;
				System.out.println("hueLower " + hueLower);
			}
			else if (whichKey.equals("Left")) {
				hueLower = hueLower - .01f;
				System.out.println("hueLower " + hueLower);
			}
		}
		else {
			if (whichKey.equals("Up")) {
				satUpper = satUpper + .01f;
				System.out.println("satUpper " + satUpper);
			}
			else if (whichKey.equals("Down")) {
				satUpper = satUpper - .01f;
				System.out.println("satUpper " + satUpper);
			}
			else if (whichKey.equals("Right")) {
				satLower = satLower + .01f;
				System.out.println("satLower " + satLower);
			}
			else if (whichKey.equals("Left")) {
				satLower = satLower - .01f;
				System.out.println("satLower " + satLower);
			}
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
