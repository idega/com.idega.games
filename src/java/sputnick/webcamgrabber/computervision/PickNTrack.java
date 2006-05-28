package sputnick.webcamgrabber.computervision;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import sputnick.webcamgrabber.util.ImageWrangler;
import vxp.PixelSource;
import vxp.QTLivePixelSource;

public class PickNTrack extends Frame {

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
	static int x = 0; // these are the x and y position of the dot you are
	// drawing
	static int y = 0;
	static int redGoal = 210; // these describe the color you are chasing
	static int greenGoal = 20; // 255, 255, 255 would be white
	static int blueGoal = 20;
	static PixelSource ps;
	static ImageWrangler iw;
	static PickNTrack myWindow;
	static boolean scanning = true;

	PickNTrack() { // this is like startmovie
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
		myWindow = new PickNTrack();
		// This is the object that give you the video pixels, the last parameter
		// is for dialog box
		ps = new QTLivePixelSource(originalVideoWidth, originalVideoHeight, 24);
		// ps.videoSettings();//pop up the settings window
		iw = new ImageWrangler(ps.vidWidth, originalVideoHeight, ps.getMasks());
		// add a listener for shutting the window, give it a method to call
		// (thisWindowClosing)
		myWindow.addWindowListener(new java.awt.event.WindowAdapter() {

			public void windowClosing(java.awt.event.WindowEvent e) {
				myWindow.thisWindowClosing(e);
			}
		});
		// add a listener for clicking the mouse in the window, give it a method
		// to call (MouseClicked)
		myWindow.addMouseListener(new java.awt.event.MouseAdapter() {

			public void mouseClicked(java.awt.event.MouseEvent e) {
				myWindow.MouseClicked(e);
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
		int[] rgb;
		int worldRecord = 60000; // some huge number
		ps.grabFrame(); // grab a frame
		for (int row = 0; row < originalVideoHeight; row++) // REPEAT FOR EACH
		// ROW OF PIXELS
		{
			// REPEAT FOR EACH PIXEL IN THE ROW
			for (int column = 0; column < originalVideoWidth; column++) {
				rgb = ps.getPixel(column, row);
				// ps.setPixel(column,row,255); //set the alpha to opaque
				int differenceInColor = Math.abs(rgb[1] - redGoal) + Math.abs(rgb[3] - blueGoal)
						+ Math.abs(rgb[2] - greenGoal);
				if (differenceInColor < worldRecord) {
					x = (int) (column * scale);
					y = (int) (row * scale);
					worldRecord = differenceInColor;
				}
				// System.out.println ("red" + red + " blue" + blue + " green" +
				// green);
				// }
			}// END FOR EACH PIXEL IN A ROW
		}// END FOR EACH ROW OF PIXELS
		try {
			Thread.sleep(1);
		}
		catch (InterruptedException e) {
		}
		myImage = iw.imageFromArray(ps.getPixelArray());
		elapsedTime = System.currentTimeMillis() - now;
		now = System.currentTimeMillis();
	} // end of lookat frame

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) { // this is where we paint
		if (myImage != null) {
			// g.drawImage(myImage,0,0,this);//the background video
			((Graphics2D) g).drawImage(myImage, transform, null);
			g.setColor(new Color(0, 0, 0));// black
			g.fillOval(x, y, 20, 20);// the dot for tracking
		}
	}

	public boolean MouseClicked(java.awt.event.MouseEvent evt) { // this
		// repicks
		// the color
		// you are
		// chasing
		int[] rgb;
		// find a color to track
		int x = evt.getX();
		int y = evt.getY();
		rgb = ps.getPixel(x, y);
		redGoal = rgb[1];
		greenGoal = rgb[2];
		blueGoal = rgb[3];
		System.out.println("clicked x" + x + " y" + y + " R" + redGoal + " G" + greenGoal + " B" + blueGoal);
		return (true);
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
