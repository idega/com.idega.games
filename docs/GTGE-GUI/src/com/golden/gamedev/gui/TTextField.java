package com.golden.gamedev.gui;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import com.golden.gamedev.object.font.SystemFont;
import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.util.ImageUtil;

import com.golden.gamedev.gui.toolkit.*;

public class TTextField extends TComponent {

	private static final String NULL_STRING = new String();
	private static final GameFont f = new SystemFont(new Font("Verdana", Font.PLAIN, 12));

	private final StringBuffer buff = new StringBuffer();

	private String 		text;
	private int 		caretPosition, caret;
	private int			maxLength = -1;
	private boolean		editable;
	protected int		visiblePosition;

	private BufferedImage textUI;
	private GameFont	font = f;

	private long    	lastTicks;
	private boolean 	showCursor;

	public TTextField(String text, int x, int y, int w, int h) {
		super(x,y,w,h);

		this.text = text;
		buff.append(text);
		caret = 5;
		editable = true;

		createTextUI();
	}

	public GameFont getFont() { return font; }
	public void setFont(GameFont f) {
		font = f;
		moveCaretPosition(0);
		createTextUI();
	}

	/**
	 * Do the action as the user pressed enter when editing this textfield.
	 */
	public void doAction() { }

	public void update() {
		if (isSelected() && editable) {
			if (System.currentTimeMillis()-lastTicks > 500) {
				lastTicks = System.currentTimeMillis();
				showCursor = !showCursor;
			}

		} else if (showCursor) {
			showCursor = false;
		}
	}

	public void setEnabled(boolean b) {
		if (isEnabled() == b) return;
		super.setEnabled(b);

		createTextUI();
	}

	protected void createTextUI() {
		textUI = ImageUtil.createImage(getWidth(), getHeight(),
									   Transparency.BITMASK);

		Graphics2D g = textUI.createGraphics();

		g.setClip(5, 2, getWidth()-10, getHeight()-4);
		g.setColor((isEnabled()) ? Color.BLACK : new Color(172, 168, 153));
		font.drawString(g, getText(), 5-visiblePosition, (getHeight()/2)-(font.getHeight()/2));

		g.dispose();
	}

	public void render(Graphics2D g) {
		super.render(g);

		renderText(g, getScreenX(), getScreenY(), getWidth(), getHeight());
	}

	protected void renderText(Graphics2D g, int x, int y, int w, int h) {
		g.drawImage(textUI, x, y, null);

		if (editable && showCursor) {
			int yfont = y+(h>>1)-(font.getHeight()>>1);

			g.setColor(Color.BLACK);
			g.drawLine(x+caret, yfont,
					   x+caret, yfont+font.getHeight());
		}
	}

	public String getText() { return text; }
	public void setText(String st) {
		buff.setLength(0);
		buff.append(st);
		if (maxLength >= 0 && buff.length() > maxLength) {
			buff.setLength(maxLength);
		}
		text = buff.toString();

		setCaretPosition(buff.length());
		createTextUI();
	}

	public int getMaxLength() { return maxLength; }
	public void setMaxLength(int i) {
		maxLength = i;
		if (maxLength >= 0 && buff.length() > maxLength) {
			buff.setLength(maxLength);
			text = buff.toString();
			createTextUI();
		}
	}

	public int getCaretPosition() { return caretPosition; }
	public void setCaretPosition(int i) {
		caretPosition = i;

		if (caretPosition < 0) caretPosition = 0;
		if (caretPosition > buff.length()) caretPosition = buff.length();

		boolean invalidCaret = true,
				createText = false;
		while (invalidCaret) {
			invalidCaret = false;
			caret = font.getWidth(getText().substring(0, caretPosition)) + 5 - visiblePosition;
			if (caret < 5) {
				int len = font.getWidth(getText().substring(
										Math.max(caretPosition-5, 0),
										Math.max(caretPosition-1, 0)));
				visiblePosition -= len;
				if (visiblePosition < 0 || caretPosition <= 4)
					visiblePosition = 0;
				invalidCaret = true;
				createText = true;

	   		} else if (caret >= getWidth()-5) {
				int len = font.getWidth(getText().substring(
										Math.min(caretPosition-1, getText().length()),
										Math.min(caretPosition+3, getText().length())));
				visiblePosition += len;
				invalidCaret = true;
				createText = true;
			}
		}

		if (createText) createTextUI();
	}
	public void moveCaretPosition(int i) {
		setCaretPosition(caretPosition + i);
	}

	public boolean isEditable() { return editable; }
	public void setEditable(boolean b) {
		if (editable == b) return;

		editable = b;
		createTextUI();
	}

	public boolean insertString(int offset, String st) {
		if (maxLength >= 0 &&
			buff.length() + st.length() > maxLength) {
			return false;
		}

		buff.insert(offset, st);
		text = buff.toString();
		createTextUI();
		return true;
	}

	public void delete(int index) {
		if (index < 0 || index > buff.length()-1) return;

		buff.deleteCharAt(index);
		text = buff.toString();

		if (caretPosition > buff.length()) {
			setCaretPosition(buff.length());
		}
		createTextUI();
	}

