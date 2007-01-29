package vxp;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayList;

/**
 * @author Dan O'Sullivan
 * @author Eirikur S. Hrafnsson, Idega software. 
 * 
 * The original code has been modified a lot by Eirikur S. Hrafnsson and the support for JMF removed.
 * 
 * Abstracts the particulars of getting video. Video is usually returned to you
 * in a big long linear array when you would like a two dimenensionaly array for
 * x and y. Furthermore the individual colors are packed inside a single int. Of
 * course you also have the problems of different video systems delivering
 * things differently. Pixelsource and it's implementations try to give you
 * single simple interface for getting at the individual pictures of video.
 * 
 * 
 */
public abstract class PixelSource {

	/**
	 * This is the width of the video that you requested when you constructed
	 * this object
	 */
	public int kWidth;
	/**
	 * This is the width of the video after you have created the video. It may
	 * vary from your requested width
	 */
	public int vidWidth;
	/**
	 * This is the height of the video that you requested when you constructed
	 * this object
	 */
	public int kHeight;
	int[] newPixels;
	// int[] rgb = new int[4];//saves making this object every time you do get
	// pixel
	// int[] frgb = new int[4];//saves making this object every time you do get
	// pixel
	BufferedImage image;
	WritableRaster raster;
	// positions in a "pixel" array
	int redPosition = 1;
	int greenPosition = 2;
	int bluePosition = 3;
	int alphaPosition = 4;
	// 8 bits for each color and 8 for the transparency
	int redShift = 16;
	int greenShift = 8;
	int blueShift = 0;
	int alphaShift = 24;
	// don't really know what this is for, used in ImageWrangler
	public int blueMask = 255;
	public int greenMask = 65280;
	public int redMask = 0xff0000;
	public int alphaMask = 0xff000000;
	// background storage
	int background[];
	int bbackground[][];
	int normalizedBbackground[][];
	// the listeners
	ArrayList videoListeners = new ArrayList();

	/**
	 * Constructor class. Remember the eventual width may vary from what you
	 * asked for. Safer to use getVideoWidth() after making this object
	 * 
	 * @param pw
	 *            width of the video
	 * @param ph
	 *            height of the video
	 */
	public PixelSource(int pw, int ph) {
		kWidth = pw;
		kHeight = ph;
	}

	/**
	 * This returns the actual width of the video which may differ from the
	 * width you requested because some extra slop bytes may be added at the end
	 * of each line. Sometimes no bytes are added (on most pc's) but sometime 4
	 * bytes are added (most MACs).
	 */
	public int getVideoWidth() {
		return vidWidth;
	}

	/** This returns the height of the video. */
	public int getVideoHeight() {
		return kHeight;
	}

	/**
	 * This needs to be done after configuring the video where you learn about
	 * the widths and heights available. You usually don't have to worry about
	 * this.
	 */
	public void setNativeArrays() {
		newPixels = new int[vidWidth * kHeight];
		background = new int[vidWidth * kHeight];
		bbackground = new int[vidWidth * kHeight][4];
		normalizedBbackground = new int[vidWidth * kHeight][4];
	}

	/**
	 * 
	 * @return an empty array with the same size as the video, new int[vidWidth *
	 *         kHeight];
	 */
	public int[] getEmptyPixelArray() {
		return new int[vidWidth * kHeight];
	}

	/**
	 * This needs to be done after configuring the video where you learn about
	 * the format of the pixels. You usually don't have to worry about this.
	 */
	public void setImageType() {
//		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//		GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
//		image = gc.createCompatibleImage(vidWidth, kHeight);
		image = new BufferedImage(vidWidth, kHeight, BufferedImage.TYPE_INT_ARGB);
		raster = image.getRaster();
	}

	/**
	 * You can specify which type of image to supply with the getImage command
	 * 
	 * @param type
	 *            Usually this is is either BufferedImage.TYPE_INT_ARGB or
	 *            BufferedImage.TYPE_INT_RGB.
	 */
	public void setImageType(int type) {
//		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//		GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
//		image = gc.createCompatibleImage(vidWidth, kHeight);
		image = new BufferedImage(vidWidth, kHeight, type);
		raster = image.getRaster();
	}

