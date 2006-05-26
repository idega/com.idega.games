package com.golden.gamedev.gui.toolkit;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.util.Map;

import com.golden.gamedev.engine.BaseInput;
import com.golden.gamedev.util.ImageUtil;

public abstract class TComponent {
	protected FrameWork 		frame = FrameWork.NULL_FRAME;
	protected BaseInput 		bsInput;
	private TContainer 			parent;		// parent container

	///////// rendering variables /////////
	private UIRenderer			renderer;
	protected BufferedImage[] 	ui;
	private BufferedImage[]		externalUI;
	private boolean				processUI;
	protected final Map 		UIResource = new java.util.HashMap();

	/**
	 * true, component is taken all the responsibility of its own rendering.
	 *
	 * @see #renderCustomUI(Graphics2D,int,int,int,int).
	 */
	public boolean				customRendering;

	///////// member variables /////////
	private int 		x, y, width, height;	// position, size
	private int 		screenX, screenY;		// screen position
	private int 		layer = 0;				// default layer, the lowest layer
	private String 		tooltip;				// tooltip text
	private TComponent	tooltipParent;			// tooltip parent

	/////// flags ////////
	private boolean visible		= true;
	private boolean enabled     = true;

	private boolean focusable   = true;			// only focusable component can be selected
	private boolean selected    = false;

	public TComponent(int x, int y, int w, int h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;

		if (width == 0) width = 1;
		if (height == 0) height = 1;
	}

	/**
	 * Returns true if this component is a container.
	 * If component is a container,
	 * some method will also check its child components.
	 * Reduce the use of instanceof, because instanceof is a 'heavy' method.
	 */
	public boolean isContainer() { return false; }

	///////// main methods /////////
	/** Updates component state. */
	public void update() { }

	/** Renders component to specified graphics context. */
	public void render(Graphics2D g) {
		if (!visible) return;

		try {
			if (customRendering) {
				renderCustomUI(g, screenX, screenY, width, height);

			} else {
				renderer.renderUI(g, screenX, screenY, this, ui);

			}
		} catch(Exception e) {
			System.out.println("error rendering = "+this);
			e.printStackTrace(); System.exit(0);
		}
	}
	/**
	 * When component {@link #customRendering} is set to true,
	 * component rendering is done within this method.
	 * The implementation of this method provided by
	 * TComponent class does nothing.
	 */
	protected void renderCustomUI(Graphics2D g, int x, int y, int w, int h) { }

	/**
	 * Tests whether this component bounds intersects with specified point,
	 * where the point are defined as screen coordinate.
	 */
	public boolean intersects(int x1, int y1) {
		return (visible) &&
			   (x1 >= screenX && x1 <= screenX + width &&
				y1 >= screenY && y1 <= screenY + height);
	}
	/**
	 * Tests whether this component intersects with specified component.
	 */
	public boolean intersects(TComponent comp) {
		return (visible) && (comp.isVisible()) &&
        	   (screenX + width >= comp.screenX &&
			    screenX <= comp.screenX + comp.width &&
				screenY + height >= comp.screenY &&
				screenY <= comp.screenY + comp.height);
	}

	/**
	 * Destroys this component.
	 */
	public void dispose() {
		frame.setComponentStat(this, false);
		if (parent != null) {
			parent.remove(this);
		}

		frame = FrameWork.NULL_FRAME;
		bsInput = null;
		parent = null;

		ui = null;
		renderer = null;

		selected = false;
	}

	///////// flags /////////
	/** Returns true if this component is visible on screen. */
	public boolean isVisible() { return visible; }
	/** Shows or hides this component. */
    public void setVisible(boolean b) {
		if (visible == b)
			return; // no visibility state changed, no need further process

		visible = b;
		frame.setComponentStat(this, visible);
	}

	/**
	 * Returns true if this component is enabled.
	 * An enabled component can receive user input.
	 */
	public boolean isEnabled() {
		return (parent == null) ? enabled : (enabled && parent.isEnabled());
	}
	/**
	 * Enables or disables this component.
	 * An enabled component can receive user input.
	 */
	public void setEnabled(boolean b) {
		if (enabled == b) {
			return; // no enabled state changed, no need further process
		}

		enabled = b;
		frame.setComponentStat(this, enabled);
	}

	/** Returns true if this component is selected (the focus owner). */
	public boolean isSelected() { return selected; }
	final void setSelected(boolean b) { selected = b; }

	/**
	 * Requests that this component get the input focus.
	 *
	 * @return false if the request is guaranteed to fail
	 */
	public boolean requestFocus() { return frame.selectComponent(this); }
	/**
	 * Transfers the focus to the next component,
	 * as though this component were the focus owner.
	 */
	public void transferFocus() {
		if (isSelected() && parent != null) {
			parent.transferFocus(this);
		}
	}
	/**
	 * Transfers the focus to the previous component,
	 * as though this component were the focus owner.
	 */
	public void transferFocusBackward() {
		if (isSelected() && parent != null) {
			parent.transferFocusBackward(this);
		}
	}

