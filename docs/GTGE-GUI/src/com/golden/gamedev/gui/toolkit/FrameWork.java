package com.golden.gamedev.gui.toolkit;

import java.awt.*;
import java.awt.event.*;

import com.golden.gamedev.engine.*;

import com.golden.gamedev.gui.TPane;
import com.golden.gamedev.gui.theme.basic.BasicTheme;

public class FrameWork {
	///////// NULL SINGLETON /////////
	public static final FrameWork NULL_FRAME = new FrameWork();

//	protected TInputEvent 	inputEvent = new TInputEvent();
	protected final BaseInput bsInput;

	private TContainer 		contentPane;
	private TToolTip		tooltip;
	private TComponent		modal;

	private TComponent 		hoverComponent;
	private TComponent 		selectedComponent;
	private TComponent[] 	clickComponent = new TComponent[3];

	private UITheme 		theme;

	public FrameWork(BaseInput input, int width, int height) {
		this.contentPane = new TPane(0, 0, width, height);
		this.bsInput = input;
		this.theme = new BasicTheme();

		tooltip = new TToolTip();
		contentPane.add(tooltip);

		setFrameWork(contentPane);
	}
	///////// null frame work /////////
	private FrameWork() {
		this.contentPane = new TPane(0, 0, 1, 1);
		this.bsInput = null;
		this.theme = new UITheme();

		setFrameWork(contentPane);
	}

	public void add(TComponent comp) {
		contentPane.add(comp);
		processMouseMotionEvent();
	}
	public int remove(TComponent comp) {
		int removed = removeComponent(contentPane, comp);
		if (removed != -1) processMouseMotionEvent();

		return removed;
	}
	private int removeComponent(TContainer container, TComponent comp) {
		int removed = container.remove(comp);
		TComponent[] components = container.getComponents();
		int i = 0;
		while (removed == -1 && i < components.length-1) {
			if (components[i].isContainer()) {
				removed = removeComponent((TContainer) components[i], comp);
			}
			i++;
		}

		return removed;
	}

	public void update() {
		if (!contentPane.isVisible()) return;

		processEvents();

		// update all components!
		contentPane.update();
	}
	public void render(Graphics2D g) {
		contentPane.render(g);
	}

	///////// events /////////
	private void processEvents() {
		///////// mouse motion event /////////
		processMouseMotionEvent();

		///////// mouse event /////////
		if (hoverComponent != null && hoverComponent.isEnabled()) {
			processMouseEvent();
		}

		///////// key event /////////
		if (selectedComponent != null && selectedComponent.isEnabled()) {
			processKeyEvent();
		}
	}
	///////// mouse motion event /////////
	private void processMouseMotionEvent() {
		if ((hoverComponent != null && hoverComponent.isEnabled()) &&
			(bsInput.isMouseDown(MouseEvent.BUTTON1) ||
			 bsInput.isMouseDown(MouseEvent.BUTTON2) ||
		 	 bsInput.isMouseDown(MouseEvent.BUTTON3))) {

			if (bsInput.getMouseDX() != 0 || bsInput.getMouseDY() != 0) {
				hoverComponent.processMouseDragged();
			}

		} else {
			// find component at current mouse coordinates
			TComponent comp = findComponent(bsInput.getMouseX(),
											bsInput.getMouseY());

			if (comp != null) {
			    if (bsInput.getMouseDX() != 0 || bsInput.getMouseDY() != 0) {
					comp.processMouseMoved();
					tooltip.dismiss = 0; // refresh tooltip, so the tooltip will
										 // always visible if the mouse keep moving
				}

				if (hoverComponent == null) {
					tooltip.setToolTipComponent(comp);
					comp.processMouseEntered();

				} else if (comp != hoverComponent) {
					tooltip.setToolTipComponent(comp);
					hoverComponent.processMouseExited();
					comp.processMouseEntered();
				}

			} else { // no hover component right now
				tooltip.setToolTipComponent(null);
				if (hoverComponent != null) {
					hoverComponent.processMouseExited();
				}
			}

			// set component as the new hover component
			hoverComponent = comp;
		}
	}
	///////// mouse event /////////
	private void processMouseEvent() {
		int pressed = bsInput.getMousePressed(),
			released = bsInput.getMouseReleased();

		if (pressed != BaseInput.NO_BUTTON) {
			tooltip.setToolTipComponent(null);
			tooltip.reshow = 0;
			tooltip.initial = 0;
			hoverComponent.processMousePressed();

			clickComponent[pressed - 1] = hoverComponent;

			// if mouse button 1 pressed,
			// sets hover component as selected component
			if (hoverComponent.isFocusable())
				if (pressed == MouseEvent.BUTTON1 &&
					hoverComponent != selectedComponent)
					selectComponent(hoverComponent);
		}

		if (released != BaseInput.NO_BUTTON) {
			hoverComponent.processMouseReleased();

			// mouse pressed == mouse released
			// process mouse click
			if (clickComponent[released - 1] == hoverComponent) {
				hoverComponent.processMouseClicked();
			}
		}
	}
	///////// key event /////////
	private void processKeyEvent() {
		if (bsInput.getKeyPressed() != BaseInput.NO_KEY)
			selectedComponent.keyPressed();

		if (bsInput.getKeyReleased() != BaseInput.NO_KEY &&
			selectedComponent != null)
			selectedComponent.processKeyReleased();
	}


