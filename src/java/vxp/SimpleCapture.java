package vxp;

/*
 * This class grabs raw pixels from any quicktime video source
 */
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import quicktime.QTException;
import quicktime.QTSession;
import quicktime.qd.PixMap;
import quicktime.qd.QDGraphics;
import quicktime.qd.QDRect;
import quicktime.std.StdQTConstants;
import quicktime.std.StdQTException;
import quicktime.std.image.CodecComponent;
import quicktime.std.image.DSequence;
import quicktime.std.image.ImageDescription;
import quicktime.std.image.Matrix;
import quicktime.std.image.QTImage;
import quicktime.std.movies.media.UserData;
import quicktime.std.sg.SGChannel;
import quicktime.std.sg.SGDataProc;
import quicktime.std.sg.SGVideoChannel;
import quicktime.std.sg.SequenceGrabber;
import quicktime.util.QTHandle;
import quicktime.util.QTPointerRef;
import quicktime.util.RawEncodedImage;

public class SimpleCapture implements SGDataProc {

	// Brings up a bunch of output, including a standard java window
	public final static boolean TESTING = true;
	protected int maxDataLength;
	protected int realDataLength;
	protected byte[] data; // The actual Data
	protected Dimension size;
	// Have we started the pipeline
	protected boolean started;
	// Thread for running this
	protected Thread bufferHandlerThread;
	// Thread for idleing the QT capture
	protected Thread qtIdleThread;
	// The framerate we are running at
	protected float frameRate = 15f;
	// QuickTime Sequence Grabber Objects
	SequenceGrabber sg;
	SGVideoChannel vc;
	QDRect cis;
	QDGraphics gw;
	// QuickTime DataProc Objects
	DSequence ds = null;
	final Matrix idMatrix = new Matrix();
	byte[] rawData;
	RawEncodedImage ri = null;
	// The QuickTime Codec
	// int thisCodec = StdQTConstants.kRawCodecType; // This changes all of the
	// pixel math.. fuck!
	int thisCodec = StdQTConstants.kComponentVideoCodecType;
	// Testing Window
	MyComp ret;
	int[] pixelData;
	BufferedImage image;

	public static void main(String args[]) {
		SimpleCapture sc = new SimpleCapture();
	}

