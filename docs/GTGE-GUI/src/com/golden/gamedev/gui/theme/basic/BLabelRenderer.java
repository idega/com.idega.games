package com.golden.gamedev.gui.theme.basic;

import java.awt.*;
import java.awt.image.*;

import com.golden.gamedev.gui.*;
import com.golden.gamedev.gui.toolkit.*;

import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.object.font.SystemFont;
import com.golden.gamedev.util.ImageUtil;

public class BLabelRenderer extends UIRenderer {

	public BLabelRenderer() {
		put("Background Color", null);
		put("Background Border Color", null);
		put("Background Disabled Color", null);

		put("Text Color", Color.BLACK);
		put("Text Disabled Color", Color.GRAY);

		GameFont font = new SystemFont(new Font("Verdana", Font.PLAIN, 12));
		put("Text Font", font);
		put("Text Disabled Font", font);

		put("Text Horizontal Alignment Integer", UIConstants.LEFT);
		put("Text Vertical Alignment Integer", UIConstants.CENTER);
		put("Text Insets", new Insets(0, 0, 0, 0));
		put("Text Vertical Space Integer", new Integer(1));
	}

	public String UIName() { return "Label"; }
	public String[] UIDescription() {
		return new String[] {
			"Label", "Label Disabled"
		};
	}

	public BufferedImage[] createUI(TComponent component, int w, int h) {
		BufferedImage[] ui = new BufferedImage[2];

		String[] bgColor = new String[] {
			"Background Color", "Background Disabled Color"
		};

		for (int i=0;i < ui.length;i++) {
			Color background = (Color) get(bgColor[i], component);

			// set ui image transparency based on background color
			ui[i] = (background != null) ?
					ImageUtil.createImage(w, h, Transparency.OPAQUE) :
					ImageUtil.createImage(w, h, Transparency.BITMASK);

			Graphics2D g = ui[i].createGraphics();

			// fill background
			if (background != null) {
				g.setColor(background);
				g.fillRect(0, 0, w, h);
			}

			// draw border
			Color border = (Color) get("Background Border Color", component);
			if (border != null) {
				g.setColor(border);
				g.drawRect(0, 0, w-1, h-1);
			}

			g.dispose();
		}

		return ui;
	}
	public void processUI(TComponent component, BufferedImage[] ui) {
		TLabel label = (TLabel) component;

		String[] color = new String[] { "Text Color", "Text Disabled Color" };
		String[] font = new String[] { "Text Font", "Text Disabled Font" };

		String[] document = GraphicsUtil.parseString(label.getText());
		for (int i=0;i < 2;i++) {
			Graphics2D g = ui[i].createGraphics();
			GraphicsUtil.drawString(g, document,
									label.getWidth(), label.getHeight(),
									(GameFont) get(font[i], component),
									(Color) get(color[i], component),
									(Integer) get("Text Horizontal Alignment Integer", component),
									(Integer) get("Text Vertical Alignment Integer", component),
									(Insets) get("Text Insets", component),
									(Integer) get("Text Vertical Space Integer", component));
			g.dispose();
		}
	}
	public void renderUI(Graphics2D g, int x, int y,
						 TComponent component, BufferedImage[] ui) {
		TLabel label = (TLabel) component;

		if (!label.isEnabled()) g.drawImage(ui[1], x, y, null);
		else g.drawImage(ui[0], x, y, null);
	}

}