	///////// member methods /////////
	private TComponent findComponent(int x, int y) {
		if (modal != null && !modal.isContainer()) {
			// when there's a modal, and the modal not container
			// return immediately
			return null;
		}

		// set the top container that hold all child components
		// contentpane or modal
		TContainer panel = (modal == null) ?
					       contentPane : ((TContainer) modal);
		TComponent comp = panel.findComponent(x,y);

		return comp;
//		return (comp == panel) ? null : comp;

//		return contentPane.findComponent(x, y);
	}

	public void clearFocus() {
		deselectComponent();
	}
	void deselectComponent() {
		if (selectedComponent == null) {
			return; // there's no selected component, nothing to proceed
		}

		selectedComponent.setSelected(false);
		selectedComponent = null;
	}
	boolean selectComponent(TComponent comp) {
		if (!comp.isVisible() ||      	// can not select invisible,
			!comp.isFocusable() ||		// unfocusable, and disable component
			!comp.isEnabled()) return false;

		// clear last selected component
		deselectComponent();

		// select component
		comp.setSelected(true);
		selectedComponent = comp;

		return true;
	}

	void setFrameWork(TComponent comp) {
		if (comp.isContainer()) {
			TComponent[] child = ((TContainer) comp).getComponents();
		    for (int i=0;i < child.length;i++) {
				setFrameWork(child[i]);
			}
		}

		comp.setFrameWork(this);
	}

	void setComponentStat(TComponent comp, boolean active) {
		if (this == NULL_FRAME) return;

		if (active == false) { // component is set to non-active
			if (hoverComponent == comp) { // check for new hover component
				processMouseMotionEvent();
			}

			if (selectedComponent == comp) {
				deselectComponent();
			}

			for (int i=0;i < clickComponent.length;i++) {
				// check for clicked component
				if (clickComponent[i] == comp) {
					clickComponent[i] = null;
					break;
				}
			}

			// set to non-modal
			if (modal == comp) {
				modal = null;
			}

		} else {
			// check is this component is new hover component
			processMouseMotionEvent();
		}

		if (comp.isContainer()) {
			TComponent[] components = ((TContainer) comp).getComponents();
			int size = ((TContainer) comp).getComponentCount();
			for (int i=0;i < size;i++) {
				setComponentStat(components[i], active);
			}
		}
	}
	void clearComponentsStat(TComponent[] comp) {
		if (this == NULL_FRAME) return;

		boolean checkMouseMotion = false;
		for (int i=0;i < comp.length;i++) {
			if (hoverComponent == comp[i]) {
				checkMouseMotion = true;
			}

			if (selectedComponent == comp[i]) {
				deselectComponent();
			}

			for (int j=0;j < clickComponent.length;j++) {
				// check for clicked component
				if (clickComponent[j] == comp[i]) {
					clickComponent[j] = null;
					break;
				}
			}
		}

		if (checkMouseMotion) processMouseMotionEvent();
	}

	public void validateUI() {
		validateContainer(contentPane);
	}
	final void validateContainer(TContainer container) {
		if (container.UIResource().size() > 0) {
			container.createUI();
		}

		TComponent[] components = container.getComponents();
		int size = container.getComponentCount();
		for (int i=0;i < size;i++) {
			if (components[i].UIResource().size() > 0) {
				components[i].createUI();
			}
			if (components[i].isContainer()) {
				validateContainer((TContainer) components[i]);
			}
		}
	}

	///////// member variables /////////
	public int getWidth() { return contentPane.getWidth(); }
	public int getHeight() { return contentPane.getHeight(); }
	public void setSize(int w, int h) { contentPane.setSize(w, h); }

	public TContainer getContentPane() { return contentPane; }
	public void setContentPane(TContainer pane) {
		pane.setBounds(0, 0, getWidth(), getHeight());

		contentPane = pane;
		setFrameWork(contentPane);
	}

	public TComponent getHoverComponent() { return hoverComponent; }
	public TComponent getSelectedComponent() { return selectedComponent; }

	public TComponent getModal() { return modal; }
	public void setModal(TComponent comp) {
		if (comp != null && !comp.isVisible()) {
			throw new RuntimeException("Can't set invisible component as modal component!");
		}

		modal = comp;
	}

	/**
	 * Returns the latest inserted component into this frame work.
	 */
	public TComponent get() {
		return contentPane.get();
	}

	public UITheme getTheme() { return theme; }
	public void installTheme(UITheme newTheme) {
		UIRenderer[] ui = theme.getInstalledUI();
		for (int i=0;i < ui.length;i++) {
			if (newTheme.getUITheme(ui[i].UIName()) == null || ui[i].immutable) {
				// new theme doesn't have UI Renderer for specified UIName
				// or old theme use immutable renderers for specified UIName
				// therefore install from old theme
				newTheme.installUI(ui[i]);
			}
		}

		theme = newTheme;

		installTheme(contentPane);
	}
	private void installTheme(TComponent comp) {
		comp.setUIRenderer(theme.getUIRenderer(comp.UIName()));
		if (comp.isContainer()) {
			TComponent[] childs = ((TContainer) comp).getComponents();
			for (int i=0;i < childs.length;i++) {
				installTheme(childs[i]);
			}
		}
	}

	public TToolTip getToolTip() { return tooltip; }
	public void setToolTip(TToolTip tip) {
		contentPane.replace(tooltip, tip);
		tooltip = tip;
	}

	protected void finalize() throws Throwable {
		System.out.println("Finalization Frame Work = "+this);
		super.finalize();
	}

	public String toString() {
		if (this == NULL_FRAME) return "NULL FRAME WORK";
		return super.toString() + " " +
			"[width=" + getWidth() +
			", height=" + getHeight() + "]";
	}

}