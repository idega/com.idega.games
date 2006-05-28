package vxp;
/*
 * Created on Apr 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author Administrator
 *
 * This class really is just an alias to the QTLivePixelSource.
 * It exists for backward compatablility.
 */

public class DefaultPixelSource extends QTLivePixelSource {
	

	public DefaultPixelSource(  int pw, int ph,int _frameRate ){
		super(pw,ph,_frameRate);
	}
	
}
