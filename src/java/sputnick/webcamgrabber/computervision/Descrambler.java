package sputnick.webcamgrabber.computervision;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import vxp.QTLivePixelSource;

public class Descrambler extends Frame {

	static int originalVideoWidth = 320; // The overall size of your video
	static int originalVideoHeight = 240;
	static int frameWidth = 1280; // display size
	static int frameHeight = 960;
	static float scale = frameHeight / originalVideoHeight;
	static AffineTransform transform;
	static BufferedImage myImage;
	static BufferedImage scaled;
	static long elapsedTime;
	static long now;
	static QTLivePixelSource ps;
	static Descrambler myWindow;
	static boolean scanning = true;
	static boolean startGrabbing = true;
	private static int shiftBy = 0;

	Descrambler() { // this is like startmovie
		super();
		scaled = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
		transform = new AffineTransform();
		transform.scale(scale, scale);
		setSize(frameWidth, frameHeight);
		show();
		toFront();
		setLayout(null);
	}

	public static void main(String args[]) // this is like exit frame
	{
		myWindow = new Descrambler();
		// This is the object that give you the video pixels, the last parameter
		// is for dialog box
		ps = new QTLivePixelSource(originalVideoWidth, originalVideoHeight, 24);
		// ps.videoSettings();//pop up the settings window
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
		// ps.grabBackground();
		// ///////In every frame (like exit frame)///////
		while (scanning) {
			// you may want to farm this out to a thread
			LookAtAFrame(); // we look through all the pixels
			myWindow.repaint(); // we paint the winner
		}
	}

	static void LookAtAFrame() // this is where the action is, where we go
								// pixel by pixel through the video
	{
		ps.grabFrame(); // grab a frame
		// int[] straightPixels = ps.getPixelArray();//the current frame;
		int[] straightPixels = ps.getEmptyPixelArray();
		ps.getPixelArray(straightPixels);
		int[] rgb;
		if (startGrabbing) {
			for (int row = 0; row < ps.getVideoHeight(); row++) {
				// REPEAT FOR EACH ROW OF PIXELS
				int changedRowNumber = row; // image normal vertically
				// changedRowNumber = originalVideoHeight - row - 1; //image
				// flipped vertically
				for (int column = 0; column < ps.getVideoWidth(); column++) {
					// REPEAT FOR EACH PIXEL IN THE ROW
					//					
					// get pixel from one of the last x number of frames
					rgb = ps.getPixel(straightPixels, column, row);
					int alpha = 0;// randomizer.nextInt(255);
					int red = rgb[1];
					int green = rgb[2];
					int blue = rgb[3];
					int changedColumnNumber = column; // image normal
														// horizontally
					if (row % 2 == 0) {
						changedColumnNumber = Math.max(0, column - shiftBy);
					}
					else {
						changedColumnNumber = (column + shiftBy) % (originalVideoHeight - 1);
					}
					// int changedColumnNumber = kWidth - column; //image
					// flipped horizontally
					// move the pixels
					ps.setPixel(straightPixels, changedColumnNumber, changedRowNumber, red, green, blue, alpha);
				}// END FOR EACH PIXEL IN A ROW
			}// END FOR EACH ROW OF PIXELS
		}
		try {
			Thread.sleep(1);
		}
		catch (InterruptedException e) {
		}
		myImage = ps.getImage(straightPixels);
		elapsedTime = System.currentTimeMillis() - now;
		now = System.currentTimeMillis();
	} // end of lookat frame

	public void update(Graphics g) { // smoother picture
		paint(g);
	}

	public void paint(Graphics g) { // this is where we paint
		if (myImage != null) {
			// g.drawImage(myImage, 0, 0, this);//the background video
			// Graphics2D g2 = myImage.createGraphics();
			((Graphics2D) g).drawImage(myImage, transform, null);
			// g.drawImage(myImage.getScaledInstance(640,480,BufferedImage.SCALE_FAST),
			// 0, 0, this);//the background video
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
		else if (whichKey.equals("Right")) {
			shiftBy++;
		}
		else if (whichKey.equals("Left")) {
			shiftBy--;
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
