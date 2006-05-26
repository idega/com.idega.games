package com.golden.gamedev.gui;

import java.awt.*;
import java.awt.image.BufferedImage;

import com.golden.gamedev.gui.toolkit.*;

/**
 * TPanel is the simplest opaque container class.
 */
public class TPanel extends TContainer {

	public TPanel(int x, int y, int w, int h) {
		super(x,y,w,h);
	}

	/**
	 * This Component UI Name is <b>Panel</b>.
	 */
	public String UIName() { return "Panel"; }

}