package com.golden.gamedev.gui.theme.basic;

import java.awt.*;
import java.awt.image.*;

import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.object.font.SystemFont;

import com.golden.gamedev.gui.TButton;
import com.golden.gamedev.gui.toolkit.*;

public class BButtonRenderer extends UIRenderer {

	public BButtonRenderer() {
		put("Background Color", Color.LIGHT_GRAY);
		put("Background Over Color", Color.CYAN);
		put("Background Pressed Color", Color.LIGHT_GRAY);
		put("Background Border Color", Color.BLACK);
		put("Background Disabled Color", Color.GRAY);

		put("Text Color", Color.BLACK);
		put("Text Over Color", Color.BLACK);
		put("Text Pressed Color", Color.BLACK);
		put("Text Disabled Color", Color.BLACK);

		GameFont font = new SystemFont(new Font("Dialog", Font.BOLD, 15));
		put("Text Font", font);
		put("Text Over Font", font);
		put("Text Pressed Font", font);
		put("Text Disabled Font", font);

		put("Text Horizontal Alignment Integer", UIConstants.CENTER);
		put("Text Vertical Alignment Integer", UIConstants.CENTER);
		put("Text Insets", new Insets(5, 5, 5, 5));
		put("Text Vertical Space Integer", new Integer(1));
	}

	public String UIName() { return "Button"; }
	public String[] UIDescription() {
		return new String[] {
			"Button", "Button Over", "Button Pressed", "Button Disabled"
		};
	}

	public BufferedImage[] createUI(TComponent component, int w, int h) {
		BufferedImage[] ui = GraphicsUtil.createImage(4, w, h, Transparency.OPAQUE);

		String[] color = new String[] {
			"Background Color", "Background Over Color",
			"Background Pressed Color", "Background Disabled Color"
		};

		Color borderColor = (Color) get("Background Border Color", component);
		for (int i=0;i < 4;i++) {
			Graphics2D g = ui[i].createGraphics();
			g.setColor((Color) get(color[i], component));
			switch (i) {
				case 0: g.fill3DRect(0, 0, w-1, h-1, true); break;
				case 1: g.fillRect(0, 0, w-1, h-1); break;
				case 2: g.fill3DRect(0, 0, w-1, h-1, false); break;
				case 3: g.fill3DRect(0, 0, w-1, h-1, true); break;
			}
			if (borderColor != null) {
				g.setColor(borderColor);
				g.drawRect(0, 0, w-1, h-1);
			}
			g.dispose();
		}

		return ui;
	}
	public void processUI(TComponent component, BufferedImage[] ui) {
		TButton button = (TButton) component;

		String[] color = new String[] {
			"Text Color", "Text Over Color",
			"Text Pressed Color", "Text Disabled Color"
		};
		String[] font = new String[] {
			"Text Font", "Text Over Font",
			"Text Pressed Font", "Text Disabled Font"
		};

		String[] document = GraphicsUtil.parseString(button.getText());
		for (int i=0;i < 4;i++) {
			Graphics2D g = ui[i].createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							   RenderingHints.VALUE_ANTIALIAS_ON);
			GraphicsUtil.drawString(g, document,
									button.getWidth(), button.getHeight(),
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
		TButton button = (TButton) component;

		if (!button.isEnabled()) g.drawImage(ui[3], x, y, null);
		else if (button.isMousePressed()) g.drawImage(ui[2], x, y, null);
		else if (button.isMouseOver()) g.drawImage(ui[1], x, y, null);
		else g.drawImage(ui[0], x, y, null);
	}

}