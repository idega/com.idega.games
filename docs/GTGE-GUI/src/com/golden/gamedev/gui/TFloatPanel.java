package com.golden.gamedev.gui;

import java.awt.*;
import java.awt.image.BufferedImage;

import com.golden.gamedev.gui.toolkit.*;

public class TFloatPanel extends TContainer {

	private TTitleBar 	titleBar;
	private TContainer	contentPane;
	private boolean		icon;

	private int			distance = -2;

	public TFloatPanel(String title, boolean closable, boolean iconifiable,
					   int x, int y, int w, int h) {
		super(x,y,w,h);

		titleBar = new TTitleBar(title, closable, iconifiable,
								 0, 0, w, 25);
		contentPane = new TPanel(0, 0, w, h-25-distance);

		addAccessory(titleBar);
		addAccessory(contentPane);
		relayout();

		setElastic(true);
		setLayer(100);
	}

	public void add(TComponent comp) { contentPane.add(comp); }
	public void addAccessory(TComponent comp) { super.add(comp); }

	public String getTitle() { return titleBar.getTitle(); }
	public void setTitle(String title) {
		titleBar.setTitle(title);
	}

	public void relayout() {
		titleBar.setLocation(0, 0);
		contentPane.setLocation(0, titleBar.getHeight()+distance);
		setSize(contentPane.getWidth(),
				titleBar.getHeight()+distance+contentPane.getHeight());
	}
	protected void validateSize() {
		super.validateSize();
		titleBar.setSize(getWidth(), titleBar.getHeight());

		if (icon) {
			// iconified
			contentPane.setVisible(false);
		} else {
			// deiconified
			contentPane.setVisible(true);
			contentPane.setSize(getWidth(),
							    getHeight()-(titleBar.getHeight()+distance));
		}
	}

	public boolean isIcon() { return icon; }
	/**
	 * Iconifies or de-iconifies this float panel.
	 */
	public void setIcon(boolean b) {
		if (icon == b) return;

		icon = b;

		int w = getWidth(), h = titleBar.getHeight();
		if (!icon) h += contentPane.getHeight()+distance;

		setSize(w, h);

		titleBar.getIconifiedButton().setToolTipText((icon) ? "Restore" : "Minimize");
	}

	public int getPaneDistance() { return distance; }
	public void setPaneDistance(int i) { distance = i; relayout(); }

	public TTitleBar getTitleBar() { return titleBar; }
	public void setTitleBar(TTitleBar bar) {
		replace(titleBar, bar);
		titleBar = bar;
		relayout();
	}
	public TContainer getContentPane() { return contentPane; }
	public void setContentPane(TContainer container) {
		replace(contentPane, container);
		contentPane = container;
		relayout();
	}

	/**
	 * {@inheritDoc}
	 * This Component UI Name is <b>FloatPanel</b>.
	 */
	public String UIName() { return "FloatPanel"; }



	public class TTitleBar extends TContainer {
		private String 				title = "";
		private TTitleBarButton 	close, iconified;

		public TTitleBar(String title, boolean closable, boolean iconifiable,
						 int x, int y, int w, int h) {
			super(x,y,w,h);

			this.title = title;

			close = new TTitleBarButton("x", TTitleBarButton.CLOSE_BUTTON,
									    0, 0, 20, h-5);
			iconified = new TTitleBarButton("=", TTitleBarButton.ICONIFIED_BUTTON,
											0, 0, 20, h-5);
			if (!closable) close.setVisible(false);
			if (!iconifiable) iconified.setVisible(false);

			add(close);
			add(iconified);
			relayout();
		}

		protected void processMouseDragged() {
			if (TFloatPanel.this.getContainer() != null) {
				TFloatPanel.this.getContainer().sendToFront(TFloatPanel.this);
			}

			TFloatPanel.this.move(bsInput.getMouseDX(), bsInput.getMouseDY());
		}

		public void relayout() {
			close.setLocation(getWidth()-22, 2);

			// if no close button
			// iconified button will replace close button position
			iconified.setLocation(close.isVisible() ?
								  getWidth()-43 : getWidth()-22, 2);
		}
		protected void validateSize() {
			super.validateSize();

			close.setSize(20, getHeight()-5);
			iconified.setSize(20, getHeight()-5);
		}

		public boolean isClosable() { return close.isVisible(); }
		public void setClosable(boolean b) {
			close.setVisible(b);
			relayout();
		}
		public boolean isIconifiable() { return iconified.isVisible(); }
		public void setIconifiable(boolean b) {
			iconified.setVisible(b);
			relayout();
		}

		public String getTitle() { return title; }
		public void setTitle(String st) {
			this.title = title;
			createUI();
		}

		public TTitleBarButton getCloseButton() { return close; }
		public void setCloseButton(TTitleBarButton btn) {
			replace(close, btn);
			close = btn;
			relayout();
		}

		public TTitleBarButton getIconifiedButton() { return iconified; }
		public void setIconifiedButton(TTitleBarButton btn) {
			replace(iconified, btn);
			iconified = btn;
			relayout();
		}

		/**
		 * This Component UI Name is <b>TitleBar</b>.
		 */
		public String UIName() { return "Panel.TitleBar"; }



		public class TTitleBarButton extends TButton {
			public static final int CLOSE_BUTTON = 1;
			public static final int ICONIFIED_BUTTON = 2;

			private int action;

			public TTitleBarButton(String text, int action,
								   int x, int y, int w, int h) {
				super(text,x,y,w,h);

				this.action = action;

				switch (action) {
					case CLOSE_BUTTON:
						setToolTipText("Close");
					break;
					case ICONIFIED_BUTTON:
						setToolTipText((TFloatPanel.this.isIcon()) ?
									   "Restore" : "Minimize");
					break;
				}
			}

			public void doAction() {
				switch (action) {
					case CLOSE_BUTTON:
						TFloatPanel.this.setVisible(false);
					break;
					case ICONIFIED_BUTTON:
						TFloatPanel.this.setIcon(!TFloatPanel.this.isIcon());
						createUI();
					break;
				}
			}

			/**
			 * Returns the action of this button.
			 *
			 * @see #CLOSE_BUTTON
			 * @see #ICONIFIED_BUTTON
			 */
			public int getAction() { return action; }

			/**
			 * This Component UI Name is <b>TitleBarButton</b>.
			 */
			public String UIName() {
				return "Button.TitleBarButton";
			}
		}
	}

}