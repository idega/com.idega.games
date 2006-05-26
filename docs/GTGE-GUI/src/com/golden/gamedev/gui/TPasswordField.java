package com.golden.gamedev.gui;

public class TPasswordField extends TTextField {
	private final StringBuffer buff = new StringBuffer();

	private String	password = "";
	private char	echoChar = '*';
	protected boolean	allowTextCreation;

	public TPasswordField(String text, int x, int y, int w, int h) {
		super(text,x,y,w,h);

		allowTextCreation = true;
		createPassword();
		createTextUI();
	}
	protected void createPassword() {
		if (echoChar != 0) {
			int len = super.getText().length();
			buff.setLength(0);
			for (int i=0;i < len;i++) {
				buff.append(echoChar);
			}

			password = buff.toString();
		} else {
			password = super.getText();
		}
	}

	protected void createTextUI() {
		if (!allowTextCreation) return;
		super.createTextUI();
	}

	public void setText(String st) {
		allowTextCreation = false;
		super.setText(st);
		allowTextCreation = true;
		createPassword();
		createTextUI();
	}

	public String getText() { return password; }
	public String getPasswordText() { return super.getText(); }

	public char getEchoChar() { return echoChar; }
	public void setEchoChar(char c) {
		echoChar = c;
		createPassword();
		createTextUI();
		moveCaretPosition(0);
	}

	public void setMaxLength(int i) {
		allowTextCreation = false;
		super.setMaxLength(i);
		allowTextCreation = true;
		createPassword();
		createTextUI();
	}

	public boolean insertString(int offset, String st) {
		allowTextCreation = false;
		boolean retval = super.insertString(offset, st);
		allowTextCreation = true;
		if (retval) {
			createPassword();
			createTextUI();
		}
		return retval;
	}

	public void delete(int index) {
		allowTextCreation = false;
		super.delete(index);
		allowTextCreation = true;
		createPassword();
		createTextUI();
	}

}