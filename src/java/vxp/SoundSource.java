package vxp;

import quicktime.QTException;
import quicktime.sound.SPBDevice;
import quicktime.sound.SoundConstants;
import quicktime.sound.SoundException;

/**
 * @author DanO
 * 
 */

public class SoundSource extends Thread implements SoundConstants {

	int threshold;

	SoundListener sListener;

	SPBDevice mySound;

	boolean looper = true;

	/**
	 * This is the constructor to use when you don't want a callback. Instead
	 * you would keep asking getLevel (This crashed for me).
	 * 
	 */
	public SoundSource() {
		connectToSoundInput();
	}

	/**
	 * When you use this constructor, the soundSource listens for you and alerts
	 * when the sound has gone over the threshold you supplied. You have to give
	 * a reference to yourself and you have to implement the SoundListener
	 * interface by have a "newSound(int _level)" method. The listening thread
	 * is automaticallys started for you so you don't have to do the usual
	 * addBlaListener(this).
	 * 
	 */
	public SoundSource(SoundListener _sListener, int _threshold) {
		// mySound = new SndChannel(0,0) ;
		sListener = _sListener;
		threshold = _threshold;
		connectToSoundInput();
		start();
	}

	/**
	 * This seems to crash too.
	 * 
	 */
	public void soundSettings() {
		try {
			mySound.showOptionsDialog();
		} catch (SoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void connectToSoundInput() {
		try {

			// mySound = new SPBDevice (null, siActiveLevels);
			mySound = new SPBDevice(null, SoundConstants.siWritePermission);
			mySound.setLevelMeterOnOff(true);

			for (int i = 1; i < 10; i++) {
				String d = SPBDevice.getIndexedDevice(i);
				if (d == null)
					break;
				System.out.println(i + " : " + d);

			}
			// mySound = new SPBDevice(SPBDevice.getIndexedDevice(1),1) ;
			String[] sources = mySound.getInputSourceNames();
			int[] levelTest = mySound.getActiveLevels();
			System.out.println(levelTest.length + " active levels");

			for (int i = 0; i < sources.length; i++) {
				System.out.println("Source " + sources[i]);
			}
			// mySound.showOptionsDialog();
			// mySound.setLevelMeterOnOff(true);
		} catch (SoundException ee) {
			System.out.println("no device" + ee);
			// ee.printStackTrace();
		}

	}

	/**
	 * Theoretically you can get the sound level but this crashes for me. I
	 * guess I should work on it
	 * 
	 */
	public int getLevel() {
		int level = 0;
		try {
			level = mySound.getLevelMeterLevel();

		} catch (SoundException ee) {
			System.out.println("no device" + ee);
			// ee.printStackTrace();
		}
		return level;
	}

	/**
	 * Be sure to clean up after you are done listening.
	 * 
	 */
	public void kill() {
		looper = false;
		try {
			mySound.disposeQTObject();
		} catch (QTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			while (looper) {
				this.sleep(2);
				int level = mySound.getLevelMeterLevel();
				// System.out.println(" " + level);
				if (level > threshold) {
					sListener.newSound(level);
				}

			}
		} catch (InterruptedException e) {
		} catch (SoundException ee) {
			System.out.println("no device" + ee);
		}

	}
}