package sputnick.webcamgrabber.computervision;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import vxp.PixelSource;
import vxp.QTLivePixelSource;

public class EdgeManual extends Frame {

	static int kWidth; // The overall size of your video
	static int kHeight;
	static PixelSource ps;
	static EdgeManual myWindow;
	static int Threshold = 20;
	static Image myImage;
	static boolean scanning = true;

	EdgeManual() { // this is like startmovie
		kWidth = 320;
		kHeight = 240;
	}

	public static void main(String args[]) // this is like exit frame
	{
		myWindow = new EdgeManual();
		myWindow.resize(kWidth, kHeight);
		myWindow.show();
		myWindow.toFront();
		myWindow.setLayout(null);
		// This is the object that give you the video pixels, the last parameter
		// is for dialog box
		ps = new QTLivePixelSource(kWidth, kHeight, 24);
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
		myWindow.addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
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
		int[] rgb;
		int[] prgb;
		ps.grabFrame(); // grab a frame
		for (int row = 0; row < kHeight; row++) // REPEAT FOR EACH ROW OF PIXELS
		{
			for (int column = 1; column < kWidth; column++) // REPEAT FOR EACH
			// PIXEL IN THE ROW
			{
				rgb = ps.getPixel(column, row);
				// /////EDGE
				prgb = ps.getPixel(column - 1, row);
				if ((Math.abs(prgb[1] - rgb[1]) > 30) || (Math.abs(prgb[2] - rgb[2]) > 30)
						|| (Math.abs(prgb[3] - rgb[3]) > 30)) {
					ps.setPixel(column - 1, row, 255, 0, 0, 255);
				}
			}// END FOR EACH PIXEL IN A ROW
		}// END FOR EACH ROW OF PIXELS
		try {
			Thread.sleep(33);
		}
		catch (InterruptedException e) {
		}
		myImage = ps.getImage();
	} // end of lookat frame

	// public void update(Graphics g){
	// paint(g);
	// }
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
