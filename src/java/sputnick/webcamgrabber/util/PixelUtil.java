/*
 * $Id: PixelUtil.java,v 1.1 2006/05/30 13:44:08 eiki Exp $ Created on May 29, 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package sputnick.webcamgrabber.util;

import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import com.idega.util.FileUtil;

/**
 * Loads an Image from file, stores pixels as ARGB int array, and RGBA
 * ByteBuffer. An alternate constructor creates a GLImage from a ByteBuffer
 * containing pixel data.
 * <P>
 * Static functions are included to load, flip and convert pixel arrays.
 * <P>
 * napier at potatoland dot org
 */
public class PixelUtil {

	public static final int SIZE_BYTE = 1;
	private static int buffSize = 4096;
	int h = 0;
	int w = 0;
	ByteBuffer pixelBuffer = null; // to store bytes in GL_RGBA format
	int[] pixels = null;
	Image image = null;

	public PixelUtil() {
	}

	/**
	 * Load pixels from an image file.
	 * 
	 * @param imgName
	 */
	public PixelUtil(String imgName) {
		loadImage(imgName);
	}

	/**
	 * Store pixels passed in a ByteBuffer.
	 * 
	 * @param pixels
	 * @param w
	 * @param h
	 */
	public PixelUtil(ByteBuffer pixels, int w, int h) {
		this.pixelBuffer = pixels;
		this.pixels = null;
		this.image = null; // image is not loaded from file
		this.h = h;
		this.w = w;
	}

	public static void main(String[] args) {
		// test
		byte[] bites = { (byte) 100, (byte) 150, (byte) 200, (byte) 255 };
		int sum = makeIntFromByte4(bites);
		System.out.println("sum of byte array : " + sum);
		// int[] intBites = {-16775424,-16775680,-16776448,-16777216}
		byte[] fourBytes = makeByte4FromInt(-16775424);
		sum = makeIntFromByte4(fourBytes);
		System.out.println("Int to byte to int (-16775424) : " + sum);
	}

	/**
	 * Flip the image pixels vertically
	 */
	public void flipPixels() {
		pixels = PixelUtil.flipPixels(pixels, w, h);
	}

	/**
	 * Scale the image and regenerate pixel array
	 */
	/*
	 * public void resize(float scaleW, float scaleH) { if (image != null) {
	 * image = image.getScaledInstance( //(int)((float)w*scaleW),
	 * //(int)((float)h*scaleH), 600, -1, Image.SCALE_SMOOTH); if (image !=
	 * null) { w = image.getWidth(null); h = image.getHeight(null); pixels =
	 * getImagePixels(); // pixels in default Java ARGB format pixelBuffer =
	 * convertImagePixels(pixels, w, h, true); // convert to RGBA bytes } } }
	 */
	/**
	 * Load an image file and hold its width/height.
	 * 
	 * @param imgName
	 */
	public void loadImage(String imgName) {
		Image tmpi = loadImageFromFile(imgName);
		if (tmpi != null) {
			w = tmpi.getWidth(null);
			h = tmpi.getHeight(null);
			image = tmpi;
			pixels = getImagePixels(); // pixels in default Java ARGB format
			pixelBuffer = convertImagePixels(pixels, w, h, true); // convert
			// to RGBA
			// bytes
			System.out.println("GLImage: loaded " + imgName + ", width=" + w + " height=" + h);
		}
		else {
			System.out.println("GLImage: FAILED TO LOAD IMAGE " + imgName);
			image = null;
			pixels = null;
			pixelBuffer = null;
			h = w = 0;
		}
	}

	/**
	 * Return the image pixels in default Java int ARGB format.
	 * 
	 * @return
	 */
	public int[] getImagePixels() {
		if (pixels == null && image != null) {
			pixels = new int[w * h];
			PixelGrabber pg = new PixelGrabber(image, 0, 0, w, h, pixels, 0, w);
			try {
				pg.grabPixels();
			}
			catch (Exception e) {
				System.out.println("Pixel Grabbing interrupted!");
				return null;
			}
		}
		return pixels;
	}

	/**
	 * return int array containing pixels in ARGB format (default Java byte
	 * order).
	 */
	public int[] getPixelsARGB() {
		return pixels;
	}

	/**
	 * return ByteBuffer containing pixels in RGBA format (commmonly used in
	 * OpenGL).
	 */
	public ByteBuffer getPixelsRGBA() {
		return pixelBuffer;
	}