	protected void processKeyPressed() {
		super.processKeyPressed();

		if (!editable) return;

		switch (bsInput.getKeyPressed()) {
			case KeyEvent.VK_LEFT:
				if (bsInput.isKeyDown(KeyEvent.VK_CONTROL)) {
					int pos = getText().lastIndexOf(' ', caretPosition-2);
					if (pos != -1) {
						setCaretPosition(pos+1);
					} else {
						setCaretPosition(0);
					}
				} else {
					moveCaretPosition(-1);
				}
			break;

			case KeyEvent.VK_RIGHT:
				if (bsInput.isKeyDown(KeyEvent.VK_CONTROL)) {
					int pos = getText().indexOf(' ', caretPosition);
					if (pos != -1) {
						setCaretPosition(pos+1);
					} else {
						setCaretPosition(getText().length());
					}

				} else {
					moveCaretPosition(1);
				}
			break;

			case KeyEvent.VK_HOME:
				setCaretPosition(0);
			break;

			case KeyEvent.VK_END:
				setCaretPosition(getText().length());
			break;

			case KeyEvent.VK_ENTER:
				doAction();
			break;

			case KeyEvent.VK_BACK_SPACE:
			    if (caretPosition > 0) {
					moveCaretPosition(-1);
					delete(caretPosition);
				}
			break;

			case KeyEvent.VK_DELETE:
			    delete(caretPosition);
			break;

			default:
				boolean upperCase = bsInput.isKeyDown(KeyEvent.VK_SHIFT);
				boolean capsLock = false;
				try {
					capsLock = Toolkit.getDefaultToolkit().getLockingKeyState(KeyEvent.VK_CAPS_LOCK);
				} catch (Exception e) {
				}

				String st = NULL_STRING;
				int keyCode = bsInput.getKeyPressed();

				switch (keyCode) {
					case KeyEvent.VK_BACK_QUOTE: st = (upperCase) ? "~" : "`"; break;
					case KeyEvent.VK_1: 	st = (upperCase) ? "!" : "1"; break;
					case KeyEvent.VK_2: 	st = (upperCase) ? "@" : "2"; break;
					case KeyEvent.VK_3: 	st = (upperCase) ? "#" : "3"; break;
					case KeyEvent.VK_4: 	st = (upperCase) ? "$" : "4"; break;
					case KeyEvent.VK_5: 	st = (upperCase) ? "%" : "5"; break;
					case KeyEvent.VK_6: 	st = (upperCase) ? "^" : "6"; break;
					case KeyEvent.VK_7: 	st = (upperCase) ? "&" : "7"; break;
					case KeyEvent.VK_8: 	st = (upperCase) ? "*" : "8"; break;
					case KeyEvent.VK_9: 	st = (upperCase) ? "(" : "9"; break;
					case KeyEvent.VK_0: 	st = (upperCase) ? ")" : "0"; break;
					case KeyEvent.VK_MINUS: st = (upperCase) ? "_" : "-"; break;
					case KeyEvent.VK_EQUALS: st = (upperCase) ? "+" : "="; break;
					case KeyEvent.VK_BACK_SLASH: st = (upperCase) ? "|" : "\\"; break;
					case KeyEvent.VK_OPEN_BRACKET: st = (upperCase) ? "{" : "["; break;
					case KeyEvent.VK_CLOSE_BRACKET: st = (upperCase) ? "}" : "]"; break;
					case KeyEvent.VK_SEMICOLON: st = (upperCase) ? ":" : ";"; break;
					case KeyEvent.VK_QUOTE:	st = (upperCase) ? "\"" : "'"; break;
					case KeyEvent.VK_COMMA:	st = (upperCase) ? "<" : ","; break;
					case KeyEvent.VK_PERIOD: st = (upperCase) ? ">" : "."; break;
					case KeyEvent.VK_SLASH: st = (upperCase) ? "?" : "/"; break;
					case KeyEvent.VK_DIVIDE: st = "/"; break;
					case KeyEvent.VK_MULTIPLY: st = "*"; break;
					case KeyEvent.VK_SUBTRACT: st = "-"; break;
					case KeyEvent.VK_ADD: st = "+"; break;
					case KeyEvent.VK_DECIMAL: st = "."; break;
					case KeyEvent.VK_SPACE: st = " "; break;
					default:
						st = KeyEvent.getKeyText(keyCode).toLowerCase();
						if (st.startsWith("numpad")) {
							// convert numpadX -> X
							st = st.substring(7);
						}

						if (st.length() == 0 || st.length() > 1) {
							// invalid key
							return;
						}
					break;
				}

				if (keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z) {
					if (upperCase && !capsLock || !upperCase && capsLock) {
						// upper case
						st = st.toUpperCase();
					}
				}

				if (st == NULL_STRING) return;

				if (insertString(caretPosition, st)) {
					moveCaretPosition(1);
				}
			break;
		}
	}

	/**
	 * This Component UI Name is <b>TextField</b>.
	 */
	public String UIName() { return "TextField"; }

}