package vxp;
/*
 * Created on Mar 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */


import java.awt.Graphics2D;
import java.awt.Image;

/**
 * @author Dan O'Sullivan
 *
 Not sure I actually wrote this one yet.
  */
public class StillImagePixelSource extends PixelSource {

	public StillImagePixelSource( int pw, int ph, Image _image ){
			super(pw,ph);
			vidWidth = pw;
			kWidth = vidWidth;
			kHeight = ph;
			
			setNativeArrays();
			setImageType();
			
			//convert image to bufferedImage
			Graphics2D ig = (Graphics2D) image.getGraphics();
			ig.drawImage(_image,0,0,null); 
			raster = image.getRaster();
			newPixels = null;
			newPixels = raster.getPixels(0,0,vidWidth, kHeight,newPixels);
			//getArray(bi,0,0,vidWidth,kHeight,newPixels);
		}
	

}