	// ========================================================================
	//
	// Utility functions to prepare pixels for use in OpenGL
	//
	// ========================================================================
	/**
	 * Flip an array of pixels vertically
	 * 
	 * @param imgPixels
	 * @param imgw
	 * @param imgh
	 * @return int[]
	 */
	public static int[] flipPixels(int[] imgPixels, int imgw, int imgh) {
		int[] flippedPixels = null;
		if (imgPixels != null) {
			flippedPixels = new int[imgw * imgh];
			for (int y = 0; y < imgh; y++) {
				for (int x = 0; x < imgw; x++) {
					flippedPixels[((imgh - y - 1) * imgw) + x] = imgPixels[(y * imgw) + x];
				}
			}
		}
		return flippedPixels;
	}

	/**
	 * Convert ARGB pixels to a ByteBuffer containing RGBA pixels.<BR>
	 * Can be drawn in ORTHO mode using:<BR>
	 * GL.glDrawPixels(imgW, imgH, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, byteBuffer);
	 * <BR>
	 * If flipVertically is true, pixels will be flipped vertically (for OpenGL
	 * coord system).
	 * 
	 * @param imgFilename
	 * @return ByteBuffer
	 */
	public static ByteBuffer convertImagePixels(int[] jpixels, int imgw, int imgh, boolean flipVertically) {
		byte[] bytes; // will hold pixels as RGBA bytes
		if (flipVertically) {
			jpixels = flipPixels(jpixels, imgw, imgh); // flip Y axis
		}
		bytes = convertARGBtoRGBA(jpixels);
		return allocBytes(bytes); // convert to ByteBuffer and return
	}

	/**
	 * Convert pixels from java default ARGB int format to byte array in RGBA
	 * format.
	 * 
	 * @param jpixels
	 * @return
	 */
	public static byte[] convertARGBtoRGBA(int[] jpixels) {
		byte[] bytes = new byte[jpixels.length * 4]; // will hold pixels as
		// RGBA bytes
		int p;
		byte r, g, b, a;
		int j = 0;
		for (int i = 0; i < jpixels.length; i++) {
			// int outPixel = 0x00000000; // AARRGGBB
			p = jpixels[i];
			a = (byte) (p >> 24); // get pixel bytes in ARGB order
			r = (byte) (p >> 16);
			g = (byte) (p >> 8);
			b = (byte) (p >> 0);
			bytes[j + 0] = r; // fill in bytes in RGBA order
			bytes[j + 1] = g;
			bytes[j + 2] = b;
			bytes[j + 3] = a;
			j += 4;
		}
		return bytes;
	}

	/**
	 * Convert pixels from java default ARGB int format to 4byte array in ABGR
	 * format.
	 * 
	 * @param jpixels
	 * @return
	 */
	public static byte[] convertARGBto4ByteABGR(int[] aRGBPixels) {
		// TODO make faster by not using a new array
		byte[] bytes = new byte[aRGBPixels.length * 4]; // will hold pixels as
		// ABGR bytes
		int j = 0;
		byte[] fourBytes;
		for (int i = 0; i < aRGBPixels.length; i++) {
			// int outPixel = 0x00000000; // AARRGGBB
			fourBytes = makeByte4FromInt((int) aRGBPixels[i]);
			bytes[j + 0] = fourBytes[1];
			bytes[j + 1] = fourBytes[2];
			bytes[j + 2] = fourBytes[3];
			bytes[j + 3] = fourBytes[0]; // fill in bytes in ABGR order
			j += 4;
		}
		return bytes;
	}

	/**
	 * Converts a byte array of pixels in AGBR format to int array of ARGB
	 * format
	 * 
	 * @param aGBRPixels
	 * @return
	 */
	public static int[] convert4ByteABGRToARGB(byte[] aBGRPixels) {
		// TODO make faster by not using a new array
		int length = aBGRPixels.length / 4;
		int[] aRGB = new int[length];
		int j = 0;
		for (int i = 0; i < length; i++) {
			aRGB[i] = makeIntFromByte4(new byte[] { aBGRPixels[j + 3], aBGRPixels[j], aBGRPixels[j + 1],
					aBGRPixels[j + 2] });
			j += 4;
		}
		return aRGB;
	}

	// ========================================================================
	// Utility functions to load file into byte array
	// and create Image from bytes.
	// ========================================================================
	/**
	 * Same function as in GLApp.java. Allocates a ByteBuffer to hold the given
	 * array of bytes.
	 * 
	 * @param bytearray
	 * @return ByteBuffer containing the contents of the byte array
	 */
	public static ByteBuffer allocBytes(byte[] bytearray) {
		ByteBuffer bb = ByteBuffer.allocateDirect(bytearray.length * SIZE_BYTE).order(ByteOrder.nativeOrder());
		bb.put(bytearray).flip();
		return bb;
	}

