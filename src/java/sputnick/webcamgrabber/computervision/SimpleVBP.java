package sputnick.webcamgrabber.computervision;

/**
 * Applet1.java
 * 
 * Description:
 * 
 * @author Dan O'Sullivan
 * @version
 */
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import vxp.PixelSource;
import vxp.QTLivePixelSource;

public class SimpleVBP extends java.applet.Applet {

	// IMPORTANT: Source code between BEGIN/END comment pair will be regenerated
	// every time the form is saved. All manual changes will be overwritten.
	// BEGIN GENERATED CODE
	// member declarations
	// END GENERATED CODE
	boolean isStandalone = false;
	static int kWidth = 320; // The overall size of your video
	static int kHeight = 240;
	static int x = 0; // these are the x and y position of the dot you are
						// drawing
	static int y = 0;
	static int redGoal = 210; // these describe the color you are chasing
	static int greenGoal = 20; // 255, 255, 255 would be white
	static int blueGoal = 20;
	static PixelSource ps;
	static Image myImage;
	static boolean scanning = true;
	static PCanvas p;
	static Thread pThread;

	public SimpleVBP() {
	}

	// Initialize the applet
	public void init() {
		ps = new QTLivePixelSource(kWidth, kHeight, 24);
		// ps.videoSettings();//pop up the settings window
		// add a listener for clicking the mouse in the window, give it a method
		// to call (MouseClicked)
		addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				MouseClicked(e);
			}
		});
		// add a listener for pressing a key , give it a method to call
		// (KeyPressed)
		addKeyListener(new java.awt.event.KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				KeyPressed(e);
			}
		});
		try {
			System.out.println("init");
			setLocation(new java.awt.Point(0, 0));
			setLayout(null);
			setSize(new java.awt.Dimension(400, 300));
			pThread = new pThread(this);
			pThread.start();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void destroy() {
		ps.killSession();
	}

	 void LookAtAFrame() // this is where the action is, where we go
								// pixel by pixel through the video
	{
		int[] rgb;
		int worldRecord = 60000; // some huge number
		ps.grabFrame(); // grab a frame
		int[] straightPixels = ps.getPixelArray();
		// REPEAT FOR EACH ROW OF PIXELS
		for (int row = 0; row < ps.getVideoHeight(); row++) {
			// REPEAT FOR EACH PIXEL IN THE ROW
			for (int column = 0; column < ps.getVideoWidth(); column++) {
				rgb = ps.getPixel(column, row);
				// set the alpha to opaque
				ps.setPixel(straightPixels, column, row, rgb[1], rgb[2], rgb[3], 255);
				int differenceInColor = Math.abs(rgb[1] - redGoal) + Math.abs(rgb[3] - blueGoal)
						+ Math.abs(rgb[2] - greenGoal);
				if (differenceInColor < worldRecord) {
					x = column;
					y = row;
					worldRecord = differenceInColor;
				}
				// System.out.println ("red" + red + " blue" + blue + " green" +
				// green);
				// }
			}// END FOR EACH PIXEL IN A ROW
		}// END FOR EACH ROW OF PIXELS
		myImage = ps.getImage(straightPixels);
	} // end of lookat frame

	public boolean MouseClicked(java.awt.event.MouseEvent evt) {
		// this repicks the color you are chasing
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
		return (true);
	}

	public void update(Graphics g) {
		paint(g);
	}

	public void paint(Graphics g) { // this is where we paint
		if (myImage != null) {
			g.drawImage(myImage, 0, 0, this);// the background video
			g.setColor(new Color(0, 0, 0));// black
			g.fillOval(x, y, 10, 10);// the dot for tracking
		}
	}
}

class PCanvas extends Canvas {

	BufferedImage bi;

	public void newPicture(BufferedImage _bi) {
		bi = _bi;
	}

	public void paint(Graphics g) {
		if (bi != null) {
			g.drawImage(bi, 0, 0, this);
		}
	}
}

class pThread extends Thread {

	SimpleVBP parent;

	public pThread(SimpleVBP _parent) {
		parent = _parent;
	}

	public void run() {
		while (true) {
			parent.LookAtAFrame();
			// parent.p.newPicture(parent.myImage);
			parent.repaint();
			try {
				Thread.sleep(33);
			}
			catch (InterruptedException e) {
			}
		}
	}
}
