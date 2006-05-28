package vxp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import quicktime.QTException;
import quicktime.QTRuntimeException;
import quicktime.QTRuntimeHandler;
import quicktime.QTSession;
import quicktime.io.IOConstants;
import quicktime.io.QTFile;
import quicktime.qd.Pict;
import quicktime.qd.PixMap;
import quicktime.qd.QDGraphics;
import quicktime.qd.QDRect;
import quicktime.std.StdQTConstants;
import quicktime.std.StdQTException;
import quicktime.std.image.GraphicsImporter;
import quicktime.std.movies.media.UserData;
import quicktime.std.sg.SGVideoChannel;
import quicktime.std.sg.SequenceGrabber;
import quicktime.std.sg.VideoDigitizer;
import quicktime.util.QTHandle;
import quicktime.util.QTUtils;
import quicktime.util.RawEncodedImage;

public class QTLivePixelSource extends PixelSource implements StdQTConstants { //SGDataProc,
	protected SequenceGrabber mGrabber;
	protected RawEncodedImage pixels;
	protected SGVideoChannel mVideo;
	protected TicklingThread tikle;
	protected 	QDGraphics qd;
	int input = -1;

	String[] deviceList;
	/**This is the constructor class.  Try putting in a huge framerate to lessen the lag*/
	public QTLivePixelSource(int pw, int ph, int _frameRate) {
		super(pw, ph );
		try { //open up the video
			QTSession.open();
			QTRuntimeException.registerHandler(new Handler());
			//this handles some benign errors that qtjava throws
			// you must also have a "inner class" which is like a method that is a class
			configureSGObject();
		} catch (Exception ee) {
			System.out.println("QuickTime For Java Not Installed in the current Java jre/lib/ext folder");
			ee.printStackTrace();
			QTSession.close();
		}
		setNativeArrays();
		setImageType();
		if (_frameRate != 0){
			tikle = new TicklingThread(this,_frameRate);
			tikle.startMe();
		}
	}
	
	public QDGraphics getGraphics(){
		return 	 qd;
	}
	
	/**This tickles whatever is suppling the video to give up fresh pixels.  
	 * You shouldn't have to do this if you have done addVideoListener */
	public void idleIt() {
		try {
			mGrabber.idle();
			//mGrabber.idleMore();
			//sg.idle();
			//mGrabber.update(null);
	

		} catch (QTException ee) {}
	}
	

	public String getDeviceName() {
		return "Live";
	}
	/**Puts the pixels in an int array that you supplied .
	*@param newPixels  should be properly sized to  vidWidth*KHeight
	*/
	public void getPixelArray(int[] _newPixels) {
		pixels.copyToArray(0, _newPixels, 0, vidWidth * kHeight);
	}
	

	/**This pops up the video setting dialog box.*/
	public void videoSettings() {
		try {
			VideoDigitizer vd = mVideo.getDigitizerComponent();

			System.out.println("Input Before " + vd.getInput());

			mVideo.settingsDialog();
			mVideo.digitizerChanged();
			 vd = mVideo.getDigitizerComponent();
		
			System.out.println("Input After " + vd.getInput());


			System.out.println("Channel CompressorType " + QTUtils.fromOSType(mVideo.getCompressorType()));
			//System.out.println( "Channel FrameRate " + mVideo.getFrameRate()) ;
			//System.out.println( "Channel Settings " + mVideo.getSettings().toString()) ;
			System.out.println("Channel Number of Inputs " + vd.getNumberOfInputs());
			//System.out.println("Settings" + mVideo.getSettings().putIntoHandle().getBytes());
			settingsToFile(mVideo.getSettings().putIntoHandle().getBytes());

			//settingsToFile(mVideo.getSettings().putIntoHandle().getBytes());
			//showSettings();
		} catch (QTException ee) {
			ee.printStackTrace();
			QTSession.close();
		}
	}
	/**This gives you a fresh frame for getPixel and setPixel to operate on.*/
	public boolean grabFrame() {
		pixels.copyToArray(0, newPixels, 0, vidWidth * kHeight);
		return true;
	}
	
