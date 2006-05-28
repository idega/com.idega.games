/*
 * Created on Oct 1, 2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package vxp;

/**
 * @author Dan O'Sullivan
 * 
 * Describes how what methods to implement in order to hear back using the
 * newSound method when the sound has past a threshold .
 * 
 */
public interface SoundListener {

	public void newSound(int _level);
}
