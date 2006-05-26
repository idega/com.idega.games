package com.golden.gamedev.gui;

import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;

import com.golden.gamedev.gui.toolkit.*;
import com.golden.gamedev.engine.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.util.*;

public class TButton extends TComponent {
	private String 		text = "";
	private boolean 	over, pressed;
	private int 		pressedTime;

	public TButton(String text, int x, int y, int w, int h) {
		super(x,y,w,h);

		this.text = text;
	}

	public void update() {
		if (pressedTime > 0 && --pressedTime <= 0) {
			pressed = false;
		}
	}

	public boolean isMouseOver() { return over; }
	public boolean isMousePressed() { return pressed; }

	public String getText() { return text; }
	public void setText(String st) { text = st; createUI(); }

	/**
	 * Do the action as the user pressed and released this button.
	 */
	public void doAction() { }

	/**
	 * Programmatically perform a "click",
	 * with the button stays in pressed state for <code>pressedTime</code> time.
	 * The button action will also be performed.
	 *
	 * @param pressedTime time for the button to stay in pressed state
	 */
	public void doClick(int pressedTime) {
		this.pressedTime = pressedTime;
		pressed = true;
		doAction();
	}

	protected void processMousePressed() {
		if (bsInput.getMousePressed() == MouseEvent.BUTTON1) {
			pressed = true;
		}
	}
	protected void processMouseReleased() {
		if (bsInput.getMouseReleased() == MouseEvent.BUTTON1) {
			pressed = false;
		}
	}
	protected void processMouseClicked() {
		if (bsInput.getMouseReleased() == MouseEvent.BUTTON1) {
			doAction();
		}
	}

	protected void processKeyPressed() {
		if (isSelected() && bsInput.getKeyPressed() == KeyEvent.VK_ENTER) {
			pressedTime = 5;
			pressed = true;
			doAction();
		}
	}
	protected void processKeyReleased() {
		if (isSelected() && bsInput.getKeyReleased() == KeyEvent.VK_ENTER) {
			pressed = false;
		}
	}

	protected void processMouseEntered() { over = true; }
	protected void processMouseExited() { over = pressed = false; }
	protected void processMouseDragged() {
		if (bsInput.isMouseDown(MouseEvent.BUTTON1)) {
			over = pressed = intersects(bsInput.getMouseX(), bsInput.getMouseY());
		}
	}

	/**
	 * This Component UI Name is <b>Button</b>.
	 */
	public String UIName() { return "Button"; }

	public String toString() {
		return super.toString() + " " +
			"[text=" + text + "]";
	}
}