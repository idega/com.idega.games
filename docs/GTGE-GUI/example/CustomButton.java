import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import com.golden.gamedev.Game;
import com.golden.gamedev.object.AnimatedSprite;
import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.util.ImageUtil;

import com.golden.gamedev.gui.*;
import com.golden.gamedev.gui.toolkit.*;

///////// a sample of custom component rendering /////////
public class CustomButton extends TButton {
	private Game			owner;

	// some animation -> we want a nice custom button right :)
	private AnimatedSprite 	duke;
	private BufferedImage[] standing, waving;
	private BufferedImage	standingBig;

	private GameFont		font;

	public CustomButton(Game owner, String text, int x, int y, int w, int h) {
		super(text,x,y,w,h);

		// turn on custom rendering flag
		// in this way, the component is fully responsible of its own rendering
		customRendering = true;

		// font for this button
		font = owner.fontManager.getFont(owner.getImage("images/bitmapfont.png"));

		// loading the animation (if it's already loaded before, grab it from cache)
		standing = owner.getImages("images/duke.png", 10, 1, "0", 1);
		waving = owner.getImages("images/duke.png", 10, 1, "123456789", 1);
		standingBig = ImageUtil.resize(standing[0],
			(int)((double)standing[0].getWidth()*1.1),
			(int)((double)standing[0].getHeight()*1.1));
		duke = new AnimatedSprite(standing, 0, 0);

		this.owner = owner;
	}

	public void update() {
		duke.update(10);

		if (isMousePressed()) {
			// pressed state, the duke is waving
		 	if (duke.isAnimate() == false) {
				if (duke.getImages() != waving) {
					duke.setImages(waving);
				}
				// turn on animation
				duke.setAnimate(true);
			}

		} else {
			// unpressed state, the duke just standing
			if (!duke.isAnimate() && duke.getImages() != standing) {
				duke.setImages(standing);
			}
		}
	}

	protected void processMousePressed() {
		super.processMousePressed();

		if (bsInput.getMousePressed() == MouseEvent.BUTTON1) {
			// play sound every time the mouse clicking this button
			owner.playSound("images/click.wav");
		}
	}

	// creates the background ui
	protected void createCustomUI(int w, int h) {
		// creates 2 ui images -> for normal and pressed state
		ui = GraphicsUtil.createImage(2, w, h, Transparency.BITMASK);

		// draw the button, hexagonal shape???
		GeneralPath path = new GeneralPath();
		path.moveTo(0, h/2);
		path.lineTo((w/2)-(w/4), 0);
		path.lineTo((w/2)+(w/4), 0);
		path.lineTo(w, h/2);
		path.lineTo((w/2)+(w/4), h);
		path.lineTo((w/2)-(w/4), h);
		path.closePath();

		// fill with gradient color
		Color[] col1 = new Color[] { new Color(204,204,255), Color.LIGHT_GRAY },
				col2 = new Color[] { new Color(102,102,153), Color.GRAY };

		for (int i=0;i < ui.length;i++) {
			Graphics2D g = ui[i].createGraphics();
			g.setPaint(new GradientPaint(0, 0, col1[i],
										 w, h, col2[i], false));
			g.fill(path);
			g.dispose();
		}
	}
	// processing the ui -> drawing text and other accessory
	protected void processCustomUI() {
		for (int i=0;i < ui.length;i++) {
			Graphics2D g = ui[i].createGraphics();

			// draw the button text exactly at the bottom of the duke
			font.drawString(g, getText(), GameFont.CENTER,
						    0, (getHeight()/3)-(duke.getHeight()/2)+(standingBig.getHeight())+2, getWidth());

			g.dispose();
		}
	}
	// render the ui to screen
	protected void renderCustomUI(Graphics2D g, int x, int y, int w, int h) {
		g.drawImage((isMousePressed()) ? ui[1] : ui[0], x, y, null);

		// mouse over the button, draw a BIG duke
		if (!duke.isAnimate() && !isMousePressed() && isMouseOver() ) {
			g.drawImage(standingBig,
						x + (w/2)-(standingBig.getWidth()/2),
						y + (h/3)-(duke.getHeight()/2),
						null);

		} else {
			// render normal duke
			duke.render(g, x + (w/2)-(duke.getWidth()/2),
						   y + (h/3)-(duke.getHeight()/2));
		}
	}

	// since the button is not rectangular, we use pixel scanning here
	public boolean intersects(int x, int y) {
		if (super.intersects(x, y)) {
			// pixel checking
			boolean b = false;

			try {
				b = (ui[0].getRGB(x-getScreenX(), y-getScreenY()) != 0);
			} catch (Exception e) {
				// bugs or not? :
				// width = 50, height = 50
				// getRGB(50, 50) throws out of bounds exception
				b = (ui[0].getRGB(x-getScreenX()-1, y-getScreenY()-1) != 0);
			}

			return b;

		} else return false;
	}

}