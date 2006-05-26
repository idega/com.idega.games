package com.golden.gamedev.gui.theme.basic;

import java.awt.*;
import java.awt.image.BufferedImage;

import com.golden.gamedev.gui.TTextField;
import com.golden.gamedev.gui.toolkit.*;

public class BTextFieldRenderer extends UIRenderer {

	public BTextFieldRenderer() {
		put("Background Color", Color.WHITE);
		put("Background Disabled Color", Color.WHITE);
		put("Background Uneditable Color", new Color(204, 204, 204));

		put("Background Border Color", Color.BLACK);
		put("Background Border Disabled Color", Color.DARK_GRAY);
		put("Background Border Uneditable Color", Color.DARK_GRAY);
	}

	public String UIName() { return "TextField"; }
	public String[] UIDescription() {
		return new String[] {
			"TextField", "TextField Disabled", "TextField Not Editable"
		};
	}

	public BufferedImage[] createUI(TComponent component, int w, int h) {
		BufferedImage[] ui = GraphicsUtil.createImage(3, w, h, Transparency.OPAQUE);

		String[] color = new String[] {
			"Background Color", "Background Disabled Color",
			"Background Uneditable Color"
		};
		String[] border = new String[] {
			"Background Border Color", "Background Border Disabled Color",
			"Background Border Uneditable Color"
		};

		for (int i=0;i < ui.length;i++) {
			Graphics2D g = ui[i].createGraphics();

			g.setColor((Color) get(color[i], component));
			g.fillRect(0, 0, w, h);

			g.setColor((Color) get(border[i], component));
			g.drawRect(0, 0, w-1, h-1);

			g.dispose();
		}

		return ui;
	}

	public void processUI(TComponent component, BufferedImage[] ui) {
	}
	public void renderUI(Graphics2D g, int x, int y,
						 TComponent component, BufferedImage[] ui) {
		TTextField textField = (TTextField) component;

		if (!textField.isEnabled()) {
			g.drawImage(ui[1], x, y, null);

		} else if (!textField.isEditable()) {
			g.drawImage(ui[2], x, y, null);

		} else {
			g.drawImage(ui[0], x, y, null);
		}
	}

}