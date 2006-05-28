// import java.awt.image.BufferedImage;
package sputnick.webcamgrabber.util;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.DataBuffer;
import java.awt.image.Kernel;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImageWrangler {

	int kWidth;
	int kHeight;
	BufferedImage rgbImage;
	BufferedImage alphaImage;
	WritableRaster rgbRaster;
	WritableRaster argbRaster;
	/** A ConvolveOp object for blurring. */
	public ConvolveOp blur;
	/** A ConvolveOp object for finding edges. */
	public ConvolveOp edge;

	/**
	 * This has a bunch of utilities for going between images and arrays and for
	 * taking advantage of native convolveOps for edge detection and blur..
	 */
	public ImageWrangler(int _kWidth, int _kHeight, int[] masks) {
		kWidth = _kWidth;
		kHeight = _kHeight;
		// blur matrix of 25
		int[] masker = { masks[1], masks[2], masks[3] };
		int[] aMasker = { masks[0], masks[1], masks[2], masks[3] };
		rgbImage = new BufferedImage(kWidth, kHeight, BufferedImage.TYPE_INT_RGB);
		alphaImage = new BufferedImage(kWidth, kHeight, BufferedImage.TYPE_INT_ARGB);
		rgbRaster = WritableRaster.createPackedRaster(DataBuffer.TYPE_INT, kWidth, kHeight, masker, null);
		argbRaster = WritableRaster.createPackedRaster(DataBuffer.TYPE_INT, kWidth, kHeight, aMasker, null);
		float[] edgeMatrix = { 0.0f, -1.0f, 0.0f, -1.0f, 4.0f, -1.0f, 0.0f, -1.0f, 0.0f };
		setEdge(edgeMatrix, 3);
		setBlur(5);
		// edge = new ConvolveOp(new Kernel(3,3, edgeMatrix ));
	}

	/**
	 * Set up the edge detection. This sets the kernel in case you know what
	 * that is. You never really have to set this. The default matrix is, <BR>{
	 * 0.0f, -1.0f, 0.0f, <BR>
	 * -1.0f, 4.0f, -1.0f, <BR>
	 * 0.0f, -1.0f, 0.0f }; <BR>
	 * always a classic. The size is 3 as in 3x3.
	 */
	public void setEdge(float[] matrix, int size) {
		edge = new ConvolveOp(new Kernel(size, size, matrix));
	}

	/**
	 * Sets the degree of blur. This takes an integer that describes how many
	 * pixels around each pixel to take into account when averaging. The default
	 * is 5 and bigger numbers will really slow things down.
	 */
	public void setBlur(int size) {
		float th = 1.0f / ((float) (size * size));
		float[] blurMatrix = new float[size * size];
		for (int i = 0; i < size * size; i++) {
			blurMatrix[i] = th;
		}
		blur = new ConvolveOp(new Kernel(size, size, blurMatrix));
	}

	public void blurArray(int[] pixis) {
		rgbRaster.setDataElements(0, 0, kWidth, kHeight, pixis);
		rgbImage.setData(rgbRaster);
		rgbImage = blur.filter(rgbImage, null);
		rgbRaster = (WritableRaster) rgbImage.getData();
		rgbRaster.getDataElements(0, 0, kWidth, kHeight, pixis);
	}

	/**
	 * You give me an array of pixels, I give you them back blurred in an image.
	 */
	public BufferedImage blurArrayToImage(int[] pixis) {
		rgbRaster.setDataElements(0, 0, kWidth, kHeight, pixis);
		rgbImage.setData(rgbRaster);
		rgbImage = blur.filter(rgbImage, null);
		return rgbImage;
	}

	/**
	 * You give me an array of pixels, I find the edges and give it back in an
	 * image
	 */
	public BufferedImage edge(int[] myArray) {
		rgbRaster.setDataElements(0, 0, kWidth, kHeight, myArray);
		rgbImage.setData(rgbRaster);
		rgbImage = edge.filter(rgbImage, null);
		return rgbImage;
	}

	/**
	 * You give me an array of pixels, and the image you want them returned in
	 * and I will blur them.
	 */
	public void imageFromArray(int[] pixis, BufferedImage outImage) {
		rgbRaster.setDataElements(0, 0, kWidth, kHeight, pixis);
		outImage.setData(rgbRaster);
	}

	/**
	 * You give me an array of pixels, I give you back and image.
	 */
	public BufferedImage imageFromArray(int[] pixis) {
		rgbRaster.setDataElements(0, 0, kWidth, kHeight, pixis);
		rgbImage.setData(rgbRaster);
		return rgbImage;
	}

	/**
	 * You give me an image with Alpha, I give you back an array.
	 */
	public void imageWithAlphaFromArray(int[] pixis, BufferedImage outImage) {
		argbRaster.setDataElements(0, 0, kWidth, kHeight, pixis);
		outImage.setData(argbRaster);
	}

	/**
	 * You give me an array, I give you back an image with Alpha.
	 */
	public BufferedImage imageWithAlphaFromArray(int[] pixis) {
		argbRaster.setDataElements(0, 0, kWidth, kHeight, pixis);
		alphaImage.setData(argbRaster);
		return alphaImage;
	}

	/**
	 * You give me an image and I will blur it and return it in your destination
	 * image
	 */
	public void blurImage(BufferedImage inImage, BufferedImage outImage) {
		outImage = blur.filter(inImage, null);
	}

	/**
	 * You give me an image and some pixels and I will transfer the image into
	 * the array.
	 */
	public void arrayFromImage(BufferedImage inImage, int[] outPixis) {
		rgbRaster = (WritableRaster) inImage.getData();
		rgbRaster.getDataElements(0, 0, kWidth, kHeight, outPixis);
	}

	public void arrayFromImageWithAlpha(BufferedImage inImage, int[] pixis) {
		argbRaster = (WritableRaster) inImage.getData();
		argbRaster.getDataElements(0, 0, kWidth, kHeight, pixis);
	}

	/** You give me an image and I will encode it and write it to a file. */
//	public void makePictureFile(BufferedImage bi, String pathname, String filename, float quality, int threshold) {
//		try {
//			File outputFile = new File(pathname + filename);
//			FileOutputStream fos = new FileOutputStream(outputFile);
//			BufferedOutputStream bufOut = new BufferedOutputStream(fos);
//			if (filename.toLowerCase().endsWith("gif")) {
//				int[][] gifPixels = getPixels(bi);
//				int iquality = (int) (256 * quality);
//				int palette[] = Quantize.quantizeImage(gifPixels, iquality);
//				// createImage(new MemoryImageSource(w , h, pix, 0, w))
//				bi = setImage(palette, gifPixels);
//				GIFEncoder en = new GIFEncoder(bi);
//				en.Write(bufOut);
//				System.out.println(" gif " + pathname + filename);
//			}
//			else {
//				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bufOut);
//				JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
//				param.setQuality(quality, false);
//				encoder.setJPEGEncodeParam(param);
//				encoder.encode(bi);
//				System.out.println(" jpeg " + pathname + filename);
//			}
//			bufOut.close();
//		}
//		catch (FileNotFoundException e) {
//			System.out.println("file not found" + pathname + filename);
//		}
//		catch (IOException e) {
//			System.out.println("io execption" + pathname + filename);
//		}
//		catch (AWTException e) {
//			System.out.println("awt exception" + pathname + filename);
//		}
//	} // end of lookat frame

	BufferedImage setImage(int palette[], int pixels[][]) {
		int w = pixels.length;
		int h = pixels[0].length;
		int pix[] = new int[w * h];
		// convert to RGB
		for (int x = w; x-- > 0;) {
			for (int y = h; y-- > 0;) {
				pix[y * w + x] = palette[pixels[x][y]];
			}
		}
		BufferedImage ri = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		WritableRaster rr = ri.getRaster();
		rr.setDataElements(0, 0, w, h, pix);
		ri.setData(rr);
		// BufferedImage bi2 = imageFromArray(pix);
		// createImage(new MemoryImageSource(w, h, pix, 0, w)
		return ri;
	}

	static int[][] getPixels(Image image) throws IOException {
		int w = image.getWidth(null);
		int h = image.getHeight(null);
		int pix[] = new int[w * h];
		PixelGrabber grabber = new PixelGrabber(image, 0, 0, w, h, pix, 0, w);
		try {
			if (grabber.grabPixels() != true) {
				throw new IOException("Grabber returned false: " + grabber.status());
			}
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		int pixels[][] = new int[w][h];
		for (int x = w; x-- > 0;) {
			for (int y = h; y-- > 0;) {
				pixels[x][y] = pix[y * w + x];
			}
		}
		return pixels;
	}
}