import java.awt.*;
import java.awt.image.*;

import com.golden.gamedev.util.Utility;
import com.golden.gamedev.object.font.SystemFont;

import com.golden.gamedev.gui.toolkit.*;
import com.golden.gamedev.gui.theme.basic.*;

public class TestTheme extends BasicTheme {

	public TestTheme() {
		installUI(new TestButtonRenderer());
		installUI(new TestTitleBarRenderer());

		getUIRenderer("TitleBar").put("Text Font", new SystemFont(new Font("Verdana", Font.BOLD, 14)));

		getUIRenderer("TitleBarButton").put("Background Color", Color.ORANGE);
	}

	public String getName() { return "Test Theme"; }

}

class TestButtonRenderer extends BButtonRenderer {

	public TestButtonRenderer() {
		put("Background Color", Color.ORANGE.darker());
		put("Background Over Color", Color.ORANGE);
		put("Background Pressed Color", Color.GREEN);
		put("Background Disabled Color", Color.GREEN.darker());
		put("Border Stroke", new BasicStroke(2));
	}

	public BufferedImage[] createUI(TComponent component, int w, int h) {
		BufferedImage[] ui = GraphicsUtil.createImage(4, w, h, Transparency.BITMASK);

		String[] color = new String[] {
			"Background Color", "Background Over Color",
			"Background Pressed Color", "Background Disabled Color"
		};

		Color borderColor = (Color) get("Background Border Color", component);
		for (int i=0;i < 4;i++) {
			Graphics2D g = ui[i].createGraphics();

			g.setColor((Color) get(color[i], component));
			switch (i) {
				case 0: g.fillRoundRect(0, 0, w-1, h-1, 10, 30); break;
				case 1: g.fillRoundRect(0, 0, w-1, h-1, 10, 30); break;
				case 2: g.fillRoundRect(0, 0, w-1, h-1, 10, 30); break;
				case 3: g.fillRoundRect(0, 0, w-1, h-1, 10, 30); break;
			}
			if (borderColor != null) {
				g.setColor(borderColor);
				g.setStroke((Stroke) get("Border Stroke", component));
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
								   RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_RENDERING,
								   RenderingHints.VALUE_RENDER_QUALITY);
				g.drawRoundRect(0, 0, w-1, h-1, 10, 30);
			}

			g.dispose();
		}

		return ui;
	}

}

class TestTitleBarRenderer extends BTitleBarRenderer {

	public TestTitleBarRenderer() {
		// remove unused fields from previous renderer
		remove("Background Color");
		remove("Background Border Color");
		remove("Background Disabled Color");

		// now we use texture renderer ;)
		BufferedImage[] texture = GraphicsUtil.createTexture(getClass(),
							  	  "images/titlebar.png", Transparency.OPAQUE);
		put("Texture Images", texture);
	}

	public String[] UIDescription() {
		return new String[] { "TitleBar" };
	}

	public BufferedImage[] createUI(TComponent component, int w, int h) {
		BufferedImage[] ui = GraphicsUtil.createImage(1, w, h, Transparency.OPAQUE);

		for (int i=0;i < ui.length;i++) {
			Graphics2D g = ui[i].createGraphics();

			BufferedImage[] texture = (BufferedImage[]) get("Texture Images", component);
			GraphicsUtil.renderTexture(g, texture, w, h);

			g.dispose();
		}

		return ui;
	}

}