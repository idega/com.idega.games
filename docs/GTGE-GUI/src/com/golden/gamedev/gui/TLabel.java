package com.golden.gamedev.gui;

import java.awt.*;
import java.awt.image.BufferedImage;

import com.golden.gamedev.gui.toolkit.*;

public class TLabel extends TComponent {
	private String text;

	public TLabel(String text, int x, int y, int w, int h) {
		super(x,y,w,h);

		this.text = text;
	}

	public String getText() { return text; }
	public void setText(String st) {
		text = st; createUI();
	}

	/**
	 * This Component UI Name is <b>Label</b>.
	 */
	public String UIName() { return "Label"; }

}