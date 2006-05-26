package com.golden.gamedev.gui.theme.basic;

import java.awt.*;
import java.awt.image.BufferedImage;

import com.golden.gamedev.gui.toolkit.*;

import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.object.font.SystemFont;

public class BToolTipRenderer extends UIRenderer {

	public BToolTipRenderer() {
		put("Background Color", new Color(255, 255, 231));
		put("Background Border Color", Color.BLACK);

		put("Text Color", Color.BLACK);

		put("Text Font", new SystemFont(new Font("Verdana", Font.PLAIN, 11)));

		put("Text Insets", new Insets(3, 4, 4, 4));
		put("Text Vertical Space Integer", new Integer(1));
	}

	public String UIName() { return "ToolTip"; }
	public String[] UIDescription() {
		return new String[] {
			"ToolTip"
		};
	}

	public BufferedImage[] createUI(TComponent component, int w, int h) {
		TComponent tooltip = ((TToolTip) component).getToolTipComponent();
		if (tooltip == null) return null;

		String tipText = tooltip.getToolTipText();
		String[] document = GraphicsUtil.parseString(tipText);

		GameFont font = (GameFont) get("Text Font", component);
		Insets inset = (Insets) get("Text Insets", component);
		int space = ((Integer) get("Text Vertical Space Integer", component)).intValue();
		int width = 0;
		for (int i=0;i < document.length;i++) {
			w = font.getWidth(document[i]) + inset.left + inset.right;
			if (w > width) width = w;
		}
		int height = (document.length * (font.getHeight()+space)) - space +
					 inset.top + inset.bottom;


		BufferedImage[] ui = GraphicsUtil.createImage(1, width, height, Transparency.OPAQUE);
		Graphics2D g = ui[0].createGraphics();

		g.setColor((Color) get("Background Color", component));
		g.fillRect(1, 1, width-2, height-2);
		Color borderColor = (Color) get("Background Border Color", component);
		if (borderColor != null) {
			g.setColor(borderColor);
			g.drawRect(0, 0, width-1, height-1);
		}

		g.dispose();

		return ui;
	}
	public void processUI(TComponent component, BufferedImage[] ui) {
		TComponent tooltip = ((TToolTip) component).getToolTipComponent();
		if (tooltip == null) return;

		String tipText = tooltip.getToolTipText();

		Graphics2D g = ui[0].createGraphics();
		GraphicsUtil.drawString(g, GraphicsUtil.parseString(tipText),
							    ui[0].getWidth(), ui[0].getHeight(),
								(GameFont) get("Text Font", component),
								(Color) get("Text Color", component),
								UIConstants.LEFT, UIConstants.TOP,
								(Insets) get("Text Insets", component),
								(Integer) get("Text Vertical Space Integer", component));
		g.dispose();
	}
	public void renderUI(Graphics2D g, int x, int y,
						 TComponent component, BufferedImage[] ui) {
		g.drawImage(ui[0], x, y, null);
	}

}