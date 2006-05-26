package com.golden.gamedev.gui.theme.basic;

import java.awt.*;
import java.awt.image.BufferedImage;

import com.golden.gamedev.gui.TFloatPanel;
import com.golden.gamedev.gui.toolkit.*;

public class BTitleBarButtonRenderer extends BButtonRenderer {

	public BTitleBarButtonRenderer() {
		put("Background Color", new Color(204, 204, 255));
		put("Background Border Color", new Color(102, 102, 153));

		put("Icon Close Image", GraphicsUtil.loadImage(getClass(), "icons/close.png", Transparency.BITMASK));
		put("Icon Minimize Image", GraphicsUtil.loadImage(getClass(), "icons/minimize.png", Transparency.BITMASK));
		put("Icon Restore Image", GraphicsUtil.loadImage(getClass(), "icons/restore.png", Transparency.BITMASK));
	}

	public String UIName() { return "TitleBarButton"; }

	public void processUI(TComponent component, BufferedImage[] ui) {
		TFloatPanel.TTitleBar.TTitleBarButton button =
			(TFloatPanel.TTitleBar.TTitleBarButton) component;

		for (int i=0;i < ui.length;i++) {
			Graphics2D g = ui[i].createGraphics();
			int w = ui[i].getWidth(),
				h = ui[i].getHeight();

			BufferedImage img;
			switch (button.getAction()) {
				case TFloatPanel.TTitleBar.TTitleBarButton.CLOSE_BUTTON:
					img = (BufferedImage) get("Icon Close Image", component);
					g.drawImage(img,
								((w-img.getWidth())/2)+1,
								((h-img.getHeight())/2)+1, null);
				break;
				case TFloatPanel.TTitleBar.TTitleBarButton.ICONIFIED_BUTTON:
					TFloatPanel pane = (TFloatPanel) button.getContainer().getContainer();
					img = (BufferedImage) get((pane.isIcon() == true) ? "Icon Restore Image" : "Icon Minimize Image",
											  component);
					g.drawImage(img,
								((w-img.getWidth())/2)+1,
								((h-img.getHeight())/2)+1, null);
				break;
			}
		}
	}

}