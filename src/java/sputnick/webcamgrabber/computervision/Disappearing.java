package sputnick.webcamgrabber.computervision;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import vxp.PixelSource;
import vxp.QTLivePixelSource;
import com.idega.util.math.MersenneTwisterFast;


public class Disappearing extends Frame {

	static int originalVideoWidth = 320; // The overall size of your video
	static int originalVideoHeight = 240;
	static int frameWidth = originalVideoWidth * 2; // display size
	static int frameHeight = originalVideoHeight * 2;
	static float scale = frameHeight / originalVideoHeight;
	static AffineTransform transform;
	static BufferedImage myImage;
	static BufferedImage scaled;
	static long elapsedTime;
	static long now;
	static PixelSource ps;
	static Disappearing myWindow;
	static boolean scanning = true;
	static int frameCounterRecyclesAtTheXFrameNumber = 0;// first frame is 0
	static int numberOfFramesToKeep = 24;
	static int[][] lastFrames = new int[numberOfFramesToKeep][];// last x frames
	static boolean startGrabbing = false;
	static boolean behaviourToggle = true;
	static int threshHold = 15;
	static MersenneTwisterFast randomizer = null;

	Disappearing() { // this is like startmovie
		super();
		scaled = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
		transform = new AffineTransform();
		transform.scale(scale, scale);
		setSize(frameWidth + 20, frameHeight + 20);
		show();
		toFront();
		setLayout(null);
	}

	public static void main(String args[]) // this is like exit frame
	{
		myWindow = new Disappearing();
		// This is the object that give you the video pixels, the last parameter
		// is for dialog box
		ps = new QTLivePixelSource(originalVideoWidth, originalVideoHeight, 24);
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
		// uncomment if we need to store the first background/frame
		ps.grabBackground();
		// ///////In every frame (like exit frame)///////
		while (scanning) {
			// you may want to farm this out to a thread
			LookAtAFrame(); // we look through all the pixels
			myWindow.repaint(); // we paint the winner
			frameCounterRecyclesAtTheXFrameNumber++;
			if (frameCounterRecyclesAtTheXFrameNumber == numberOfFramesToKeep) {
				startGrabbing = true;
				frameCounterRecyclesAtTheXFrameNumber = 0;
			}
		}
	}

	static void LookAtAFrame() // this is where the action is, where we go
								// pixel by pixel through the video
	{
		ps.grabFrame(); // grab a frame
		// int[] straightPixels = ps.getPixelArray();//the current frame;
		int[] straightPixels = new int[ps.vidWidth * originalVideoHeight];
		ps.getPixelArray(straightPixels);
		int[] currentFramePixels = new int[ps.vidWidth * originalVideoHeight];
		ps.getPixelArray(currentFramePixels);
		int[] oldFramePixelRGB;
		int[] backgroundPixelRGB;
		int[] currentFramePixelRGB;
		// int[] myPixels = new int[ps.vidWidth * originalVideoHeight];
		// ps.getPixelArray(myPixels); //get an independent array full of
		// pixels.
		lastFrames[frameCounterRecyclesAtTheXFrameNumber] = straightPixels;
		if (startGrabbing) {
			int[] frameToCompareTo = lastFrames[(frameCounterRecyclesAtTheXFrameNumber + 1) % numberOfFramesToKeep];
			for (int row = 0; row < originalVideoHeight; row++) {
				// REPEAT FOR EACH ROW OF PIXELS
				for (int column = 0; column < originalVideoWidth; column++) {
					// REPEAT FOR EACH PIXEL IN THE ROW
					// get pixels
					oldFramePixelRGB = ps.getPixel(frameToCompareTo, column, row);
					currentFramePixelRGB = ps.getPixel(currentFramePixels, column, row);
					backgroundPixelRGB = ps.getBackGroundPixel(column, row);
					// ps.setPixel( straightPixels, column, row,255); //change
					// the alpha
					int alpha = 255;// opaque
					// randomizer.nextInt(255);
					int red = oldFramePixelRGB[1];
					int green = oldFramePixelRGB[2];
					int blue = oldFramePixelRGB[3];
					int redCurrent = currentFramePixelRGB[1];
					int greenCurrent = currentFramePixelRGB[2];
					int blueCurrent = currentFramePixelRGB[3];
					int redBack = backgroundPixelRGB[1];
					int greenBack = backgroundPixelRGB[2];
					int blueBack = backgroundPixelRGB[3];
					// we want things to dissapear and the current pixel is very
					// similar to the oldest frame pixel
					if (behaviourToggle
							&& (((threshHold <= (Math.abs(redCurrent - red)))
									&& (threshHold <= (Math.abs(greenCurrent - green))) && (threshHold <= (Math.abs(blueCurrent
									- blue)))) && ((threshHold <= (Math.abs(redCurrent - redBack)))
									&& (threshHold <= (Math.abs(greenCurrent - greenBack))) && (threshHold <= (Math.abs(blueCurrent
									- blueBack)))))) {
						// use the pixel from the current frame
						// ps.setPixel(straightPixels, column, row, redCurrent,
						// greenCurrent, blueCurrent, alpha);
					}
					// we want things to appear and the current pixel is not
					// similar to the oldest frame pixel
					else if (!behaviourToggle
							&& !((threshHold <= (Math.abs(redCurrent - red)))
									&& (threshHold <= (Math.abs(greenCurrent - green))) && (threshHold <= (Math.abs(blueCurrent
									- blue))))) {
					}
					else {
						// disappearing
						ps.setPixel(currentFramePixels, column, row, redBack, greenBack, blueBack, alpha);
					}
				}// END FOR EACH PIXEL IN A ROW
			}// END FOR EACH ROW OF PIXELS
		}
		try {
			Thread.sleep(1);
		}
		catch (InterruptedException e) {
		}
		myImage = ps.getImage(currentFramePixels);
		elapsedTime = System.currentTimeMillis() - now;
		now = System.currentTimeMillis();
	} // end of lookat frame

	public void update(Graphics g) { // smoother picture
		paint(g);
	}

	public void paint(Graphics g) { // this is where we paint
		if (myImage != null) {
			((Graphics2D) g).drawImage(myImage, transform, null);
		}
	}

	public boolean KeyPressed(KeyEvent e) { // pop up dialog
		// System.out.println ( "whichkey" + e.getKeyText(e.getKeyCode()));
		String whichKey = KeyEvent.getKeyText(e.getKeyCode());
		if (whichKey.equals("S")) {
			ps.videoSettings();
		}
		else if (whichKey.equals("T")) {
			System.out.println("Time: " + elapsedTime + " ms per frame including painting");
		}
		else if (whichKey.equals("D")) {
			behaviourToggle = !behaviourToggle;
		}
		else if (whichKey.equals("Up")) {
			threshHold++;
			System.out.println("threshHold " + threshHold);
		}
		else if (whichKey.equals("Down")) {
			threshHold--;
			System.out.println("threshHold " + threshHold);
		}
		else if (whichKey.equals("B")) {
			ps.grabBackground();
		}
		else {
			try {
				numberOfFramesToKeep = Integer.parseInt(whichKey);
				startGrabbing = false;
				frameCounterRecyclesAtTheXFrameNumber = 0;
			}
			catch (NumberFormatException e1) {
				// e1.printStackTrace();
			}
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