	/**
	 * Gives you the current video image in the form of a bufferedImage.
	 */
	public BufferedImage getImage() {
		raster.setDataElements(0, 0, vidWidth, kHeight, newPixels);
		image.setData(raster);
		//set RGB is much slower here than the raster method and will cause problems if you
		//are replacing the data outside a paint(g) method. The raster method is more reliable in that case
//		image.setRGB(0, 0, vidWidth, kHeight, newPixels, 0, vidWidth);
		return image;
	}

	/**
	 * Gives you the video image in the form of a NEW bufferedImage (probably a little slower than getImage)
	 * It does not affect the image gotten from getImage so they can be called at the same time.
	 * You could also just get the array copy and create a BufferedImage with GraphicsConfiguration.createCompatibleImage<br>
	 * and then use image.setRGB or get the raster and write to that like this method does but without the need to create a new BufferedImage all the time 
	 */
	public BufferedImage getImage(int[] pixelsARGB) {
		BufferedImage newImage = new BufferedImage(vidWidth, kHeight, BufferedImage.TYPE_INT_ARGB);
		WritableRaster newRaster = newImage.getRaster();
		newRaster.setDataElements(0, 0, vidWidth, kHeight, pixelsARGB);
		newImage.setData(newRaster);
		return newImage;
	}

	/**
	 * Gives you the video image in the form of a NEW bufferedImage (probably a little slower than getImage)
	 * It does not affect the image gotten from getImage so they can be called at the same time.
	 * WARNING creating a BufferedImage of the type TYPE_4BYTE_ABGR may be slower the int type, not sure how much!
	 */
	public BufferedImage getImage(byte[] pixelsABGR) {
		BufferedImage newImage = new BufferedImage(vidWidth, kHeight, BufferedImage.TYPE_4BYTE_ABGR);
		WritableRaster newRaster = newImage.getRaster();
		newRaster.setDataElements(0, 0, vidWidth, kHeight, pixelsABGR);
		newImage.setData(newRaster);
		return newImage;
	}
	
	/**
	 * Gives you the video image in the form of a NEW bufferedImage of type RGB (probably a little slower than getImage)
	 * from an array of RGB ints (NO ALPHA)
	 * It does not affect the image gotten from getImage so they can be called at the same time.
	 */
	public BufferedImage getImageRGB(int[] pixelsRGB) {
		BufferedImage newImage = new BufferedImage(vidWidth, kHeight, BufferedImage.TYPE_INT_RGB);
		WritableRaster newRaster = newImage.getRaster();
		newRaster.setDataElements(0, 0, vidWidth, kHeight, pixelsRGB);
		newImage.setData(newRaster);
		return newImage;
	}
	
	
	/**
	 * Puts a the video image into an image supplied by you.
	 * 
	 * @param _bi
	 *            the BufferedImage into which you would like the video image
	 *            placed.
	 */
	public void getImage(BufferedImage _bi) {
		_bi.getRaster().setDataElements(0, 0, vidWidth, kHeight, newPixels);
		_bi.setData(_bi.getRaster());
	}

	/*
	 * should be able to dynamically reonctruct and image of smaller dimensions
	 * public BufferedImage getImage() {
	 * 
	 * raster.setDataElements(0, 0, vidWidth, kHeight, newPixels);
	 * image.setData(raster); return image; }
	 */
	/**
	 * Converts a Buffered Image into an array. This is useful for using built
	 * in operators for imaging but then being able to through it as an array.
	 * 
	 * @param _bi
	 * @param _x
	 * @param _y
	 * @param _w
	 * @param _h
	 * @param _iArray
	 */
	public void getArray(BufferedImage _bi, int _x, int _y, int _w, int _h, int[] _iArray) {
		Raster r = _bi.getRaster();
		r.getPixels(_x, _y, _w, _h, _iArray);
	}

	public String getDeviceName() {
		return "UnKnown";
	}

	/** This gives the separate colors for a given location */
	/**
	 * @param x
	 * @param y
	 * @return integer array with the offset in 0, the r in 1, the g in 2 and
	 *         the b in 3
	 */
	public int[] getPixel(int x, int y) {
		int offset = getPixelOffset(x, y);
		int[] rgb = new int[4];
		rgb[redPosition] = ((newPixels[offset] >>> 16) & 0xff);
		rgb[greenPosition] = ((newPixels[offset] >>> 8) & 0xff);
		rgb[bluePosition] = ((newPixels[offset]) & 0xff);
		rgb[0] = offset;
		return rgb;
	}

