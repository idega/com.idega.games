package vxp;

public class QTLiveBWPixelSource extends QTLivePixelSource {
	
	byte[] newPixels;
	public QTLiveBWPixelSource(int _pw, int _ph, int _frameRate) {
		super(_pw, _ph, _frameRate);
		// TODO Auto-generated constructor stub
	}
	
	/**This needs to be done after configuring the video where you learn about the widths and heights available.  You usually don't have to worry about this.*/
	public void setNativeArrays() {
		newPixels = new byte[vidWidth * kHeight];
	}
	
	public boolean grabFrame() {
	    newPixels = pixels.getBytes();
		//pixels.copyToArray(0, newPixels, 0, vidWidth * kHeight);
		return true;
	}

}