	public SimpleCapture() {
		// The size of our capture
		// This should be a parameter
		size = new Dimension(320, 240);
		try {
			QTSession.open();
			// Set up the Sequence Grabber
			sg = new SequenceGrabber();
			vc = new SGVideoChannel(sg);
			cis = new QDRect(size.width, size.height);
			gw = new QDGraphics(cis);
			UserData ud = fileToSettings();
			if (ud != null) {
				System.out.println("got ud" + ud);
				vc.setSettings(ud);
				System.out.println(ud.toString());
				// mVideo.digitizerChanged();
			}
			ud = vc.getSettings();
			System.out.println(ud.toString());
			vc.settingsDialog();
			ud = vc.getSettings();
			System.out.println(ud.toString());
			settingsToFile(vc.getSettings().putIntoHandle().getBytes());
			// settingsToFile(mVideo.getSettings().putIntoHandle().getBytes())
			sg.setGWorld(gw, null);
			vc.setBounds(cis);
			vc.setUsage(StdQTConstants.seqGrabRecord); // New version below,
														// not
			// sure if good flags to
			// be set
			// vc.setUsage(StdQTConstants.seqGrabRecord |
			// StdQTConstants.seqGrabDataProcDoesOverlappingReads |
			// StdQTConstants.seqGrabPlayDuringRecord);
			vc.setFrameRate(frameRate); // Should be set to frameRate?
			vc.setCompressorType(thisCodec); // This was kComponentCodecType
												// or
			// something of the sort
			// Set up the buffer - Which size should I use?
			if (TESTING)
				System.out.println("QT Reported Max Size: "
						+ QTImage.getMaxCompressionSize(gw, gw.getBounds(), 0, StdQTConstants.codecLowQuality,
								thisCodec, CodecComponent.anyCodec));
			if (TESTING)
				System.out.println("Byte Size of GWorld: " + gw.getPixMap().getPixelData().getSize());
			// maxDataLength = QTImage.getMaxCompressionSize(gw, gw.getBounds(),
			// 0, StdQTConstants.codecLowQuality, thisCodec,
			// CodecComponent.anyCodec);
			maxDataLength = gw.getPixMap().getPixelData().getSize();
			realDataLength = size.height * size.width * 4;
			int extraRowLength = (maxDataLength - (size.width * size.height) * 4) / size.height;
			if (TESTING)
				System.out.println("Extra Row Length: " + extraRowLength + " Should be: " + (4 * 4));
			if (TESTING)
				System.out.println("Row Length: " + maxDataLength / size.height + " " + maxDataLength / size.height / 4);
			if (TESTING)
				System.out.println("CIS: " + cis.getWidth() + " " + cis.getHeight());
			// This probably isn't right..
			// rgbFormat = new RGBFormat(size, maxDataLength, Format.byteArray,
			// frameRate, 32, 2, 3, 4, 4, gw.getPixMap().getRowBytes(),
			// VideoFormat.FALSE, RGBFormat.BIG_ENDIAN);
			if (TESTING)
				System.out.println("Pixel Size: " + gw.getPixMap().getPixelSize());
			// rgbFormat = new RGBFormat(size, maxDataLength, Format.byteArray,
			// frameRate, 32, 3, 2, 1, 4, size.width * 4,
			// VideoFormat.FALSE, Format.NOT_SPECIFIED);
			// dimensions, the total size, the format, the framerate, number of
			// bits per pixel, red mask, blue, green
			// rgbFormat = new RGBFormat(size, maxDataLength, Format.byteArray,
			// frameRate, 24, 3, 2, 1);
			// Our Data buffer
			data = new byte[maxDataLength];
			// For the SGProc
			// It is interesting that this is a different size... When it writes
			// onto the gworld qt is fixing this issue, I guess..
			rawData = new byte[QTImage.getMaxCompressionSize(gw, gw.getBounds(), 0, StdQTConstants.codecLowQuality,
					thisCodec, CodecComponent.anyCodec)];
			if (TESTING) {
				// Setting up the buffered image
				int s = gw.getPixMap().getPixelData().getSize(); // in bytes
				System.out.println("PixMap Size: " + s);
				int intsPerRow = gw.getPixMap().getPixelData().getRowBytes() / 4;
				System.out.println("IntsPerRow: " + intsPerRow);
				s = intsPerRow * cis.getHeight(); // in ints
				System.out.println("Actual Size: " + s);
				pixelData = new int[s];
				DataBuffer db = new DataBufferInt(pixelData, s);
				ColorModel colorModel = new DirectColorModel(32, 0x00ff0000, 0x0000ff00, 0x000000ff);
				int[] masks = { 0x00ff0000, 0x0000ff00, 0x000000ff };
				WritableRaster raster = Raster.createPackedRaster(db, cis.getWidth(), cis.getHeight(), intsPerRow,
						masks, null);
				image = new BufferedImage(colorModel, raster, false, null);
				ret = new MyComp();
				Frame myFrame = new Frame("Test");
				myFrame.setBounds(100, 100, cis.getWidth(), cis.getHeight());
				myFrame.add(ret);
				myFrame.show();
			}
			// Create our data proc, execute get's called
			sg.setDataProc(this);
			// Run the sg
			// To keep from erroring out on the sq.idleMore()
			// QTFile mFile = new QTFile("/Users/vanevery/Desktop/testmov.mov");
			// sg.setDataOutput(mFile,StdQTConstants.seqGrabToDisk);
			// Fixed with the below line:
			sg.setDataOutput(null, quicktime.std.StdQTConstants.seqGrabDontMakeMovie);
			sg.prepare(false, true); // preview and record
			sg.startRecord();
			// Setup the Camera Idler
			IdleCamera idleCameraRunner = new IdleCamera();
			qtIdleThread = new Thread(idleCameraRunner);
			qtIdleThread.start();
		}
		catch (QTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class IdleCamera implements Runnable {

		public void run() {
			try {
				while (true) {
					sg.idleMore();
					// sg.idle();
					sg.update(null);
				}
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	// The data proc which pushes the data into the buffer via a callback
	int count = 0;

	public int execute(SGChannel chan, QTPointerRef dataToWrite, int offset, int chRefCon, int time, int writeType) {
		if (chan instanceof SGVideoChannel) {
			try {
				// Not sure why?
				ImageDescription id = vc.getImageDescription();
				// This should only happen the first time..?
				if (rawData == null) {
					rawData = new byte[dataToWrite.getSize()];
				}
				if (TESTING) {
					if (count % 50 == 0) {
						System.out.println("New Data: " + dataToWrite.getSize());
					}
				}
				// Write the new data to the rawData array
				dataToWrite.copyToArray(0, rawData, 0, dataToWrite.getSize());
				// Create the RawEncodedImage before putting the data in the
				// array?
				RawEncodedImage ri = new RawEncodedImage(rawData);
				// Create a DSequence with the rawData, not sure why when it is
				// null it is created and when it isn't null it is decompressed
				if (ds == null) {
					ds = new DSequence(id, ri, gw, cis, idMatrix, null, 0, StdQTConstants.codecNormalQuality,
							CodecComponent.anyCodec);
				}
				else {
					ds.decompressFrameS(ri, StdQTConstants.codecNormalQuality);
				}
				// Copy the decompressed data out to the data array..? // This
				// doesn't really make sense..
				gw.getPixMap().getPixelData().copyToArray(0, data, 0, data.length);
				// data = myArrayCopy(gw.getPixMap(), size.width, size.height);
				if (TESTING) {
					if (count % 50 == 0) {
						System.out.println("Other New Data: " + gw.getPixMap().getPixelData().getSize());
						System.out.println("Middle Data: " + data[500] + " " + data[501] + " " + data[502] + " "
								+ data[503]);
					}
					count++;
					gw.getPixMap().getPixelData().copyToArray(0, pixelData, 0, pixelData.length);
					ret.repaint();
				}
				return 0;
			}
			catch (Exception ex) {
				ex.printStackTrace();
				return 1;
			}
		}
		else {
			return 1;
		}
	}

	byte[] myArrayCopy(PixMap myPixMap, int width, int height) {
		byte[] arrayToCopyTo;// = new byte[width*height*4];
		RawEncodedImage rawImage = myPixMap.getPixelData();
		int qtRowBytes = myPixMap.getRowBytes();
		int normRowBytes = width * 4;
		int diff = qtRowBytes - normRowBytes;
		if (TESTING)
			System.out.println("Difference: " + (qtRowBytes - normRowBytes));
		byte[] qtpixel = new byte[qtRowBytes * height];
		arrayToCopyTo = new byte[normRowBytes * height];
		rawImage.copyToArray(0, qtpixel, 0, qtpixel.length);
		if (TESTING)
			System.out.println("arrayToCopyTo: " + arrayToCopyTo.length);
		if (TESTING)
			System.out.println("qtpixel: " + qtpixel.length);
		if (qtRowBytes != normRowBytes) {
			// Copy one row at a time to get rid of extrapixels
			for (int i = 0; i < height; i++) {
				System.arraycopy(qtpixel, i * qtRowBytes + diff, arrayToCopyTo, i * normRowBytes, normRowBytes);
				if (TESTING)
					System.out.println("i:" + i + " i*qtRowBytes: " + (i * qtRowBytes) + " i*normRowBytes: "
							+ (i * normRowBytes));
			}
		}
		else {
			// They match so do a straight copy
			System.arraycopy(qtpixel, 0, arrayToCopyTo, 0, normRowBytes * height);
		}
		return arrayToCopyTo;
	}

	// Setting up a component, capable of displaying the image for testing
	class MyComp extends Component {

		public void paint(Graphics g) {
			super.paint(g);
			g.drawImage(image, 0, 0, this);
		}
	}

	private UserData fileToSettings() {
		UserData ud = null;
		try {
			FileInputStream fileIn;
			fileIn = new FileInputStream(this.getClass().getName() + ".vset");
			// make a straw for sucking the object in from the file
			ObjectInputStream in = new ObjectInputStream(fileIn);
			// suck in the object and cast it into the right type
			byte[] bytes = (byte[]) in.readObject();
			ud = new UserData(new QTHandle(bytes));
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (StdQTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (QTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ud;
	}

	private void settingsToFile(byte[] settings) {
		try {
			FileOutputStream fileOut = new FileOutputStream(this.getClass().getName() + ".vset");
			// make straw for spitting out the object
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			// send the object down the straw
			out.writeObject(settings);
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}