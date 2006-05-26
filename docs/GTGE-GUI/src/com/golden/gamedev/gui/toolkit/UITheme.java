package com.golden.gamedev.gui.toolkit;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.object.font.SystemFont;

import com.golden.gamedev.gui.theme.basic.BasicTheme;

public class UITheme {
	private final UIRenderer NULL_RENDERER = new NullRenderer();

	private final Map UIRenderers = new HashMap();

	public UITheme() {
		installUI(NULL_RENDERER);
	}

	public String getName() { return "Null Theme"; }

	/**
	 * @return true, if the specified ui successfully installed
	 */
	public boolean installUI(UIRenderer ui) {
		if (ui != NULL_RENDERER) {
			UIRenderer renderer = getUIRenderer(ui.UIName());
			if (renderer != null &&
				renderer.immutable) {
				// immutable renderer can not be replaced
				return false;
			}
		}

		UIRenderers.put(ui.UIName(), ui);

		return true;
	}
	/**
	 * Install empty UI for specified component name.
	 *
	 * @return true, if the specified ui successfully installed
	 */
	public boolean installEmptyUI(String UIName) {
		return installUI(new EmptyRenderer(UIName));
	}

	public UIRenderer getUIRenderer(String UIName) {
		if (UIName == null) return NULL_RENDERER;

		UIRenderer renderer = null;
  		StringTokenizer st = new StringTokenizer(UIName, ".");
		if (st.countTokens() > 0) {
			String[] tokens = new String[st.countTokens()];
			for (int i=tokens.length-1;i >= 0;i--) {
				tokens[i] = st.nextToken();
			}

			int num = 0;
			while (renderer == null && num < tokens.length) {
				renderer = (UIRenderer) UIRenderers.get(tokens[num]);
				num++;
			}
		}

		return (renderer != null) ? renderer : NULL_RENDERER;
	}
	UIRenderer getUITheme(String UIName) {
		UIRenderer renderer = null;
  		StringTokenizer st = new StringTokenizer(UIName, ".");
		if (st.countTokens() > 0) {
			String[] tokens = new String[st.countTokens()];
			for (int i=tokens.length-1;i >= 0;i--) {
				tokens[i] = st.nextToken();
			}

			int num = 0;
			while (renderer == null && num < tokens.length) {
				renderer = (UIRenderer) UIRenderers.get(tokens[num]);
				num++;
			}
		}

		return renderer;
	}


	public UIRenderer[] getInstalledUI() {
		return (UIRenderer[]) UIRenderers.values().toArray(new UIRenderer[0]);
	}
	public void printInstalledUI() {
		UIRenderer[] renderers = getInstalledUI();
		System.out.println("Installed UI in " + this);
		System.out.println("=========================");
		for (int i=0;i < renderers.length;i++) {
			if (renderers[i] != NULL_RENDERER) {
				System.out.println((renderers[i].immutable) ?
					renderers[i].UIName() + " -> " + renderers[i] + " (immutable)" :
					renderers[i].UIName() + " -> " + renderers[i]
				);

			}
		}
		System.out.println("=========================");
	}

	public String toString() {
		return super.toString() + " " +
			"[name=" + getName() +
			", total renderer=" + (UIRenderers.size()-1) + "]";
	}

}


///////// NULL RENDERER for Unregistered Component /////////
class NullRenderer extends UIRenderer {
	private GameFont font = new SystemFont(new Font("Verdana", Font.BOLD, 11), Color.WHITE);

	public String UIName() { return "Invalid Component"; }
	public String[] UIDescription() {
		return new String[] {
			"Invalid Component"
		};
	}

	public BufferedImage[] createUI(TComponent component, int w, int h) {
		BufferedImage[] ui = GraphicsUtil.createImage(1, w, h, Transparency.OPAQUE);

		Graphics2D g = ui[0].createGraphics();

		g.setColor(Color.GRAY);
		g.fill3DRect(0, 0, w-1, h-1, true);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, w-1, h-1);

		g.dispose();

		return ui;
	}
	public void processUI(TComponent component, BufferedImage[] ui) {
	}
	public void renderUI(Graphics2D g, int x, int y, TComponent component,
						 BufferedImage[] ui) {
		g.drawImage(ui[0], x, y, null);

		String[] text = new String[] {
							"UI Factory can not find",
							"UI Renderer for",
							"\"" + component.UIName() + "\"",
							"",
							"Please register one"
						};

		int posy = y + 5;
		for (int i=0;i < text.length;i++) {
			font.drawString(g, text[i], GameFont.CENTER, x, posy, component.getWidth());
			posy += font.getHeight();
		}
	}

	public String toString() {
		return "[INVALID UI]";
	}
}

///////// empty renderer, draw nothing /////////
class EmptyRenderer extends UIRenderer {
	private String 			name;
	private BufferedImage[] ui = new BufferedImage[0];

	public EmptyRenderer(String name) {
		this.name = name;
	}

	public String UIName() { return name; }
	public String[] UIDescription() {
		return new String[] { "Empty Component" };
	}

	public BufferedImage[] createUI(TComponent component, int w, int h) {
		return ui;
	}
	public void processUI(TComponent component, BufferedImage[] ui) { }
	public void renderUI(Graphics2D g, int x, int y,
						 TComponent component, BufferedImage[] ui) {
	}

	public String toString() {
		return "[EMPTY-BLANK UI]";
	}
}