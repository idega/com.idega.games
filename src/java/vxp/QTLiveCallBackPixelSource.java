package vxp;

import quicktime.QTException;
import quicktime.qd.PixMap;
import quicktime.qd.QDGraphics;
import quicktime.qd.QDRect;
import quicktime.std.StdQTConstants;
import quicktime.std.sg.SGChannel;
import quicktime.std.sg.SGDataProc;
import quicktime.std.sg.SGVideoChannel;
import quicktime.std.sg.SequenceGrabber;
import quicktime.util.QTPointerRef;
import quicktime.util.QTUtils;

public class QTLiveCallBackPixelSource extends QTLivePixelSource implements SGDataProc {

	public QTLiveCallBackPixelSource(int pw, int ph, int _frameRate) {
		super(pw, ph, 0);
		if (_frameRate != 0) {
			tikle = new Idler(this, _frameRate);
			tikle.startMe();
		}
	}

	void configureSGObject() {
		// you may never want to look at this, this is where we hook up with
		// quicktime
		try {
			QDRect bounds = new QDRect(kWidth, kHeight);
			QDGraphics qd = new QDGraphics(bounds);
			mGrabber = new SequenceGrabber();
			// UserData ud = fileToSettings();
			/*
			 * if (ud != null) { System.out.println("got ud" + ud);
			 * mGrabber.setSettings(ud); //mVideo.digitizerChanged(); }
			 */
			mVideo = new SGVideoChannel(mGrabber);
			/*
			 * if (ud != null) { System.out.println("got ud" + ud);
			 * mGrabber.setSettings(ud); //mVideo.digitizerChanged(); }
			 */
			mVideo.setBounds(bounds);
			mVideo.setUsage(seqGrabPreview | seqGrabRecord);
			mGrabber.setDataProc(this);
			mGrabber.setDataOutput(null, quicktime.std.StdQTConstants.seqGrabDontMakeMovie);
			int thisCodec = StdQTConstants.kRawCodecType;
			mVideo.setCompressorType(thisCodec); // This was
													// kComponentCodecType or
													// something of the sort
			mGrabber.setGWorld(qd, null);
			mGrabber.prepare(false, true);
			// mGrabber.prepare(true, true);
			// mGrabber.startPreview();
			mGrabber.startRecord();
			PixMap pmap = qd.getPixMap();
			// pmap.lockHigh();
			pixels = pmap.getPixelData();
			System.out.println("Pixel Format:" + QTUtils.fromOSType(pmap.getPixelFormat()) + " Pixel Format:"
					+ pmap.getPixelSize());
			// mac and pc put the colors in different orders
			// System.out.println (0x27format0x27 + pmap.getPixelFormat() + 0x27
			// BGRA=0x27 +quicktime.qd.QDConstants.k32BGRAPixelFormat + 0x27
			// ARGB=0x27 +quicktime.qd.QDConstants.k32ARGBPixelFormat);
			if (pmap.getPixelFormat() == quicktime.qd.QDConstants.k32BGRAPixelFormat) {
				bluePosition = 3;
				greenPosition = 2;
				redPosition = 1;
				alphaPosition = 4;
				// System.out.println("PC Color Model");
				alphaShift = 24;
				redShift = 16;
				greenShift = 8;
				blueShift = 0;
			}
			int bytesInRow = pixels.getRowBytes();
			int padBytes = (bytesInRow - kWidth * 4);
			int padPixels = padBytes / 4;
			vidWidth = kWidth + padPixels;
		}
		catch (QTException ee) {
			ee.printStackTrace();
			// QTSession.close();
		}
	} // end of create object

	/** This tickles whatever is suppling the video to give up fresh pixels. */
	public void idleIt() {
		try {
			// mGrabber.idle();
			mGrabber.idleMore();
			// sg.idle();
			mGrabber.update(null);
		}
		catch (QTException ee) {
		}
	}

	public int execute(SGChannel chan, QTPointerRef dataToWrite, int offset, int chRefCon, int time, int writeType) {
		if (chan instanceof SGVideoChannel) {
			// System.out.println("New Data: " + (dataToWrite.getSize()/4));
			// grabFrame();
			tellVideoListeners();
			/*
			 * try {
			 *  // Not sure why? ImageDescription id = vc.getImageDescription();
			 *  // This should only happen the first time..? if (rawData ==
			 * null) { rawData = new byte[dataToWrite.getSize()]; }
			 * 
			 * if (TESTING) { if (count%50 == 0) { System.out.println("New Data: " +
			 * dataToWrite.getSize()); } }
			 * 
			 *  // Write the new data to the rawData array
			 * dataToWrite.copyToArray(0, rawData, 0, dataToWrite.getSize());
			 *  // Create the RawEncodedImage before putting the data in the
			 * array? RawEncodedImage ri = new RawEncodedImage(rawData);
			 *  // Create a DSequence with the rawData, not sure why when it is
			 * null it is created and when it isn't null it is decompressed if
			 * (ds == null) { ds = new
			 * DSequence(id,ri,gw,cis,idMatrix,null,0,StdQTConstants.codecNormalQuality,CodecComponent.anyCodec); }
			 * else { ds.decompressFrameS(ri,StdQTConstants.codecNormalQuality); }
			 *  // Copy the decompressed data out to the data array..? // This
			 * doesn't really make sense..
			 * gw.getPixMap().getPixelData().copyToArray(0,data, 0,
			 * data.length); //data = myArrayCopy(gw.getPixMap(), size.width,
			 * size.height);
			 * 
			 * if (TESTING) { if (count%50 == 0) { System.out.println("Other New
			 * Data: " + gw.getPixMap().getPixelData().getSize());
			 * System.out.println("Middle Data: " + data[500] + " " + data[501] + " " +
			 * data[502] + " " + data[503]); } count++;
			 * 
			 * gw.getPixMap().getPixelData().copyToArray(0,pixelData, 0,
			 * pixelData.length); ret.repaint(); }
			 * 
			 * return 0; } catch (Exception ex) { ex.printStackTrace(); return
			 * 1; } } else { return 1;
			 */
		}
		return 0;
	}
	
}