	// TODO eiki understand difference between old getPixel and new
	// OLD VERSION
	// public int[] getPixel(int inArray[], int x, int y)
	// {
	// int offset = y * vidWidth + x;
	// int rgb[] = new int[4];
	// rgb[0] = (inArray[offset] & redMask) >>> redShift;
	// rgb[1] = (inArray[offset] & greenMask) >>> greenShift;
	// rgb[2] = (inArray[offset] & blueMask) >>> blueShift;
	// rgb[3] = offset;
	// return rgb;
	// }
	public int[] getPixelBrightness(int x, int y) {
		int offset = getPixelOffset(x, y);
		int[] bw = new int[2];
		bw[0] = offset;
		bw[1] = (newPixels[offset] & 0xff);
		return bw;
	}

	/**
	 * This will make the pixelsource notify you when a new frame is ready. You
	 * have to have made your other class a "VideoListener" by implementing
	 * "VideoListener." You also have to have put in a "newFrame()" function in
	 * the other class.
	 * 
	 * @param _videoListener
	 *            This should be your other class. Try using "this".
	 */
	public void addVideoListener(VideoListener _videoListener) {
		videoListeners.add(_videoListener);
	}

	public void tellVideoListeners() {
		if (videoListeners.size() > 0) {
			for (int i = 0; i < videoListeners.size(); i++) {
				((VideoListener) videoListeners.get(i)).newFrame();
			}
		}
	}

	/**
	 * Separates Red Green and Blue out of an array where where all three colors
	 * are packed into a single int. Offset comes back first, then R, then G
	 * then B.
	 * 
	 * @param Array
	 *            of pixels within which you would like the RGB of a position.
	 * @param x
	 * @param y
	 * @return int array with offset in the 0 position Red in 1, Green in 2,
	 *         Blue in 3
	 */
	public int[] getPixel(int[] inArray, int x, int y) {
		int offset = getPixelOffset(x, y);
		int[] rgb = new int[4];
		rgb[redPosition] = ((inArray[offset] >>> 16) & 0xff);
		rgb[greenPosition] = ((inArray[offset] >>> 8) & 0xff);
		rgb[bluePosition] = ((inArray[offset]) & 0xff);
		rgb[0] = offset;
		return rgb;
	}

	/**
	 * @param x
	 * @param y
	 * @return The offset of the pixel in the int array.
	 */
	public int getPixelOffset(int x, int y) {
		int offset = y * vidWidth + x;
		return offset;
	}

	/**
	 * Separates Red Green and Blue out of an array where where all three colors
	 * are packed into a single int. Offset comes back first, then R, then G
	 * then B.
	 * 
	 * @param Array
	 *            of pixels within which you would like the RGB of a position.
	 * @param offset
	 *            -place in the array that you want the separation
	 * @return int array with offset in the 0 position Red in 1, Green in 2,
	 *         Blue in 3
	 */
	public int[] getPixel(int[] inArray, int _offset) {
		int[] rgb = new int[4];
		rgb[redPosition] = ((inArray[_offset] >>> 16) & 0xff);
		rgb[greenPosition] = ((inArray[_offset] >>> 8) & 0xff);
		rgb[bluePosition] = ((inArray[_offset]) & 0xff);
		rgb[0] = _offset;
		return rgb;
	}

	/**
	 * Gives you all the pixels as a linear int array with the r,g and b all
	 * packed into integers.
	 * 
	 * @return
	 */
	public int[] getPixelArray() {
		return newPixels;
	}

	/**
	 * Added by Eiki
	 * 
	 * @return a copy of the current pixel array
	 */
	public int[] getPixelArrayCopy() {
		int[] pixelCopy = new int[newPixels.length];
		for (int i = 0; i < newPixels.length; i++) {
			pixelCopy[i] = newPixels[i];
		}
		return pixelCopy;
	}

