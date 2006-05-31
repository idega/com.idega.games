/*
 * Created on 13/03/2006 By Anderson Fernandes do Vale You can use the Andy's
 * ParticleSystem for free! just a litle credits will be welcome :) enjoy it!
 * 
 * any bugs, questions or suggestions, mail me andersonfe@gmail.com
 */
package com.idega.games.test.particles;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Transparency;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import particle.ParticleHandler;
import com.golden.gamedev.Game;
import com.golden.gamedev.GameLoader;
import com.golden.gamedev.object.Background;
import com.golden.gamedev.object.PlayField;
import com.golden.gamedev.object.background.ColorBackground;
import com.golden.gamedev.util.ImageUtil;

/**
 * @author Anderson
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Tester extends Game {

	PlayField campo = new PlayField();
	ParticleHandler part;
	Background bg;
	BufferedImage image[];

	public void initResources() {
		bg = new ColorBackground(Color.black);
		// *********** first, setup the particles behavor ********
		String partName = "Parts0"; // name of the particles group
		int particlesSec = 500; // particles per second
		double gForce = 0.0; // gravity force
		int gAngle = 90; // gravity direction
		int partAngle = -90; // initial particles direction
		int openAngle = 360;// spreed (CCW)
		int partLife = 1000;// maximum life in seconds of each particle
		double partForce = 0.2;// initial force of eatch particle
		int variation = 1000;// variation of particles starting
		boolean reSpaw = false;// just one time or respaw?
		double friction = .03;// breaks the particle
		// now we get a animated sequence image used for each particle
		try {
			image = ImageUtil.getImages(new URL("file:///Users/shared/idega/workspace-maven/com.idega.games/resources/particles/part3.png"), 7, 1, Transparency.TRANSLUCENT);
//			image = ImageUtil.getImages(new URL("file:///Users/shared/idega/workspace-maven/com.idega.games/resources/particles/part7.png"), 7, 1, Transparency.TRANSLUCENT);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		part = new ParticleHandler(partName, particlesSec, gForce, gAngle, partAngle, openAngle, getMouseY(),
				getMouseY(), partLife, partForce, variation, reSpaw, friction, image);
		campo.addGroup(part.getParticles());
		campo.setBackground(bg);
	}

	public void update(long td) {
		if (keyPressed(KeyEvent.VK_ESCAPE)) {
			notifyExit();
		}
		if (click()) {
			// set the initial particles location
			part.setLocation(getMouseX(), getMouseY());
			// starts the particles emanation
			part.start();
		}
		campo.update(td);
	}

	public void render(Graphics2D g) {
		campo.render(g);
	}

	public static void main(String args[]) {
		GameLoader gl = new GameLoader();
//		OpenGLGameLoader gl = new OpenGLGameLoader();

        // init game with OpenGL LWJGL fullscreen mode
        // 640x480 screen resolution
//        gl.setupLWJGL(new Tester(), new Dimension(640, 480), false);
		gl.setup(new Tester(), new Dimension(640, 480), false);
		gl.start();
	}
}