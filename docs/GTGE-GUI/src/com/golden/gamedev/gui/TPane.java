package com.golden.gamedev.gui;

import java.awt.Graphics2D;

import com.golden.gamedev.gui.toolkit.TContainer;

/**
 * TPane is simply a transparent container for grouping components.
 */
public class TPane extends TContainer {

	public TPane(int x, int y, int w, int h) {
		super(x,y,w,h);

		// turn on custom rendering
		customRendering = true;
	}

	/**
	 * This Component UI Name is <b>Pane</b>.
	 */
	public String UIName() { return "Pane"; }

}