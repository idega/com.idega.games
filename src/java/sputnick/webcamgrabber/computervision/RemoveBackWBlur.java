package sputnick.webcamgrabber.computervision;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import sputnick.webcamgrabber.util.ImageWrangler;
import vxp.PixelSource;
import vxp.QTLivePixelSource;

public class RemoveBackWBlur extends Frame {

	static int kWidth = 320; // The overall size of your video
	static int kHeight = 240;
	static long elapsedTime;
	static long now;
	static PixelSource ps;
	static ImageWrangler iw;
	static RemoveBackWBlur myWindow;
	static int backgroundThreshold = 50;
	static BufferedImage myImage;
	static boolean scanning = true;

	RemoveBackWBlur() { // this is like startmovie
		super();
		kWidth = 320;
		kHeight = 240;
	}

	public static void main(String args[]) // this is like exit frame
	{
		myWindow = new RemoveBackWBlur();
		myWindow.setSize(kWidth, kHeight);
		myWindow.show();
		myWindow.toFront();
		myWindow.setLayout(null);
		// This is the object that give you the video pixels, the last parameter
		// is for dialog box
		ps = new QTLivePixelSource(kWidth, kHeight, 24);
		ps.videoSettings();// pop up the settings window
		iw = new ImageWrangler(ps.vidWidth, kHeight, ps.getMasks());
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
		ps.grabBackground();
		// blur the background
		int[] background = ps.getPackedBackground();
		iw.blurArray(background);
		ps.unpackBackground();
		// ///////In every frame (like exit frame)///////
		while (scanning) // you may want to farm this out to a thread
		{
			LookAtAFrame(); // we look through all the pixels
			myWindow.repaint(); // we paint the winner
		}
	}

	// this is where the action is, where we go
	// pixel by pixel through the video
	static void LookAtAFrame() {
		int[] rgb;
		int[] brgb;
		ps.grabFrame(); // grab a frame
		int[] blurredPixels = new int[ps.vidWidth * kHeight];
		ps.getPixelArray(blurredPixels); // get an independent array full of
		// pixels.
		int[] straightPixels = ps.getPixelArray();
		// COMMENT OUT THIS NEXT LINE AND WATCH THE SPEED TRIPLE;
		iw.blurArray(blurredPixels);
		// /REMEMBER TO PRESS "B" WITH NOTHING IN THE FOREGROUND TO REMEMBER
		// BACKGROUND
		// REPEAT FOR EACH ROW OF PIXELS
		for (int row = 0; row < kHeight; row++) {
			// REPEAT FOR EACH PIXEL IN THE ROW
			for (int column = 0; column < kWidth; column++) {
				// pull the rgb out of the blurredPixels
				rgb = ps.getPixel(blurredPixels, column, row);
				brgb = ps.getBackGroundPixel(column, row);
				// if any of the colors are different by more than 50
				// adjust the straight array no the blurred on
				if ((Math.abs(brgb[1] - rgb[1]) > backgroundThreshold)
						|| (Math.abs(brgb[2] - rgb[2]) > backgroundThreshold)
						|| (Math.abs(brgb[3] - rgb[3]) > backgroundThreshold)) {
					ps.setPixel(straightPixels, column, row, 255); // change
					// the alpha
				}
				else {
					ps.setPixel(straightPixels, column, row, 255, 0, 0, 255);
				}
				// }
			}// END FOR EACH PIXEL IN A ROW
		}// END FOR EACH ROW OF PIXELS
		try {
			Thread.sleep(1);
		}
		catch (InterruptedException e) {
		}
		myImage = iw.imageFromArray(straightPixels);
		elapsedTime = System.currentTimeMillis() - now;
		now = System.currentTimeMillis();
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
			// blur the background
			int[] background = ps.getPackedBackground();
			iw.blurArray(background);
			ps.unpackBackground();
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