	/**
	 * Brings a new set of pixels into a new array to be used by getPixel(array,..),<br>
	 * setPixel(array,...), getImage(array,..) etc.<br>
	 * This does not affect the internal array of PixelSource!
	 * 
	 * @return a new array from the current frame, you must use pixel methods that have an array property to manipulate it.
	 */
	public int[] grabFrameToArray() {
		int[] independantArray = getEmptyPixelArray();
		pixels.copyToArray(0, independantArray, 0, vidWidth * kHeight);
		return independantArray;
	}
	
	/**Be sure  to call this when you close or destroy your main window so a connection to your camera is not left hanging .*/
	public void killSession() {
		tikle.stopMe();
		/*try {
			mGrabber.stop();
		} catch (StdQTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		System.out.println("kill QT pixel source");
		QTSession.close();
	}
	
	private UserData fileToSettings()
	{

		UserData ud = null;
		try
		{
			FileInputStream fileIn;

			fileIn = new FileInputStream(this.getClass().getName() + ".vset");
			//make a straw for sucking the object in from the file
			ObjectInputStream in = new ObjectInputStream(fileIn);
			//suck in the object and cast it into the right type
			byte[] bytes = (byte[]) in.readObject();
			ud = new UserData(new QTHandle(bytes));
		}
		catch (FileNotFoundException e)
		{
			System.out.println("No existing video setttings");
		}
		catch (StdQTException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (QTException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ud;
	}
	private void settingsToFile(byte[] settings)
	{
		try
		{
			FileOutputStream fileOut = new FileOutputStream(this.getClass()
					.getName()
					+ ".vset");
			//make straw for spitting out the object
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			//send the object down the straw
			out.writeObject(settings);
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	void configureSGObject() {
		//you may never want to look at this, this is where we hook up with
		//quicktime 
		try {
			QDRect bounds = new QDRect(kWidth, kHeight);
			qd = new QDGraphics(bounds);
			mGrabber = new SequenceGrabber();
			mVideo = new SGVideoChannel(mGrabber);
			UserData ud = fileToSettings();
			if (ud != null) {
				 System.out.println("Found Video Settings" );
				 mVideo.setSettings(ud);
				 //System.out.println(ud.toString());
				//mVideo.digitizerChanged();
			}			
			
			
			  VideoDigitizer digitizer = mVideo.getDigitizerComponent();
			  if (input != -1){
				  digitizer.setInput(input);
			  }
			  /* int count = digitizer.getNumberOfInputs();
		      for (int i = 0; i < count; i++) {
		    	
		        System.out.println("format " + digitizer.getInputFormat(i));
		        //if (digitizer.getInputFormat(i) == which) {
		         // digitizer.setInput(i);
		          //return;
		        //}
		      }*/

			/*if (ud != null) {
			    System.out.println("got ud" + ud);
			    mGrabber.setSettings(ud);
				//mVideo.digitizerChanged();
			}*/
			mVideo.setBounds(bounds);
			mVideo.setUsage(seqGrabPreview );

			
			
			
			//mGrabber.setDataProc(this);	
			//mGrabber.setDataOutput(null, quicktime.std.StdQTConstants.seqGrabDontMakeMovie);

		
		//int thisCodec = StdQTConstants.kRawCodecType;
			
		//mVideo.setCompressorType(thisCodec);  // This was kComponentCodecType or something of the sort
		mGrabber.setGWorld(qd, null);
	
			
			mGrabber.prepare(true,true);
			//mGrabber.prepare(true, true);
			mGrabber.startPreview();
			//mGrabber.startRecord();
		
			PixMap pmap = qd.getPixMap();
			//pmap.lockHigh();
			pixels = pmap.getPixelData();
			System.out.println("Pixel Format:" + QTUtils.fromOSType(pmap.getPixelFormat()) + " Pixel Format:" + pmap.getPixelSize());
			//mac and pc put the colors in different orders 
			//System.out.println (0x27format0x27 + pmap.getPixelFormat() + 0x27 BGRA=0x27 +quicktime.qd.QDConstants.k32BGRAPixelFormat + 0x27 ARGB=0x27 +quicktime.qd.QDConstants.k32ARGBPixelFormat); 
			if (pmap.getPixelFormat() == quicktime.qd.QDConstants.k32BGRAPixelFormat) {
				bluePosition = 3;
				greenPosition = 2;
				redPosition = 1;
				alphaPosition = 4;
				//System.out.println("PC Color Model");
				alphaShift = 24;
				redShift = 16;
				greenShift = 8;
				blueShift = 0;
			}
			int bytesInRow = pixels.getRowBytes();
			int padBytes = (bytesInRow - kWidth * 4);
			int padPixels = padBytes / 4;
			vidWidth = kWidth + padPixels;
		} catch (QTException ee) {
			ee.printStackTrace();
			//QTSession.close(); 
		}
	} //end of create object
	static class Handler implements QTRuntimeHandler { //this is just a wierd class you have to havearound and register in the main method because of a bug in QTJAVA 
		public void exceptionOccurred(QTRuntimeException e, Object eGenerator, String methodNameIfKnown, boolean unrecoverableFlag) {}
	}
	
	/**Tells you the names of the video inputs available*/

	public String[] getDeviceList() {
		System.out.println("Video Sources Available");
		try {
		deviceList = new String[mVideo.getDeviceList(0).getCount()];
		for (int i = 0; i < mVideo.getDeviceList(0).getCount(); i++) {
			deviceList[i] = mVideo.getDeviceList(0).getDeviceName(i).getName();
			System.out.println("    " + (i + 1) + ") " + mVideo.getDeviceList(0).getDeviceName(i).getName());
		}
		} catch (QTException ee) {System.out.println("Error getting device list." + ee);}
		return deviceList;
	}
	
	/**You can tell it which video device to use.  Might have to use  getDeviceList() to know which number to use*/
	public String setDevice(int whichSource) {
		getDeviceList();
		try {
			if (whichSource <= deviceList.length) {
				whichSource = whichSource - 1;
				mVideo.setDevice(deviceList[whichSource]);
				return deviceList[whichSource];
			} else {
				return "no such source";
			}
		} catch (QTException ee) {System.out.println("Error setting device" + ee);}
		return "no such source";
	}
	
	public void setInput(int _whichInput){
		input = _whichInput;
		 VideoDigitizer digitizer = mVideo.getDigitizerComponent();

			  try {
				digitizer.setInput(input);
			} catch (StdQTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
	}
	/**You can tell it which video device to use.  Might have to use  getDeviceList() to know which name to use*/
   
	public String setDevice(String whichSource) {
		try {
			mVideo.setDevice(whichSource);
		
		} catch (QTException ee) {System.out.println("Error setting device by name" + ee);}
		return "okay";
	}
	/**This does a quick dump of a jpeg and writes it out to a file at a given pathname in a given filename.*/
	public void makeJPEG(String pathname, String filename) {
		try {
			File newdir = new File(pathname);
			newdir.mkdir();
			QTFile newFile = new QTFile(pathname + "\\" + filename);
			try {
				mGrabber.idle();
			} catch (QTException ee) {}
			QDRect myrect = new QDRect(0, 0, kWidth, kHeight);
			Pict myPict = mGrabber.grabPict(myrect, 32, 0); //sgFlagControlledGrab);
			QTHandle pictHeader = new QTHandle(512, true); //add a header to make it look like a file instead of a handler
			pictHeader.concatenate(myPict); //add the actual information
			// Use the importer to export the JPEG
			GraphicsImporter theGrip = new GraphicsImporter(kQTFileTypePicture);
			theGrip.setDataHandle(pictHeader);
			theGrip.exportImageFile(kQTFileTypeJPEG, 0, newFile, IOConstants.smSystemScript);
			System.out.println("Jpg it: " + pathname + "\\" + filename);
			//encoder.setJPEGEncodeParam(param);
		} catch (QTException e) {
			System.out.println("Error: " + e);
		}
	}
	

}
	