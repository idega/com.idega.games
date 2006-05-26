package com.golden.gamedev.gui.toolkit;

import com.golden.gamedev.object.GameFont;

/**
 * Constants used for UI Rendering.
 */
public class UIConstants {

	private UIConstants() { }

	public static Integer LEFT = new Integer(GameFont.LEFT);
	public static Integer CENTER = new Integer(GameFont.CENTER);
	public static Integer RIGHT = new Integer(GameFont.RIGHT);

	public static Integer TOP = new Integer(GameFont.LEFT + 10);
	public static Integer BOTTOM = new Integer(GameFont.LEFT + 11);

	public static Integer JUSTIFY = new Integer(GameFont.JUSTIFY);

}