package sputnick.webcamgrabber.computervision;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import vxp.PixelSource;
import vxp.QTLivePixelSource;

public class BlurManual extends Frame {

	static int kWidth; // The overall size of your video
	static int kHeight;
	static PixelSource ps;
	static BlurManual myWindow;
	static int[] blurredPixels;
	static Image myImage;
	static boolean scanning = true;

	BlurManual() { // this is like startmovie
		kWidth = 320;
		kHeight = 240;
	}

	public static void main(String args[]) // this is like exit frame
	{
		myWindow = new BlurManual();
		myWindow.resize(kWidth, kHeight);
		myWindow.show();
		myWindow.toFront();
		myWindow.setLayout(null);
		// This is the object that give you the video pixels, the last parameter
		// is for dialog box
		ps = new QTLivePixelSource(kWidth, kHeight, 50);
		ps.videoSettings();// pop up the settings window
		// make a new array you do on want blurred pixel use to blur pixels
		blurredPixels = ps.getEmptyPixelArray();
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
		ps.grabFrame(); // grab a frame
		for (int row = 0; row < ps.getVideoWidth(); row++) // REPEAT FOR EACH
															// ROW OF PIXELS
		{
			for (int column = 1; column < ps.getVideoHeight(); column++) // REPEAT
																			// FOR
																			// EACH
			// PIXEL IN THE ROW
			{
				// /////BLUR
				int[] totalrgb = { 0, 0, 0 };
				if ((column > 2) && (column < ps.getVideoWidth() - 2) && (row > 2) && (row < ps.getVideoHeight() - 2)) {
					for (int r = -2; r < 3; r++) {
						for (int c = -2; c < 3; c++) {
							int[] nrgb = ps.getPixel(column + c, row + r);
							totalrgb[0] = totalrgb[0] + nrgb[1];
							totalrgb[1] = totalrgb[1] + nrgb[2];
							totalrgb[2] = totalrgb[2] + nrgb[3];
						}
					}
					ps.setPixel(blurredPixels, column, row, totalrgb[0] / 25, totalrgb[1] / 25, totalrgb[2] / 25, 255);
				}
			}// END FOR EACH PIXEL IN A ROW
		}// END FOR EACH ROW OF PIXELS
		try {
			Thread.sleep(33);
		}
		catch (InterruptedException e) {
		}
		myImage = ps.getImage(blurredPixels);
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
			// ps.grabBackground();
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