	/** Returns whether this component can be focused (selected). */
	public boolean isFocusable() { return focusable; }
	/** Sets the focusable state of this component to the specified value. */
	public void setFocusable(boolean b) { focusable = b; }

	/** Returns the parent container of this component. */
	public TContainer getContainer() { return parent; }
	final void setContainer(TContainer container) {
		parent = container;

		validatePosition();
	}

	final void setFrameWork(FrameWork frame) {
		if (this.frame == frame) return;

		this.frame = frame;
		this.bsInput = frame.bsInput;

		if (renderer == null) {
			renderer = frame.getTheme().getUIRenderer(UIName());
		}

		createUI();
	}

	///////// member variables /////////
	public void setBounds(int x, int y, int width, int height) {
		if (this.x != x || this.y != y) {
			this.x = x;
			this.y = y;
			validatePosition();
		}

		if (this.width != width || this.height != height) {
			this.width = width;
			this.height = height;
			if (width == 0) width = 1;
			if (height == 0) height = 1;

			createUI();
			validateSize();
		}
	}
	public void setLocation(int x, int y) {
		if (this.x != x || this.y != y) {
			this.x = x;
			this.y = y;

			validatePosition();
		}
	}
	public void move(int dx, int dy) {
		if (dx != 0 || dy != 0) {
			x += dx; y += dy;
			validatePosition();
		}
	}
	public void setSize(int w, int h) {
		if (width != w || height != h) {
			width = w; height = h;
			if (width == 0) width = 1;
			if (height == 0) height = 1;

			createUI();
			validateSize();
		}
	}
	protected void validateSize() { }
	protected void validatePosition() {
		screenX = (parent == null) ? x : x + parent.getScreenX();
		screenY = (parent == null) ? y : y + parent.getScreenY();
	}

	/** Returns component x-axis coordinate. */
	public int getX() { return x; }
	/** Returns component y-axis coordinate. */
	public int getY() { return y; }
	/** Returns component screen x-axis coordinate. */
	public int getScreenX() { return screenX; }
	/** Returns component screen y-axis coordinate. */
	public int getScreenY() { return screenY; }
	/** Returns the width of this component. */
	public int getWidth() { return width; }
	/** Returns the height of this component. */
	public int getHeight() { return height; }

	public int getLayer() { return layer; }
	public void setLayer(int layer) { this.layer = layer; }

	/** Returns component tooltip string. */
	public String getToolTipText() { return tooltip; }
	/**
	 * Registers the text as this component tool tip.
	 * The text displays when the cursor lingers over the component.
	 */
	public void setToolTipText(String text) { tooltip = text; }

	public TComponent getToolTipParent() { return tooltipParent; }
	public void setToolTipParent(TComponent tipParent) {
		tooltipParent = tipParent;
	}


	///////// events /////////
	///////// key event /////////
	protected void processKeyPressed() { }
	protected void processKeyReleased() { }

	///////// mouse event /////////
	protected void processMousePressed() { }
	protected void processMouseReleased() { }
	protected void processMouseClicked() { }

	///////// mouse motion event /////////
	protected void processMouseMoved() { }
	protected void processMouseDragged() { }
	protected void processMouseEntered() { }
	protected void processMouseExited() { }

	///////// custom events /////////
	void keyPressed() {
		checkFocusKey();
		processKeyPressed();
	}
	protected void checkFocusKey() {
		if (bsInput.getKeyPressed() == KeyEvent.VK_TAB) {
			if (bsInput.isKeyDown(KeyEvent.VK_SHIFT) == false)
				transferFocus();
				else transferFocusBackward();
		}
	}
//	void mouseDragged() {
//		if (dragable && bsInput.isMouseDown(MouseEvent.BUTTON1)) {
//			// move dragable component
//			if (parent != null) {
//				parent.sendToFront(this);
//			}
//			move(bsInput.getMouseDX(), bsInput.getMouseDY());
//		}
//
//		processMouseDragged();
//	}

//	protected void finalize() throws Throwable {
//		System.out.println("finalizing "+this);
//		super.finalize();
//	}

	public String toString() {
		return super.toString() + " " +
			"[UIName=" + UIName() +
			", bounds=" + x + "," + y + "," + width + "," + height + "]";
	}

	///////// various ui rendering function /////////
	public BufferedImage[] getExternalUI() { return externalUI; }
	public void setExternalUI(BufferedImage[] externalUI, boolean processUI) {
		if (externalUI != null) {
			this.width = externalUI[0].getWidth();
			this.height = externalUI[0].getHeight();
		}

		this.externalUI = externalUI;
		this.processUI = processUI;

		createUI();
	}

