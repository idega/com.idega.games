package com.golden.gamedev.gui.theme.basic;

import com.golden.gamedev.gui.toolkit.UITheme;

public class BasicTheme extends UITheme {

	public BasicTheme() {
		installUI(new BButtonRenderer());
		installUI(new BPanelRenderer());
		installUI(new BLabelRenderer());
		installUI(new BTextFieldRenderer());
		installEmptyUI("FloatPanel");
		installUI(new BToolTipRenderer());
		installUI(new BTitleBarRenderer());
		installUI(new BTitleBarButtonRenderer());
	}

	public String getName() { return "Basic Theme"; }

}