	/**
	 * Load an image from file. Avoids the flaky MediaTracker/ImageObserver
	 * headache. Assumes that the file can be loaded quickly from the local
	 * filesystem, so does not need to wait in a thread. If it can't find the
	 * file in the filesystem, will try loading from jar file. If not found will
	 * return null.
	 * <P>
	 * 
	 * @param imgName
	 */
	public static Image loadImageFromFile(String imgName) {
		byte[] imageBytes = getBytesFromFile(imgName);
		Image tmpi = null;
		int numTries = 20;
		if (imageBytes == null) {
			// couldn't read image from file: try JAR file
			// URL url = getClass().getResource(filename); // for non-static
			// class
			URL url = PixelUtil.class.getResource(imgName);
			if (url != null) {
				// found image in jar: load it
				tmpi = Toolkit.getDefaultToolkit().createImage(url);
				// Wait up to two seconds to load image
				int wait = 200;
				while (tmpi.getHeight(null) < 0 && wait > 0) {
					try {
						Thread.sleep(10);
					}
					catch (Exception e) {
					}
				}
			}
		}
		else {
			tmpi = Toolkit.getDefaultToolkit().createImage(imageBytes, 0, imageBytes.length);
			while (tmpi.getWidth(null) < 0 && numTries-- > 0) {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
			while (tmpi.getHeight(null) < 0 && numTries-- > 0) {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		return tmpi;
	}

	public static Image loadImageFromFile_ORIG(String imgName) {
		byte[] imageBytes = getBytesFromFile(imgName);
		Image tmpi = null;
		int numTries = 20;
		if (imageBytes != null) {
			tmpi = Toolkit.getDefaultToolkit().createImage(imageBytes, 0, imageBytes.length);
			while (tmpi.getWidth(null) < 0 && numTries-- > 0) {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
			while (tmpi.getHeight(null) < 0 && numTries-- > 0) {
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
					System.out.println(e);
				}
			}
		}
		return tmpi;
	}

	/**
	 * Given name of file, return entire file as a byte array.
	 * 
	 * @param filename
	 * @return
	 */
	public static byte[] getBytesFromFile(String filename) {
		File f = new File(filename);
		byte[] bytes = null;
		if (f.exists()) {
			try {
				bytes = getBytesFromFile(f);
			}
			catch (Exception e) {
				System.out.println("getBytesFromFile() exception: " + e);
			}
		}
		return bytes;
	}

	/**
	 * Given File object, returns the contents of the file as a byte array.
	 */
	public static byte[] getBytesFromFile(File file) throws IOException {
		byte[] bytes = null;
		if (file != null) {
			InputStream is = new FileInputStream(file);
			long length = file.length();
			// Can't create an array using a long type.
			// Before converting to an int type, check
			// to ensure that file is not larger than Integer.MAX_VALUE.
			if (length > Integer.MAX_VALUE) {
				System.out.println("getBytesFromFile() error: File " + file.getName() + " is too large");
			}
			else {
				// Create the byte array to hold the data
				bytes = new byte[(int) length];
				int offset = 0;
				int numRead = 0;
				// Read in the bytes
				while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
					offset += numRead;
				}
				// Ensure all the bytes have been read in
				if (offset < bytes.length) {
					throw new IOException("getBytesFromFile() error: Could not completely read file " + file.getName());
				}
			}
			// Close the input stream and return bytes
			is.close();
		}
		return bytes;
	}

	// ========================================================================
	// Compression for byte and int array (ARGB)
	// ========================================================================
	/**
	 * converts an int[] ARGB array to a compressed 4 byte ABGR byte[]
	 */
	public static byte[] compress(int[] pixelIntARGBArray) {
		byte[] pixels = PixelUtil.convertARGBto4ByteABGR(pixelIntARGBArray);
		return PixelUtil.compress(pixels);
	}

	public static byte[] compress(byte[] pixelByteArray) {
		// protected static Deflater compressor = new Deflater();
		Deflater compressor = new Deflater();
		compressor.setLevel(Deflater.BEST_COMPRESSION);
		compressor.setStrategy(Deflater.FILTERED);
		// Give the compressor the data to compress
		compressor.setInput(pixelByteArray);
		compressor.finish();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(pixelByteArray.length);
		// Compress the data
		byte[] buf = new byte[buffSize];
		while (!compressor.finished()) {
			int count = compressor.deflate(buf);
			bos.write(buf, 0, count);
		}
		try {
			bos.close();
		}
		catch (IOException e) {
		}
		// compressor.end();
		compressor.reset();
		// Get the compressed data
		byte[] compressedData = bos.toByteArray();
		return compressedData;
	}

	public static void compressToFile(byte[] pixelByteArray, String fileName, String path) {
		// protected static Deflater compressor = new Deflater();
		File file;
		try {
			file = FileUtil.getFileAndCreateIfNotExists(path, fileName);
			FileOutputStream fileOut = new FileOutputStream(file);
			Deflater compressor = new Deflater();
			compressor.setLevel(Deflater.BEST_COMPRESSION);
			compressor.setStrategy(Deflater.DEFAULT_STRATEGY);
			// Give the compressor the data to compress
			compressor.setInput(pixelByteArray);
			compressor.finish();
			// Compress the data
			byte[] buf = new byte[buffSize];
			while (!compressor.finished()) {
				int count = compressor.deflate(buf);
				fileOut.write(buf, 0, count);
			}
			fileOut.close();
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public static byte[] uncompress(byte[] in) {
		Inflater decompressor = new Inflater();
		if (in.length > 0) {
			decompressor.setInput(in);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(in.length);
			// Decompress the data
			byte[] buf = new byte[buffSize];
			while (!decompressor.finished()) {
				try {
					int count = decompressor.inflate(buf);
					bos.write(buf, 0, count);
				}
				catch (DataFormatException e) {
				}
			}
			try {
				bos.close();
			}
			catch (IOException e) {
			}
			decompressor.reset();
			// Get the decompressed data
			byte[] decompressedData = bos.toByteArray();
			return decompressedData;
		}
		else {
			return in;
		}
	}

	/**
	 * using gzip
	 * 
	 * @param byteIn
	 * @return
	 */
	public static byte[] compressGzip(byte[] byteIn) {
		long t1 = System.currentTimeMillis();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteArrayInputStream bais = new ByteArrayInputStream(byteIn);
		// BufferedOutputStream
		try {
			GZIPOutputStream gzos = new GZIPOutputStream(baos);
			// Create a buffered input stream out of the file we are
			// trying to add into the gzip archive.
			BufferedInputStream in = new BufferedInputStream(bais);
			// Create a buffer for reading raw bytes.
			byte[] buf = new byte[buffSize];
			// The len variable will keep track of how much we
			// are able to actually read each time we try.
			int len;
			// Read from the file and write to the gzip archive.
			while ((len = in.read(buf)) >= 0) {
				gzos.write(buf, 0, len);
			}
			in.close();
			gzos.close();
			long t2 = System.currentTimeMillis();
			System.out.println(t2 - t1);
		}
		catch (IOException io) {
		}
		return baos.toByteArray();
	}

	public static void compressGzipToFile(byte[] byteIn, String fileName, String path) {
		File file;
		try {
			long t1 = System.currentTimeMillis();
			file = FileUtil.getFileAndCreateIfNotExists(path, fileName);
			FileOutputStream fileOut = new FileOutputStream(file);
			ByteArrayInputStream bais = new ByteArrayInputStream(byteIn);
			// BufferedOutputStream
			GZIPOutputStream gzos = new GZIPOutputStream(fileOut);
			// Create a buffered input stream out of the file we are
			// trying to add into the gzip archive.
			BufferedInputStream in = new BufferedInputStream(bais);
			// Create a buffer for reading raw bytes.
			byte[] buf = new byte[buffSize];
			// The len variable will keep track of how much we
			// are able to actually read each time we try.
			int len;
			// Read from the file and write to the gzip archive.
			while ((len = in.read(buf)) >= 0) {
				gzos.write(buf, 0, len);
			}
			in.close();
			gzos.close();
			long t2 = System.currentTimeMillis();
			System.out.println(t2 - t1);
		}
		catch (IOException io) {
			io.printStackTrace();
		}
	}

	/**
	 * Creates a BufferedImage using
	 * GraphicsEnvironment...createCompatibleImage(...)<br>
	 * The BufferedImage returned is colormodel compatible with the display, and
	 * will be managed (cachable in vram)
	 * 
	 * @param width
	 * @param height
	 * @return A BufferedImage that is colormodel compatible with the display,
	 *         and will be managed (cachable in vram)
	 */
	public static BufferedImage createImage(int width, int height) {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(
				width, height, Transparency.BITMASK);
	}

	public static final int makeIntFromByte4(byte[] b) {
		return (b[0] & 0xff) << 24 | (b[1] & 0xff) << 16 | (b[2] & 0xff) << 8 | (b[3] & 0xff);
	}

	public static final byte[] makeByte4FromInt(int i) {
		return new byte[] { (byte) (i >> 24), (byte) (i >> 16), (byte) (i >> 8), (byte) i };
	}
}