	/**
	 * Sets the color of a pixel in the current frame of video at a given
	 * location.
	 * 
	 * @param x
	 * @param y
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 */
	public void setPixel(int x, int y, int red, int green, int blue, int alpha) {
		int offset = getPixelOffset(x, y);
		newPixels[offset] = (red << redShift) + (green << greenShift) + (blue << blueShift) + (alpha << alphaShift); // this
		// makes
		// the
		// alpha
		// transparent
	}

	/**
	 * Sets the color of a given pixel within an array of pixels.
	 * 
	 * @param inputArray
	 * @param x
	 * @param y
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 */
	public void setPixel(int[] inputArray, int x, int y, int red, int green, int blue, int alpha) {
		int offset = getPixelOffset(x, y);
		inputArray[offset] = (red << redShift) + (green << greenShift) + (blue << blueShift) + (alpha << alphaShift); // this
		// makes
		// the
		// alpha
		// transparent
		// + (red << redShift)+ (green << greenShift)+ (blue << blueShift) +
	}

	/**
	 * Sets the color of a given pixel within the current frame but uses a
	 * precomputed offset instead of the x and y. Often set pixel is used after
	 * getPixel where you get the offset back so there it is faster if you this
	 * function does not have to computer it again.
	 * 
	 * @param _offset
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 */
	public void setPixel(int _offset, int red, int green, int blue, int alpha) {
		newPixels[_offset] = (red << redShift) + (green << greenShift) + (blue << blueShift) + (alpha << alphaShift); // this
		// makes
		// the
		// alpha
		// transparent
		// + (red << redShift)+ (green << greenShift)+ (blue << blueShift) +
	}

	/**
	 * Sets the alpha (opacity) for a given pixel in the given pixel array
	 * 
	 * @param inputArray
	 * @param x
	 * @param y
	 * @param alpha
	 */
	public void setPixel(int inputArray[], int x, int y, int alpha) {
		int offset = getPixelOffset(x, y);
		inputArray[offset] = inputArray[offset] + (alpha << alphaShift);
	}

	/**
	 * Sets the alpha (opacity) for a given pixel
	 * 
	 * @param x
	 * @param y
	 * @param alpha
	 */
	public void setPixel(int x, int y, int alpha) {
		int offset = getPixelOffset(x, y);
		newPixels[offset] = newPixels[offset] + (alpha << alphaShift);
	}

	/**
	 * Sets the color of a given pixel within a supplied array but uses a
	 * precomputed offset instead of the x and y. Often set pixel is used after
	 * getPixel where you get the offset back so there it is faster if you this
	 * function does not have to computer it again.
	 * 
	 * @param inputArray
	 * @param _offset
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 */
	public void setPixel(int[] inputArray, int _offset, int red, int green, int blue, int alpha) {
		inputArray[_offset] = (red << redShift) + (green << greenShift) + (blue << blueShift) + (alpha << alphaShift); // this
		// makes
		// the
		// alpha
		// transparent
		// + (red << redShift)+ (green << greenShift)+ (blue << blueShift) +
	}

	/**
	 * Gives you a pop up dialog box for the video digitizer driver.
	 * 
	 */
	public void videoSettings() {
	}

	/**
	 * Tells the digitizer to freshen up the pixels. You don't have to call this
	 * if you set the frame rate in the constructor.
	 * 
	 */
	public void idleIt() {
	}

	/**
	 * Brings a new set of pixels into the system to be used by getPixel,
	 * setPixel, getImage etc...
	 * 
	 * @return
	 */
	public boolean grabFrame() {
		return false;
	}
	
	/**
	 * Brings a new set of pixels into a new array to be used by getPixel(array,..),
	 * setPixel(array,...), getImage(array,..) etc.<br>
	 * This does not affect the internal array of PixelSource!
	 * 
	 * @return a new array from the current frame, you must use pixel methods that have an array property to manipulate it.
	 */
	public int[] grabFrameToArray() {
		return null;
	}

	/**
	 * Be sure to call this when you close or destroy your main window so a
	 * connection to your camera is not left hanging .
	 */
	public void killSession() {
		System.out.println("kill pixel source");
	}

	public String setDevice(int whichSource) {
		return "bla";
	}

	public String setDevice(String whichSource) {
		return "bla";
	}

	/** This returns the height of the video. */
	public void setInput(int _bla) {
	}

