// JFC
import java.awt.*;
import java.awt.image.BufferedImage;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.util.FontUtil;

// GTGE GUI
import com.golden.gamedev.gui.*;
import com.golden.gamedev.gui.toolkit.*;


public class TestCase extends Game {


	private FrameWork 	frame;	// gui framework

	private Color 		bgColor = new Color(255, 128, 64);


 /****************************************************************************/
 /**********************************  ***************************************/
 /****************************************************************************/

	public void initResources() {
//		setFPS(3000);
		setMaskColor(Color.GREEN);

		// creates the gui frame work
		frame = new FrameWork(bsInput, getWidth(), getHeight());
		// turn on label bordering for default
		// -> to see label boundary
//		frame.getTheme().getUIRenderer("Label").put("Background Border Color", Color.BLACK);

		///////// adding components into gui framework /////////

		// the first component (button component)
		// simply add component to framework without any modification
		frame.add(new TButton("Hello World", 20, 50, 120, 40));
		// frame.get() method is return the latest inserted component
		frame.get().setToolTipText("the first button\nsimply add to the framework");

		// next, button component with interface modification
		TButton button = new TButton("CHANGE DEFAULT\nBUTTON FONT",
								  	 20, 105, 210, 40);
		// change the default button font with a bitmap font
		// (interface modification)
		GameFont f = fontManager.getFont(getImage("images/bitmapfont.png"));
		button.UIResource().put("Text Font", f);
		button.UIResource().put("Text Over Font", f);
		button.UIResource().put("Text Pressed Font", f);
		button.setToolTipText("change the default button font with bitmap font\n"+
							  "see the font images in folder 'images/bitmapfont.png'");
		frame.add(button);

		// label component with interface and inner modification
		TLabel label = new TLabel("This is a label with a ToolTip",
							 	   150, 50, 200, 40);
		// modified the label text (inner modification)
		label.setText(label.getText() + "\n" + "move mouse to here to see it");
		// change background color (interface modification)
		label.UIResource().put("Background Color", Color.GRAY);
		label.UIResource().put("Background Border Color", Color.DARK_GRAY);
		label.UIResource().put("Text Horizontal Alignment Integer", UIConstants.CENTER);
		label.setToolTipText("every components in here have an explanation tooltip");
		frame.add(label);


		// simple panel for grouping components
		final GameFont panelFont = fontManager.getFont(new Font("Verdana", Font.PLAIN, 12), Color.BLACK);
		TPanel panel = new TPanel(360, 40, 170, 60) {
			public void render(Graphics2D g) {
				super.render(g);

				// drawing text straight to the panel screen
				panelFont.drawString(g, "Grouping Component",
									 getScreenX()+7, getScreenY()+14);
				panelFont.drawString(g, "in a Panel",
									 getScreenX()+7, getScreenY()+29);
			}
		};
		panel.setToolTipText(
			"panel is the simplest container for grouping components\n" +
			"here i will show you that moving a container\n" +
			"will move all the components inside it\n\n" +
			"also the buttons is a good example of how to use action button");
			// up button
			TButton up = new TButton("up", 152, 2, 0, 0) {
				public void doAction() {
					// move the container (the panel) 1 pixel up
					getContainer().move(0, -1);
				}
			};
			// the up image: 0-standard, 1-over, 2-pressed, 0-disabled (0120)
			up.setExternalUI(getImages("images/up.png", 3, 1, "0120", 1), false);
			up.setToolTipText("move this panel 1 pixel up");

			// down button
			TButton down = new TButton("down", 152, 20, 0, 0) {
				public void doAction() { getContainer().move(0, 1); }
			};
			down.setExternalUI(getImages("images/down.png", 3, 1, "0120", 1), false);
			down.setToolTipText("move this panel 1 pixel down");
		panel.add(up);
		panel.add(down);
		frame.add(panel);

		// text field
		frame.add(new TTextField("Hello World", 240, 110, 110, 25));
		frame.get().setToolTipText("textfield component");

		// uneditable textfield
		final TTextField textField = new TTextField("Uneditable TextField",
											  		360, 110, 143, 25);
		textField.setEditable(false);
		textField.setToolTipText("show a textfield in editable/uneditable state");
		frame.add(textField);

		frame.add(new TButton("*", 508, 110, 22, 25) {
			public void doAction() {
				textField.setEditable(!textField.isEditable());
				textField.setText((textField.isEditable()) ?
					"Editable TextField" : "Uneditable TextField");
			}
		} );
		frame.get().setToolTipText("change beside textfield editable state");

		// pane component -> 'specially' for grouping components
		final TPane pane = new TPane(20, 350, 200, 100);
			TLabel lb = new TLabel("Pane is a transparent container",
								   0, 0, 200, 25);
			lb.setToolTipText("it is always better to group components\n" +
							  "in that way moving/hiding/enabling\n" +
							  "components in a group would be very easy\n\n" +
							  "for a transparent container use pane");
		pane.add(lb);
		pane.add(new TButton("Button in a Pane", 0, 30, 150, 25));
		pane.get().setToolTipText("this three buttons is grouped in a transparent container");
		pane.add(new TButton("Another Button", 0, 60, 150, 25));
		pane.get().setToolTipText("this three buttons is grouped in a transparent container");
		pane.add(new TButton("X", 155, 30, 40, 55));
		pane.get().setToolTipText("this three buttons is grouped in a transparent container");
		frame.add(pane);

		TLabel hideLabel = new TLabel("Mouse over here to hide above pane",
									  20, 450, 240, 25) {
			protected void processMouseEntered() { pane.setVisible(false); }
			protected void processMouseExited() { pane.setVisible(true); }
		};
		hideLabel.setToolTipText("there are numerous way to\n" +
								 "make an action of a component\n\n" +
								 "this is a sample of using mouse enter/exit action");
		frame.add(hideLabel);

		// floatpanel (equal to JInternalFrame???)
		TFloatPanel floatPane = new TFloatPanel("Floating Panel", true, true,
												20, 160, 200, 175);
			TContainer contentPane = floatPane.getContentPane();
			contentPane.setToolTipText(
				"a floating panel contains title bar and content pane\n\n" +
				"content pane is simply a panel to hold components\n" +
				"and the title bar is for moving the panel\n" +
				"that's why i called this floating panel :)\n\n" +
				"a floating panel can be iconized or closed by\n" +
				"clicking the right action button in its title bar");

			// component to show the entered password
			final TFloatPanel modalPane = new TFloatPanel("Modal Component",
														  true, false,
														  220, 140, 200, 200);
			modalPane.setVisible(false);
			modalPane.setLayer(modalPane.getLayer()+2); // always on top
			modalPane.getTitleBar().UIResource().put("Background Color", Color.RED);
			modalPane.getTitleBar().getCloseButton().UIResource().put("Background Color",
																	  new Color(204, 204, 204));
			modalPane.add(new TLabel("", 5, 0, 190, 50));
			modalPane.getContentPane().get().UIResource().put("Text Horizontal Alignment Integer",
															  UIConstants.CENTER);
			modalPane.add(new TLabel("This component is being\n" +
									 "set as modal component\n" +
									 "frame.setModal(TComponent)\n\n" +
									 "Please CLOSE this component\n" +
									 "to continue",
									 5, 40,
									 190, modalPane.getContentPane().getHeight()-40));
			modalPane.getContentPane().get().UIResource().put("Text Horizontal Alignment Integer",
															  UIConstants.CENTER);
			frame.add(modalPane);

			// password textfield
			final TPasswordField passwordField = new TPasswordField("52432",
													  	  			10, 50, 148, 30) {
				public boolean insertString(int offset, String st) {
					// accept only numeric value
					try { Integer.parseInt(st);
					} catch (Exception e) { return false; }

					return super.insertString(offset, st);
				}
				public void doAction() {
					((TLabel) modalPane.getContentPane().getComponents()[1]).setText(
						"The password you entered :\n" + getPasswordText()
					);
					modalPane.setVisible(true);
					frame.setModal(modalPane);
				}
			};
			passwordField.setMaxLength(10);
			passwordField.setFont(fontManager.getFont(getImage("images/bitmapfont.png")));
			passwordField.setToolTipText("This password field is modified to accept only numbers!\n" +
										 "PasswordField is subclass of TextField");

			TButton btnOK = new TButton("OK", 162, 50, 30, 30) {
				public void doAction() { passwordField.doAction(); }
			};
			btnOK.setToolTipText("OK");

			TButton btnMask = new TButton("mask password", 10, 85, 120, 25) {
				public void doAction() {
					passwordField.setEchoChar(
						(passwordField.getEchoChar() == 0) ? '*' : (char) 0);
				}
			};
			btnMask.setToolTipText("mask/unmask password");

		floatPane.add(new TLabel("Enter Password", 10, 30, 160, 20));
		floatPane.getContentPane().get().setToolTipParent(contentPane);
		floatPane.add(passwordField);
		floatPane.add(btnOK);
		floatPane.add(btnMask);
		frame.add(floatPane);

		// floating panel that contains component that used external image
		TFloatPanel external = new TFloatPanel("Various Components Rendering",
											   true, true,
											   230, 150, 300, 305);
			// processed external ui
			TButton btn = new TButton("PROCESSED\nEXTERNAL UI", 24, 13, 0, 0);
			btn.setExternalUI(getImages("images/button1.png", 4, 1),
							  true);
			btn.UIResource().put("Text Color", Color.WHITE);
			btn.UIResource().put("Text Over Color", Color.WHITE);
			GameFont externalFont = fontManager.getFont(new Font("Arial", Font.BOLD, 14));
			btn.UIResource().put("Text Font", externalFont);
			btn.UIResource().put("Text Over Font", externalFont);
			btn.UIResource().put("Text Pressed Font", externalFont);
			btn.UIResource().put("Text Vertical Space Integer", new Integer(0));
			btn.UIResource().put("Text Insets", new Insets(4, 5, 5, 5));
			btn.setToolTipText("button with external images\n\n" +
							   "component ui is taken from external image\n" +
							   "however the image still processed\n" +
							   "by this button default renderer\n" +
							   "(drawing the button text to the image)");
			// unprocessed external ui
			TButton btn2 = new TButton("Unprocessed External UI", 165, 11, 0, 0);
			btn2.setExternalUI(getImages("images/button2.png", 2, 1, "0010", 1),
							   false);
			btn2.setToolTipText("this component ui is taken from external image\n" +
							    "and the image is rendered as is (no further process)\n\n" +
								"notice that the text 'LOAD GAME' is the part of the image itself\n" +
								"(the button text is not rendered to the image ui)\n\n" +
								"note: component with external ui can not be resized");
			// custom button, see the source code at the bottom
			TButton btn3 = new CustomButton(this, "CUSTOM", 10, 65, 150, 100);
			btn3.setToolTipText("sample of using custom rendering\n\n" +
								"when in custom rendering state\n" +
								"component is fully responsible of its own rendering\n" +
								"you can render whatever you want to the component");
			// another custom button
			TButton btn4 = new CustomButton(this, "ANOTHER", 30, 170, 240, 105);
			btn4.setToolTipText("another custom rendered button\n" +
								"(same as above button)\n" +
							    "show you that custom button\n" +
								"also can be in whatever size");
			// fully customized button
			TButton btn5 = new TButton("FULLY\nCUSTOMIZED\nBUTTON", 170, 65, 120, 100);
			btn5.UIResource().put("Background Color", new Color(228, 229, 206));
			btn5.UIResource().put("Background Over Color", new Color(228, 229, 206));
			btn5.UIResource().put("Background Pressed Color", new Color(222, 223, 201));
			btn5.UIResource().put("Background Border Color", Color.DARK_GRAY);
			btn5.UIResource().put("Text Color", Color.RED);
			btn5.UIResource().put("Text Over Color", Color.GREEN.darker().darker());
			btn5.UIResource().put("Text Pressed Color", Color.BLUE.darker());
			GameFont btnFont = fontManager.getFont(new Font("Arial", Font.BOLD | Font.ITALIC, 15));
			btn5.UIResource().put("Text Font", btnFont);
			btn5.UIResource().put("Text Over Font", btnFont);
			btn5.UIResource().put("Text Pressed Font", btnFont);
			btn5.UIResource().put("Text Horizontal Alignment Integer", UIConstants.LEFT);
			btn5.UIResource().put("Text Insets", new Insets(10, 10, 10, 10));
			btn5.UIResource().put("Text Vertical Space Integer", new Integer(8));
			btn5.setToolTipText("a fully customized button\n\n" +
								"this button is same like other button\n" +
								"(rendered by its default renderer)\n" +
								"but all its resources has been changed\n\n" +
								"you can see what resource can be customized in\n" +
								"component.printUIResource();");
		external.add(btn);
		external.add(btn2);
		external.add(btn3);
		external.add(btn4);
		external.add(btn5);
		frame.add(external);

		createInfoBox();

		// validate ui interface -> not really neccessary
		frame.validateUI();
	}

