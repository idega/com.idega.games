package sputnick.webcamgrabber.computervision;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import sputnick.webcamgrabber.util.PixelUtil;
import vxp.QTLivePixelSource;
import vxp.VideoListener;
import com.idega.util.Timer;

/**
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 * 
 * A test to see if one can compare two frames using XOR to get only thos pixels
 * that have changed! To be used for sending video data over a network.
 */
public class XORTest extends Frame implements VideoListener, WindowListener {

	final int width = 320;
	final int height = 240;
	final int numberOfFramesToKeep = 1;
	int currentBackFrame = 0;
	int frameNumberToCompareTo = 0;
	int[][] lastFrames = new int[numberOfFramesToKeep][];// last x
	// frames;
	protected QTLivePixelSource ps;
	protected BufferedImage currentFrameImage;
	private int threshhold = 100;

	XORTest(String title) {
		super(title);
		setLocation(100, 150);
		setSize(width, height);
		setLayout(null);
		setVisible(true);
		show();
		toFront();
		ps = new QTLivePixelSource(width, height, 50);
		ps.addVideoListener(this);
	
		// add a listener for pressing a key , give it a method to call
		// (KeyPressed)
		this.addKeyListener(new KeyAdapter() {

			public void keyPressed(KeyEvent e) {
				KeyPressed(e);
			}
		});
		
		
		Graphics g = this.getGraphics();
		while (true) {
			update(g);
		}
	}

	public void newFrame() {
		// currentFrameImage = ps.getImage();
		processFrame();
	}

	protected void processFrame() {
		ps.grabFrame(); // grab a frame
		// always compare with the oldest frame
		int[] compareFrame = lastFrames[frameNumberToCompareTo];
		int[] currentFramePixels = ps.getPixelArrayCopy();
		int[] resultArray = ps.getEmptyPixelArray();

		int lastFramePixel;
		int currentFramePixel;
		int resultPixel;
		int alphaFull = 255 << 24;
		int red = 255 << 24 | 255 << 16;
		if (compareFrame != null) {
			for (int i = 0; i < currentFramePixels.length; i++) {
				// compare pixel to pixel from both frames
				currentFramePixel = currentFramePixels[i];
				lastFramePixel = compareFrame[i];
				resultPixel = alphaFull | ((currentFramePixel << 16) ^ (lastFramePixel << 16))
						| ((currentFramePixel << 8) ^ (lastFramePixel << 8))
						| ((currentFramePixel << 0) ^ (lastFramePixel << 0));
				if (resultPixel != alphaFull) {
					resultArray[i] = currentFramePixel;
				}
				else {
					// is the same use old pixel
					// resultArray[i] = lastFramePixel;
					// resultArray[i] = alphaFull | 255 << 16;
					resultArray[i] = red;
				}
			}
			currentFrameImage = ps.getImage(resultArray);
			// PixelUtil.encodePixelsToGif(currentFrameImage,"/Users/eiki/Desktop/temp/test.gif");
			currentFrameImage = PixelUtil.thresholdFilter(threshhold, currentFrameImage);
		
		}
		lastFrames[currentBackFrame] = currentFramePixels;
		currentBackFrame++;
		if (currentBackFrame >= (numberOfFramesToKeep - 1)) {
			currentBackFrame = 0;
		}
		// PixelUtil.compressToFile(PixelUtil.convertARGBto3ByteRGB(resultArray),"differencearray","/Users/eiki/Desktop/temp/");
	}

	public void update(Graphics g) {
		paint(g); // remove flicker
	}

	public void paint(Graphics g) { // this is where we paint
		if (currentFrameImage != null) {
			g.drawImage(currentFrameImage, 0, 0, this);// the background video
		}
	}

	public void windowClosing(WindowEvent e) {
		dispose();
	}

	public void windowClosed(WindowEvent e) {
		System.exit(0);
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	// Main entry point
	static public void main(String[] args) {
		XORTest preview = new XORTest("XOR test");
	}

	public boolean KeyPressed(KeyEvent e) { // pop up dialog
		// System.out.println ( "whichkey" + e.getKeyText(e.getKeyCode()));
		String whichKey = KeyEvent.getKeyText(e.getKeyCode());
		if (whichKey.equals("Up")) {
			++threshhold;
		}
		else if (whichKey.equals("Down")) {
			--threshhold;
		}
		
		System.out.println(threshhold);
		
		return true;
	}
}