	public String[] getDeviceList() {
		String[] fakeDlist = { "unknown" };
		return fakeDlist;
	}

	/**
	 * This is the amount you would would have to shift a byte packed into an
	 * int to give you an accurate value. You only need this if you start
	 * accessing the int array yourself and unpacking the ARGB out of the ints.
	 * They are returned in the order of argb. Using getPixel and setPixel
	 * allows you to avoid all of this.
	 */
	public int[] getColorOrder() {
		int[] orders = { alphaPosition, redPosition, greenPosition, bluePosition };
		return orders;
	}

	// STUFF ADDED FROM OLDER PIXELSOURCE CLASS, by Eiki, idega.
	public int[] getMasks() {
		int masks[] = { alphaMask, redMask, greenMask, blueMask };
		return masks;
	}

	public int[] getShifts() {
		int shifts[] = { alphaShift, redShift, greenShift, blueShift };
		return shifts;
	}

	public float[] getPixelHSB(int inArray[], int x, int y) {
		int offset = getPixelOffset(x, y);
		return Color.RGBtoHSB((inArray[offset] & redMask) >>> redShift, (inArray[offset] & greenMask) >>> greenShift,
				(inArray[offset] & blueMask) >>> blueShift, null);
	}

	public float[] getPixelHSB(int x, int y) {
		int offset = getPixelOffset(x, y);
		return Color.RGBtoHSB((newPixels[offset] & redMask) >>> redShift,
				(newPixels[offset] & greenMask) >>> greenShift, (newPixels[offset] & blueMask) >>> blueShift, null);
	}

	public int[] getBackGroundPixelSlow(int x, int y) {
		int offset = getPixelOffset(x, y);
		int rgb[] = new int[4];
		rgb[redPosition] = (background[offset] & redMask) >>> redShift;
		rgb[greenPosition] = (background[offset] & greenMask) >>> greenShift;
		rgb[bluePosition] = (background[offset] & blueMask) >>> blueShift;
		rgb[0] = offset;
		return rgb;
	}

	public float[] getBackGroundPixelHSBSlow(int x, int y) {
		int offset = getPixelOffset(x, y);
		return Color.RGBtoHSB((background[offset] & redMask) >>> redShift,
				(background[offset] & greenMask) >>> greenShift, (background[offset] & blueMask) >>> blueShift, null);
	}

	public int[] getBackGroundPixel(int x, int y) {
		return bbackground[y * vidWidth + x];
	}

	public void grabBackground() {
		getPixelArray(background);
		unpackBackground();
	}

	public int[][] getBackground() {
		return bbackground;
	}

	public int[][] getNormalizedBackground() {
		return normalizedBbackground;
	}

	public int[] getPackedBackground() {
		return background;
	}

	public void unpackBackground() {
		for (int k = 0; k < kHeight; k++) {
			for (int i = 0; i < kWidth; i++) {
				int offset = getPixelOffset(i, k);
				bbackground[offset][redPosition] = (background[offset] & redMask) >>> redShift;
				bbackground[offset][greenPosition] = (background[offset] & greenMask) >>> greenShift;
				bbackground[offset][bluePosition] = (background[offset] & blueMask) >>> blueShift;
				bbackground[offset][0] = offset;
			}
		}
	}

	public void unpackBackgroundNormalized() {
		for (int k = 0; k < kHeight; k++) {
			for (int i = 0; i < kWidth; i++) {
				int offset = getPixelOffset(i, k);
				int r = (background[offset] & redMask) >>> redShift;
				int g = (background[offset] & greenMask) >>> greenShift;
				int b = (background[offset] & blueMask) >>> blueShift;
				int total = r + g + b;
				int nr = (int) ((1000F * (float) r) / (float) total);
				int ng = (int) ((1000F * (float) g) / (float) total);
				int nb = (int) ((1000F * (float) b) / (float) total);
				normalizedBbackground[offset][redPosition] = nr;
				normalizedBbackground[offset][greenPosition] = ng;
				normalizedBbackground[offset][bluePosition] = nb;
				normalizedBbackground[offset][0] = offset;
			}
		}
	}

	public void getPixelArray(int arrayToChange[]) {
		// implemented in subclasses
	}
}