	/** Returns customized UI Resource used by this component. */
	public Map UIResource() { return UIResource; }

	/** Returns the UI Renderer object that renders this component. */
	public UIRenderer getUIRenderer() { return renderer; }
	/** Sets the UI Renderer object that renders this component. */
	public void setUIRenderer(UIRenderer renderer) {
		this.renderer = renderer;

		createUI();
	}

	/**
	 * Component UI Name,
	 * UI Factory used this name to poll this component UI Renderer.
	 *
	 * @return Component UI Name
	 */
	public abstract String UIName();

	/**
	 * Creates component UI, this method adjust
	 * the UI creation depends on component rendering technique. <p>
	 *
	 * There are three rendering technique :
	 *
	 * First, if this component is responsible of its own rendering
	 * ({@link #customRendering} = true), the UI creation will be handled by
	 * {@link #createCustomUI(int, int)} and
	 * processed by {@link #processCustomUI()}. <p>
	 *
	 * If not then it will checking the possibility of using
	 * {@linkplain #getExternalUI() external UI}. <p>
	 *
	 * If all fail then this component will use its default
	 * {@linkplain #getUIRenderer() Component UI Renderer}.
	 *
	 * @see #customRendering
	 * @see #createCustomUI(int, int)
	 * @see #setExternalUI(BufferedImage[], boolean)
	 */
	protected void createUI() {
		if (frame == FrameWork.NULL_FRAME) return;

		if (customRendering) {
			createCustomUI(width, height);
			processCustomUI();

		} else if (externalUI != null &&
				   renderer.UIDescription().length == externalUI.length) {
			// using external ui
			// validate size
			if (width != externalUI[0].getWidth() ||
				height != externalUI[0].getHeight()) {
				System.err.print(
					this + "\n" +
					"Illegal Operation! " +
					"Can not change component size when using external UI\n"+
					"size ("+width+","+height+") -> "
				);
				width = externalUI[0].getWidth();
				height = externalUI[0].getHeight();
				System.err.println("("+width+","+height+")");
			}

			if (!processUI) {
				ui = externalUI;

			} else {
				// external ui need further process
				// clone and process the ui
				ui = new BufferedImage[externalUI.length];
				for (int i=0;i < ui.length;i++) {
					ui[i] = ImageUtil.createImage(externalUI[i].getWidth(),
												  externalUI[i].getHeight(),
												  externalUI[i].getColorModel().getTransparency());
					Graphics2D g = ui[i].createGraphics();
					g.setComposite(AlphaComposite.Src);
					g.drawImage(externalUI[i], 0, 0, null);
					g.dispose();
				}

				processExternalUI();
			}

		} else {
			// using ui renderer
			createRenderedUI(width, height);
			processRenderedUI();
		}
	}
	/**
	 * Creates component UI by component
	 * {@linkplain #getUIRenderer() UI Renderer}.
	 */
	protected void createRenderedUI(int w, int h) {
		ui = renderer.createUI(this, w, h);
	}
	/**
	 * Process component UI by component
	 * {@linkplain #getUIRenderer() UI Renderer}.
	 */
	protected void processRenderedUI() {
		renderer.processUI(this, ui);
	}

	/**
	 * When component {@link #customRendering} is set to true,
	 * component UI is created in this method. <p>
	 * The implementation of this method provided by
	 * TComponent class does nothing.
	 */
	protected void createCustomUI(int w, int h) { }
	/**
	 * Process component UI when
	 * component {@link #customRendering} is set to true. <p>
	 * The implementation of this method provided by
	 * TComponent class does nothing.
	 */
	protected void processCustomUI() { }

	/**
	 * External UI that needed further process is processed within this method,
	 * by default external UI is processed by component
	 * {@linkplain #getUIRenderer() UI Renderer}.
	 */
	protected void processExternalUI() {
		renderer.processUI(this, ui);
	}

	public void validateUI() {
		createUI();
	}

	public void printUIResource() {
		if (renderer != null) {
			renderer.printUIResource(this);

		} else {
			String[] temp = new String[UIResource.size()];
			String[] keys = (String[]) UIResource.keySet().toArray(temp);

			System.out.println(UIName() + " Component UI Resource");
			System.out.println("UI Renderer has not been set!!");
			System.out.println("Before print component UI Resource, " +
							   "please insert this component " +
							   "into the FrameWork to set its UI Renderer");
			System.out.println("=============================================");
			for (int i=0;i < keys.length;i++) {
				System.out.println(keys[i] + " -> " + UIResource.get(keys[i]));
			}
			System.out.println("=============================================");
			System.out.println();
		}
	}

}