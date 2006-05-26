package com.golden.gamedev.gui.toolkit;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.util.ImageUtil;

/**
 * Graphics static utility collection used for UI Rendering.
 */
public class GraphicsUtil {
	private static final Insets NULL_INSETS = new Insets(0,0,0,0);
	private static final Integer VSPACE = new Integer(2);
	private static final Rectangle anchor = new Rectangle();

	public static BufferedImage[] createImage(int count,
										  	  int w, int h,
										  	  int transparency) {
		BufferedImage[] image = new BufferedImage[count];

		for (int i=0;i < image.length;i++) {
			image[i] = ImageUtil.createImage(w, h, transparency);
		}

		return image;
	}

	public static void drawString(Graphics2D g, String[] document, int w, int h,
								  GameFont font, Color color,
								  Integer hAlignment, Integer vAlignment,
								  Insets inset, Integer vSpace) {
		if (inset == null) inset = NULL_INSETS;
		if (hAlignment == null) hAlignment = UIConstants.CENTER;
		if (vAlignment == null) vAlignment = UIConstants.CENTER;
		if (vSpace == null) vSpace = VSPACE;

		int space = vSpace.intValue();
		int height = (document.length * (font.getHeight()+space)) - space;

		int y = 0;
		if (vAlignment == UIConstants.TOP) {
			y = inset.top;
		} else if (vAlignment == UIConstants.BOTTOM) {
			y = h - inset.bottom - height;
		} else if (vAlignment == UIConstants.CENTER) {
		 	y = (h/2) - (height/2);
		}

		g.setColor(color);
		for (int i=0;i < document.length;i++) {
			font.drawString(g, document[i], hAlignment.intValue(),
						 	inset.left, y,
						 	w - inset.left - inset.right);

			y += font.getHeight() + space;
		}
	}

	public static String[] parseString(String text) {
		int token, index, index2;
		token = index = index2 = 0;
		while ((index = text.indexOf('\n', index)) != -1) {
			token++; index++;
		}
		token++;
		index = 0;

		String[] document = new String[token];
		for (int i=0;i < token;i++) {
			index2 = text.indexOf('\n', index);
			if (index2 == -1) index2 = text.length();
			document[i] = text.substring(index, index2);
			index = index2 + 1;
		}

//		StringTokenizer st = new StringTokenizer(text, "\n");
//		String[] document = new String[st.countTokens()];
//		for (int i=0;i < document.length;i++) {
//			document[i] = st.nextToken();
//		}

		return document;
	}

	public static BufferedImage loadImage(Class loader, String imagefile,
										  int transparency) {
		return ImageUtil.getImage(loader.getResource(imagefile), transparency);
	}

	public static BufferedImage[] createTexture(Class loader, String imagefile,
												int transparency) {
		return createTexture(loadImage(loader, imagefile, transparency));
	}
	public static BufferedImage[] createTexture(BufferedImage image) {
		int count = 1,
			width = image.getWidth(),
			height = image.getHeight(),
			delimiter = image.getRGB(0, 0);

		for (int i=1;i < width;i++) {
			// calculate total texture
			if (image.getRGB(i, 0) == delimiter) {
				count++;
			}
		}

		BufferedImage[] texture = new BufferedImage[count];

		// create the texture image
		int current = 0, w = 0, h = 0;
		for (int i=0;i < width;i++) {
			if (image.getRGB(i, 0) == delimiter) {
				for (int j=1;j < height;j++) {
					if (image.getRGB(i, j) == delimiter) {
						if (current > 0) {
							texture[current-1] =
								ImageUtil.createImage(i-w, h, Transparency.BITMASK);
						}

						w = i; h = j-1;
						break;
					}
				}

				current++;
				if (current == count) {
					texture[current-1] =
						ImageUtil.createImage(image.getWidth()-w, h, Transparency.BITMASK);
					break;
				}
			}
		}

		// fill the texture and create texture paint from it
		int x = 0;
		for (int i=0;i < count;i++) {
			Graphics2D g = texture[i].createGraphics();

			g.drawImage(image,
						0, 0, texture[i].getWidth(), texture[i].getHeight(), // destination
						x, 1, x+texture[i].getWidth(), 1+texture[i].getHeight(),
						null);

			g.dispose();

			x += texture[i].getWidth();
		}

		return texture;
	}

	public static void createTextureFile(BufferedImage[] image, File f) {
		int w = 0, h = 0;
		for (int i=0;i < image.length;i++) {
			w += image[i].getWidth();
			if (h < image[i].getHeight()) h = image[i].getHeight();
		}
		h += 2; // for height delimiter

		Color delimiter = Color.GREEN;
		int x = 0, y = 0;
		BufferedImage texture = ImageUtil.createImage(w, h, Transparency.BITMASK);
		Graphics2D g = texture.createGraphics();

		for (int i=0;i < image.length;i++) {
			// render w & h delimiter
			g.setColor(delimiter);
			g.drawLine(x, 0, x, 0);
			g.drawLine(x, image[i].getHeight()+1, x, image[i].getHeight()+1);

			// render the texture
			g.drawImage(image[i], x, 1, null);

			x += image[i].getWidth();
		}

		g.dispose();

		ImageUtil.saveImage(texture, f);
	}

	private static TexturePaint[] createTexturePaint(BufferedImage[] texture) {
		int count = texture.length;
		TexturePaint[] texturePaint = new TexturePaint[count];

		// create texture paint from texture
		for (int i=0;i < count;i++) {
			anchor.setBounds(0, 0, texture[i].getWidth(), texture[i].getHeight());
			texturePaint[i] = new TexturePaint(texture[i], anchor);
		}

		return texturePaint;
	}

	public static void renderTexture(Graphics2D g, BufferedImage[] texture,
									 int w, int h) {
		TexturePaint[] texturePaint = createTexturePaint(texture);

		if (texturePaint.length > 8) {
			g.setPaint(texturePaint[8]);
			g.fillRect(0, 0, w, h);
		}

		// fill left
		g.setPaint(texturePaint[0]);
		g.fillRect(0, 0, texturePaint[0].getImage().getWidth(), h);

		// fill right
		g.setPaint(texturePaint[1]);
		g.fillRect(w-texturePaint[1].getImage().getWidth(), 0,
				   texturePaint[1].getImage().getWidth(), h);

		// fill top
		g.setPaint(texturePaint[2]);
		g.fillRect(0, 0, w, texturePaint[2].getImage().getHeight());

		// fill top left
		g.setPaint(texturePaint[3]);
		g.fillRect(0, 0,
				   texturePaint[3].getImage().getWidth(),
				   texturePaint[3].getImage().getHeight());

		// fill top right
		g.setPaint(texturePaint[4]);
		g.fillRect(w-texturePaint[4].getImage().getWidth(), 0,
				   texturePaint[4].getImage().getWidth(),
				   texturePaint[4].getImage().getHeight());

		// fill bottom
		g.setPaint(texturePaint[5]);
		g.fillRect(0, h-texturePaint[5].getImage().getHeight(),
				   w, texturePaint[5].getImage().getHeight());

		// fill bottom left
		g.setPaint(texturePaint[6]);
		g.fillRect(0, h-texturePaint[6].getImage().getHeight(),
				   texturePaint[6].getImage().getWidth(),
				   texturePaint[6].getImage().getHeight());

		// fill bottom right
		g.setPaint(texturePaint[7]);
		g.fillRect(w-texturePaint[7].getImage().getWidth(),
				   h-texturePaint[7].getImage().getHeight(),
				   texturePaint[7].getImage().getWidth(),
				   texturePaint[7].getImage().getHeight());
	}

}