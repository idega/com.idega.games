/*
 * $Id: ImageGrabberVideoListener.java,v 1.3 2007/01/29 09:08:45 eiki Exp $
 * Created on May 28, 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package sputnick.webcamgrabber.util;

import vxp.PixelSource;
import vxp.VideoListener;

public class ImageGrabberVideoListener implements VideoListener {

	PixelSource ps;
	long interval = 10 * 1000;// 5 seconds
	long lastTime = System.currentTimeMillis();
	long currentTime = System.currentTimeMillis();

	public ImageGrabberVideoListener(PixelSource ps) {
		this.ps = ps;
		ps.addVideoListener(this);
	}

	public void newFrame() {
		currentTime = System.currentTimeMillis();
		if ((currentTime - lastTime) >= interval) {
			lastTime = currentTime;
			// create the image in a separate thread so we don't stop the
			// process
			new Thread(new Runnable() {

				public void run() {
					// try {
					// while (true) {
					grabImage();
					// sleep(1000); System.out.print(".");
					// }
					// }
					// catch (InterruptedException ex) {
					// }
				}
			}).start();
		}
	}

	protected void grabImage() {
		String filePathAndName = System.getProperty("user.home") + "/Sites/images/grabbedImage.gif";
		int[] pixelArray = ps.grabFrameToArray();
		PixelUtil.encodePNG(ps.getImage(pixelArray), filePathAndName);
		// uploadToServerByFTP("darwin.idega.is","~/Sites/images","eiki.jpg","eiki","p1par",filePathAndName);
	}

}
