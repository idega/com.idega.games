package vxp;

import quicktime.QTException;
import quicktime.io.OpenMovieFile;
import quicktime.io.QTFile;
import quicktime.qd.QDConstants;
import quicktime.qd.QDException;
import quicktime.qd.QDGraphics;
import quicktime.qd.QDRect;
import quicktime.std.StdQTConstants;
import quicktime.std.StdQTException;
import quicktime.std.image.CSequence;
import quicktime.std.image.CodecComponent;
import quicktime.std.image.CompressedFrameInfo;
import quicktime.std.image.ImageDescription;
import quicktime.std.image.QTImage;
import quicktime.std.movies.Movie;
import quicktime.std.movies.Track;
import quicktime.std.movies.media.VideoMedia;
import quicktime.util.QTHandle;
import quicktime.util.RawEncodedImage;

public class MovieMaker implements StdQTConstants {

	int width = 320;

	int height = 240;

	int TIME_SCALE = 600; // 100?
	
	boolean readyForFrames;

	int KEY_FRAME_RATE = 15;

	VideoMedia videoMedia;

	Track videoTrack;

	Movie movie;

	QTFile movFile;

	CSequence seq;

	QTHandle imageHandle;

	QDGraphics gw;

	QDRect bounds;

	ImageDescription imgDesc;

	RawEncodedImage compressedImage;
	
	String path;

	int rate;

	public MovieMaker(QTLivePixelSource ps,  String _path, int codecType, int codecQuality, int _rate) {
		width = ps.getVideoWidth();
		height = ps.getVideoHeight();
		rate = _rate;
		
		gw = ps.getGraphics();
		initMovie( _path, codecType, codecQuality);
	}
	public MovieMaker(int _w, int _h, String _path, int codecType, int codecQuality, int _rate) {
		width = _w;
		height = _h;
		rate = _rate;
	
		try {
			ImageDescription imgDesc2 = new ImageDescription(QDConstants.k32ARGBPixelFormat);
			imgDesc2.setWidth(width);
			imgDesc2.setHeight(height);
			gw = new QDGraphics(imgDesc2, 0);
		} catch (QTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initMovie( _path,  codecType, codecQuality);
	
	}
	
	void initMovie(String _path, int codecType, int codecQuality){
		if (codecType == 0) codecType = StdQTConstants.kCinepakCodecType;
		if (codecQuality == 0) codecQuality = StdQTConstants.codecNormalQuality;

		try {
            movFile = new QTFile (new java.io.File(_path));
			movie = Movie.createMovieFile(movFile, kMoviePlayer, createMovieFileDeleteCurFile | createMovieFileDontCreateResFile);
			int timeScale = TIME_SCALE; // 100 units per second
			videoTrack = movie.addTrack(width, height, 0);
			videoMedia = new VideoMedia(videoTrack, timeScale);
			videoMedia.beginEdits();
			bounds = new QDRect(0, 0, width, height);
			int rawImageSize = QTImage.getMaxCompressionSize(gw, bounds, gw.getPixMap().getPixelSize(), codecQuality, codecType, CodecComponent.anyCodec);
			imageHandle = new QTHandle(rawImageSize, true);
			imageHandle.lock();
			compressedImage = RawEncodedImage.fromQTHandle(imageHandle);
			seq = new CSequence(gw, bounds, gw.getPixMap().getPixelSize(), codecType, CodecComponent.bestFidelityCodec, codecQuality, codecQuality, KEY_FRAME_RATE, null, 0);
			imgDesc = seq.getDescription();
			readyForFrames = true;
		} catch (StdQTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QDException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	public void finishMovie() {
		try {
			readyForFrames = false;
			videoMedia.endEdits();
			videoTrack.insertMedia(0, 0, videoMedia.getDuration(), 1);
			OpenMovieFile omf = OpenMovieFile.asWrite(movFile);
			movie.addResource(omf, movieInDataForkResID, movFile.getName());
	
		} catch (StdQTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void addFrame(int[] _pixels) {
		if (readyForFrames){
		RawEncodedImage pixelData = gw.getPixMap().getPixelData();
		int rowBytes = pixelData.getRowBytes();
		for (int j = 0; j < height; j++)
			pixelData.copyFromArray(j * rowBytes, _pixels, j * width, width);
		compressAndAdd();
		}
	}

	public void addFrame() {
		if (readyForFrames){
		compressAndAdd();
		}
	}

	void compressAndAdd() {
		try {
			CompressedFrameInfo cfInfo = seq.compressFrame(gw, bounds, codecFlagUpdatePrevious, compressedImage);
			boolean syncSample = cfInfo.getSimilarity() == 0; // see developer.apple.com/qa/qtmcc/qtmcc20.html
			videoMedia.addSample(imageHandle, 0, cfInfo.getDataSize(), rate, imgDesc, 1, syncSample ? 0 : mediaSampleNotSync);
		} catch (StdQTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