	private void createInfoBox() {
		final TFloatPanel about = new TFloatPanel("About-Box", false, false,
												  10, 30, 620, 405);
		// set about box always in front of other floating panel
		about.setLayer(about.getLayer() + 1);
		about.setVisible(false);
			TLabel lbAbout = new TLabel("", 15, 15, 550, 340);
			String text = "GRAPHICAL USER INTERFACE (GUI) for Games\n" +
						  "---------------------------------------------------------\n\n" +
						  "HOW TO USE:\n" +
						  "create the top frame:\n" +
						  "FrameWork frame = new FrameWork(bsInput, width, height);\n\n" +
						  "add component to the frame:\n" +
						  "frame.add(new TButton(\"text\", x, y, w, h));\n\n" +
						  "update the frame:\n" +
						  "frame.update();\n\n" +
						  "render to screen:\n" +
						  "frame.render(Graphics2D);\n\n\n" +
						  "GTGE GUI Copyright © 2004-2005 Golden T Studios\n" +
						  "http://www.goldenstudios.or.id/";
			lbAbout.setText(text);
			lbAbout.UIResource().put("Text Vertical Alignment Integer", UIConstants.TOP);
		about.add(lbAbout);
		frame.add(about);

		TPanel infoBox = new TPanel(537, 0, 105, 480);
			TLabel label = new TLabel("GTGE GUI\nver 0.01\n" +
									   "========\n" +
									   "- Label\n" +
									   "- Button\n" +
									   "- TextField\n" +
									   "- PasswordField\n" +
									   "- Pane\n" +
									   "- Panel\n" +
									   "- FloatPanel\n" +
									   "- ToolTip",
									   8, 10, 100, 180);
			label.UIResource().put("Text Color", Color.BLUE.darker());
			label.UIResource().put("Text Font", fontManager.getFont(new Font("Verdana", Font.PLAIN, 11)));

			// change theme
			TButton themeBtn = new TButton("change\ntheme", 8, 375, 90, 35) {
				public void doAction() {
					if (frame.getTheme().getName().equals("Basic Theme")) {
						frame.installTheme(new TestTheme());
					} else {
						frame.installTheme(new com.golden.gamedev.gui.theme.basic.BasicTheme());
					}
				}
			};
			themeBtn.UIResource().put("Text Vertical Space Integer", new Integer(-4));
			themeBtn.setToolTipText("change gui theme from basic theme to test theme\n" +
								  	"or vice versa");

			// set frame per second
			TButton fpsBtn = new TButton("change fps", 8, 415, 90, 25) {
				public void doAction() {
					setFPS((getFPS() == 50) ? 3000 : 50);
				}
			};
			fpsBtn.setToolTipText("force frame per second to 3000 from 50\n" +
								  "or vice versa");

			// toggle show/hide about box
			TButton aboutBtn = new TButton("about box", 8, 445, 90, 25) {
				public void doAction() {
					about.setVisible(!about.isVisible());
					setToolTipText((about.isVisible()) ?
								   "hide about box" : "show about box");
					playSound("images/clik.wav");
				}
			};
			aboutBtn.setToolTipText("show about box");
		infoBox.add(label);
		infoBox.add(themeBtn);
		infoBox.add(fpsBtn);
		infoBox.add(aboutBtn);
		frame.add(infoBox);
	}


 /****************************************************************************/
 /***************************** UPDATE GAME **********************************/
 /****************************************************************************/

	public void update(long elapsedTime) {
		frame.update();
	}


 /****************************************************************************/
 /***************************** RENDER GAME **********************************/
 /****************************************************************************/

	public void render(Graphics2D g) {
		g.setColor(bgColor);
		g.fillRect(0,0,getWidth(),getHeight());
		frame.render(g);
	}


 /****************************************************************************/
 /***************************** MAIN-CLASS ***********************************/
 /****************************************************************************/

	public static void main(String[] args) {
		GameLoader test = new GameLoader();
		test.setup(new TestCase(), new Dimension(640, 480), false);
		test.start();

//		OpenGLGameLoader test = new OpenGLGameLoader();
//		test.setupLWJGL(new TestCase(), new Dimension(640, 480), false);
//		test.start();
	}

}