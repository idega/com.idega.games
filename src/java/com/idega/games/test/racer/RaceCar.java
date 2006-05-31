/*
 * $Id: RaceCar.java,v 1.1 2006/05/31 19:19:33 eiki Exp $ Created on May 31, 2006
 * 
 * Copyright (C) 2006 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.games.test.racer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import com.golden.gamedev.object.Sprite;

/**
 * 
 * FROM http://www.goldenstudios.or.id/forum/showthread.php?tid=1063 Example in
 * the update of the Game class: [code] .... shipControl.update();
 * 
 * if(keyDown(java.awt.event.KeyEvent.VK_A) || keyDown
 * (java.awt.event.KeyEvent.VK_LEFT)) { shipControl.left(); }
 * if(keyDown(java.awt.event.KeyEvent.VK_D) || keyDown
 * (java.awt.event.KeyEvent.VK_RIGHT)) { shipControl.right(); } if(keyDown
 * (java.awt.event.KeyEvent.VK_W) || keyDown (java.awt.event.KeyEvent.VK_UP)) {
 * shipControl.up (); } if(keyDown (java.awt.event.KeyEvent.VK_SPACE)) {
 * shipControl.brake (); }
 * 
 * shipControl.noKey();
 * 
 * Last modified: $Date: 2006/05/31 19:19:33 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.1 $
 */
public class RaceCar extends Sprite {

	double angle = 0; // = 360 = top.
	private BufferedImage old; // Fill it with the original car (looking to the
	// top!)
	// TURNING
	public double turn_speed = 0.012;
	public int turn_duration = 3;
	// SPEED
	public double max_speed = 2;
	public double accelaration = 0.08;

	public void setAngle(double i) {
		angle += i;
		if (angle >= 360) {
			angle = 360 - angle;
		}
		int sz = 0;
		int szW = (int) ((double) old.getHeight() * 1.25);
		int szH = (int) ((double) old.getWidth() * 1.25);
		if (szW > szH)
			sz = szW;
		else
			sz = szH;
		BufferedImage bi = new BufferedImage(sz, sz, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.translate(15, 15);
		g.rotate(angle);
		g.translate(-15, -15);
		g.drawImage(old, 0, 0, null);
		this.setImage(bi);
	}
}