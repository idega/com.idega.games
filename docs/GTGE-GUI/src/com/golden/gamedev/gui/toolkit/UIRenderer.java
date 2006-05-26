package com.golden.gamedev.gui.toolkit;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Component UI Renderer.
 */
public abstract class UIRenderer {
	private final Map UIResource = new java.util.LinkedHashMap();
	/**
	 * true, determines that this renderer can not be replaced by
	 * other UI Renderer in a UI Theme.
	 * Immutable renderer only can be replaced by
	 * other immutable UI Renderer too.
	 */
	public boolean immutable = false;

	public UIRenderer() {
	}


	/**
	 * This renderer will only render components with name equal
	 * with this renderer name.
	 *
	 * @see TComponent#UIName()
	 */
	public abstract String UIName();
	/**
	 * Description of UI created by this renderer.
	 * Component that used external UI must follow this UI description for
	 * proper rendering.
	 *
	 * @see TComponent#setExternalUI(BufferedImage[], boolean)
	 */
	public abstract String[] UIDescription();


	/**
	 * Creates component UI with specified size.
	 */
	public abstract BufferedImage[] createUI(TComponent component,
											 int w, int h);
	/**
	 * Process component UI.
	 */
	public abstract void processUI(TComponent component, BufferedImage[] ui);
	/**
	 * Renders component UI to specified location.
	 */
	public abstract void renderUI(Graphics2D g, int x, int y,
						 		  TComponent component,
						 		  BufferedImage[] ui);


	/**
	 * Returns UI Resource of specified key from this UI Renderer
	 * with specified component as object component.
	 */
	public Object get(String key, TComponent component) {
		if (component == null) return UIResource.get(key);

		return (component.UIResource().containsKey(key)) ?
				component.UIResource().get(key) : UIResource.get(key);
	}
	/**
	 * Inserts UI Resource to this UI Renderer.
	 */
	public void put(String key, Object value) {
		UIResource.put(key, value);
	}

	/**
	 * Removes installed UI Resource from this UI Renderer.
	 * Use this when override UI Renderer and the resource is not valid anymore.
	 */
	protected final Object remove(String key) {
		return UIResource.remove(key);
	}


	///////// this renderer UI Resource /////////
	/**
	 * Returns UI Resource mapping of this renderer.
	 */
	public final String[] UIResource() {
		String[] temp = new String[UIResource.size()];
		String[] keys = (String[]) UIResource.keySet().toArray(temp);

		return keys;
	}

	/**
	 * Prints UI Resource mapping to console output
	 * of the specified component UI Resource.
	 */
	public final void printUIResource(TComponent component) {
		String[] keys = UIResource();
		String prefix = "?", suffix = "?";
		System.out.println(UIName() + " Component UI Resource");
		System.out.println("UI Renderer: " + this);
		System.out.println("Prefix [C] for custom component resource");
		System.out.println("=============================================");
		for (int i=0;i < keys.length;i++) {
			if (!keys[i].startsWith(prefix) ||
				!keys[i].endsWith(suffix)) {

				StringTokenizer token = new StringTokenizer(keys[i]);
				prefix = token.nextToken();
				if (!token.hasMoreTokens()) {
					suffix = prefix;
				} else {
					while (token.hasMoreTokens()) {
						suffix = token.nextToken();
					}
				}

				// draw divider
				if (i != 0) System.out.println("---------------------");
			}

			// print ui resource, draw prefix [C] for changed resource
			if (component != null &&
				component.UIResource().containsKey(keys[i])) {
				System.out.print("[C] ");
			}
			System.out.println(keys[i] + " -> " + get(keys[i], component));
		}
		System.out.println("=============================================");
		System.out.println();
	}
	/**
	 * Prints UI Resource mapping to console output.
	 */
	public final void printUIResource() {
		printUIResource(null);
	}

	/**
	 * Prints UI Description to console output.
	 */
	public final void printUIDescription() {
		String[] desc = UIDescription();
		System.out.println(UIName() + " Component UI Description" +
						   " (" + desc.length + ")");
		for (int i=0;i < desc.length;i++) {
			System.out.println(i + " " + desc[i]);
		}
	}

	public String toString() {
		return super.toString() + " " +
			"[UIName=" + UIName() +
			", immutable=" + immutable + "]";
	}

}