package vxp;
import quicktime.QTException;
import quicktime.QTSession;
import quicktime.app.view.MoviePlayer;
import quicktime.io.QTFile;
import quicktime.qd.PixMap;
import quicktime.qd.QDGraphics;
import quicktime.qd.QDRect;
import quicktime.std.StdQTConstants;
import quicktime.std.StdQTConstants4;
import quicktime.std.StdQTException;
import quicktime.std.movies.Movie;
import quicktime.std.movies.media.DataRef;
import quicktime.util.QTUtils;
import quicktime.util.RawEncodedImage;
/*
 * Created on Feb 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class QTMoviePixelSource extends PixelSource implements StdQTConstants{
	private MoviePlayer myMoviePlayer;

	private RawEncodedImage pixels;
	private int duration;
	private int lastTime= 0;
	private int mt;
	private float rate = 1.0f;
	TicklingThread tikle ;
	Movie myMovie = null;
    /**
     * @param pw
     * @param ph
     * @param myMoviePlayer
     * @param pixels
     * @param duration
     */
	
	public QTMoviePixelSource(String myFileName, boolean isURL,boolean _loop,boolean _start) {
		super(320, 240); //I am goint to change these later
	
		openingStuff(myFileName,  isURL, _loop, _start);
		
	}
	
	public QTMoviePixelSource(String myFileName, boolean isURL,boolean _loop,boolean _start, int _frameRate) {
		super(320, 240); //I am goint to change these later
		openingStuff(myFileName,  isURL, _loop, _start);
		if (_frameRate > 0){
		tikle = new TicklingThread(this,_frameRate);
		tikle.startMe();
		}
	}
  
	public int getDuration(){
		return duration;
	}
	
	void openingStuff(String myFileName,boolean isURL, boolean _loop, boolean _start){
	    try { //open up the video
			QTSession.open();
		} catch (Exception ee) {
			System.out.println("QuickTime For Java Not Installed in the current Java jre/lib/ext folder");
			ee.printStackTrace();
			QTSession.close();
		}
			DataRef fileRef = null;
	
		try {
		    
			if (isURL) {
				fileRef = new DataRef(myFileName);
			} else {
				QTFile myFile = new QTFile(myFileName);
				fileRef = new DataRef(myFile);
			}
			myMovie = Movie.fromDataRef(fileRef, StdQTConstants4.newMovieAsyncOK | StdQTConstants.newMovieActive);
			myMoviePlayer = new MoviePlayer(myMovie);
			System.out.println("Got Movie " + myMovie +  " and player " + myMoviePlayer);
	
			
			while (myMovie.maxLoadedTimeInMovie() == 0) {
				myMovie.task(100);
			}
			
			if (_loop){
			myMovie.getTimeBase().setFlags(loopTimeBase);
			}
		

			QDRect r = myMoviePlayer.getDisplayBounds();
			kWidth = r.getWidth(); //set this in the interhited class
			kHeight = r.getHeight();
			//	create offscreen space and link movie to it
			QDGraphics myQDGraphics = new QDGraphics(new QDRect(kWidth, kHeight));
			myMoviePlayer.setGWorld(myQDGraphics);
		
			if (_start){
			myMoviePlayer.setRate(rate);
			}else{
				myMoviePlayer.setRate(0.0f);
			}
					PixMap pmap = myQDGraphics.getPixMap();
			//			   get image data
			pixels = pmap.getPixelData();
			System.out.println("Pixel Format:" + QTUtils.fromOSType(pmap.getPixelFormat()) + " Pixel Format:" + pmap.getPixelSize());
			if (pmap.getPixelFormat() == quicktime.qd.QDConstants.k32BGRAPixelFormat) {
				bluePosition = 3;
				greenPosition = 2;
				redPosition = 1;
				alphaPosition = 4;
				System.out.println("PC Color Model");
				alphaShift = 24;
				redShift = 16;
				greenShift = 8;
				blueShift = 0;
			}
			int bytesInRow = pixels.getRowBytes();
			int padBytes = (bytesInRow - kWidth * 4);
			int padPixels = padBytes / 4;
			vidWidth = kWidth + padPixels;
			myMovie.prePreroll(0, 1.0f);
		} catch (QTException ee) {System.out.println("Problem with getting movie and player" + ee);}
		try{
		  
			myMovie.preroll(0, 1.0f);
		} catch (QTException ee) {System.out.println("Problem with preroll" + ee);}
		setNativeArrays();
		setImageType();
		try {
            duration = myMoviePlayer.getDuration();
        } catch (StdQTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	

	/**Be sure  to call this when you close or destroy your main window so a connection to your camera is not left hanging .*/
	public void killSession(){
	if (tikle != null){
	    tikle.stopMe();
	}
	    stop();
		QTSession.close();
	}
	public void setTime(int _time){
	    try {
            myMoviePlayer.setTime(_time);
        } catch (StdQTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	public void setRate(float _rate){
		  rate = _rate;
	}
	
	public void play(){
	    try {
            myMoviePlayer.setRate(rate);
        } catch (StdQTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	public void start(){
	    try {
            myMovie.start();
        } catch (StdQTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	    
	}
	public void stop(){
	   
	    try {
            myMoviePlayer.setRate(0.0f);
        } catch (StdQTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	public String getDeviceName() {
		return "Movie";
	}
	
	public int getTime(){
	    int  t = -1;
        try {
            t = myMoviePlayer.getTime();
                   } catch (StdQTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return t;
	    
	}
	public float getProgress(){
	    float t = -1.0f;
        try {
            t = (float)myMoviePlayer.getTime()/duration;
                   } catch (StdQTException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return t;
	    
	}
	/**This gives you a fresh frame for getPixel and setPixel to operate on.*/
	public boolean grabFrame() {
		//try{
		//mGrabber.idle();
		//}  catch (QTException ee) { }
		pixels.copyToArray(0, newPixels, 0, vidWidth * kHeight);

			try {
			    mt = myMoviePlayer.getTime();
			    if ( mt != lastTime){
			        lastTime = mt;
			        return true;
			    }else{
			        return false;
			    }
               // return (myMoviePlayer.getTime() != lastTime); //(getProgress() != 1); //(myMoviePlayer.getRate() != 0.0f);
                //if (myMoviePlayer.getRate() == 0) return false;
                //else return true;
            } catch (StdQTException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
		
		
	}
    /* (non-Javadoc)
     * @see quicktime.app.display.DrawingListener#drawingComplete(quicktime.app.display.QTDrawable)
     */
   // public void drawingComplete(QTDrawable arg0) {
    //    tellVideoListeners();
        
    //}
}
