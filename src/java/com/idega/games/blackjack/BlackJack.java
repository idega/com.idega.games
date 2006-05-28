package com.idega.games.blackjack;

// JFC
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import vxp.QTLivePixelSource;
import vxp.VideoListener;
import com.golden.gamedev.Game;
import com.golden.gamedev.GameLoader;
import com.golden.gamedev.object.Background;
import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.object.PlayField;
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.SpriteGroup;
import com.golden.gamedev.object.background.ImageBackground;
import com.golden.gamedev.object.collision.BasicCollisionGroup;

/**
 * It's time to play!
 * 
 * Objective: show how to use playfield to automate all things!
 */
public class BlackJack extends Game implements VideoListener {

	// uncomment this when ready to ship!
	{
		distribute = true;
	}
	PlayField playfield; // the game playfield
	Background background;
	SpriteGroup PLAYER_CARDS;
	SpriteGroup DEALER_CARDS;
	
	Map playerTotals = new HashMap();
	
	// Timer moveTimer; // to set enemy behaviour
	// // for moving left to right, right to left
	// ProjectileEnemyCollision2 collision;
	HashMap cardValues;
	GameFont font;
	int nextCardCounter = 0;
	int width = 800;// 640;
	int height = 600;// 480;
	BufferedImage[] cards = new BufferedImage[65];
	BufferedImage[] leafs = new BufferedImage[13];
	BufferedImage[] diamonds = new BufferedImage[13];
	BufferedImage[] hearts = new BufferedImage[13];
	BufferedImage[] spades = new BufferedImage[13];
	BufferedImage[] jokersAndBack = new BufferedImage[13];
	QTLivePixelSource ps;
	BufferedImage currentFrameImage;
	int videoWidth = 320;// 160;//320, 640
	int videoHeight = 240;// 120;//240,480
	boolean videoInBackground = false;
	protected Sprite videoSprite;

	/** ************************************************************************* */
	/** ************************** GAME SKELETON ******************************** */
	/** ************************************************************************* */
	public void initResources() {
		// create the game playfield
		playfield = new PlayField();
		// associate the playfield with a background
		background = new ImageBackground(getImage("resources/cards/BarroomBJBackground.png"), width, height);
		playfield.setBackground(background);
		// create our plane sprite
		// loadModernCardImages();
		PLAYER_CARDS = new SpriteGroup("Player");
		playfield.addGroup(PLAYER_CARDS);
		DEALER_CARDS = new SpriteGroup("Dealer");
		playfield.addGroup(DEALER_CARDS);
		videoSprite = new Sprite(width - videoWidth, 0);
		playfield.add(videoSprite);
		loadCardImages();
		newGame(true);
		// Collections.shuffle(Arrays.asList(cards));
		// plane = new AnimatedSprite(cards, getWidth()/2, getHeight()/2);
		// plane.setSpeed(30, 0);
		// plane.setSpeed(3,0);
		// plane.setAnimate(true);
		// plane.setLoopAnim(true);
		// playfield.add(plane);
		try {
			ps = new QTLivePixelSource(videoWidth, videoHeight, 100);
			ps.addVideoListener(this);
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.print("Failed to initialize video, most likely there is no camera or it is in use by another application");
		}
		dealOneCardToDealer(getNextCard());
		dealOneCardToPlayer(getNextCard());
		dealOneCardToPlayer(getNextCard());
		// ///// create the sprite group ///////
		// PLAYER_GROUP = new SpriteGroup("Player");
		// no need to set the background for each group, we delegated it to
		// playfield
		// PLAYER_GROUP.setBackground(background);
		// PROJECTILE_GROUP = new SpriteGroup("Projectile");
		// add all groups into our playfield
		// playfield.addGroup(PLAYER_GROUP);
		// playfield.addGroup(PROJECTILE_GROUP);
		// use shortcut, creating group and adding it to playfield in one step
		// ENEMY_GROUP = playfield.addGroup(new SpriteGroup("Enemy"));
		// ///// insert sprites into the sprite group ///////
		// PLAYER_GROUP.add(plane);
		// inserts sprites in rows to ENEMY_GROUP
		// BufferedImage image = getImage("resources/plane1.png");
		// int startX = 10, startY = 30; // starting coordinate
		// for (int j = 0; j < 4; j++) { // 4 rows
		// for (int i = 0; i < 7; i++) { // 7 sprites in a row
		// Sprite enemy = new Sprite(image, startX + (i * 80), startY + (j *
		// 70));
		// enemy.setHorizontalSpeed(0.04);
		// ENEMY_GROUP.add(enemy);
		// }
		// }
		// init the timer to control enemy sprite behaviour
		// (moving left-to-right, right-to-left)
		// moveTimer = new Timer(2000); // every 2 secs the enemies reverse its
		// speed
		// ///// register collision ///////
		// collision = new ProjectileEnemyCollision2(this);
		// register collision to playfield
		// playfield.addCollisionGroup(PROJECTILE_GROUP, ENEMY_GROUP,
		// collision);
		font = fontManager.getFont(getImages("resources/font.png", 20, 3), " !            .,0123"
				+ "456789:   -? ABCDEFG" + "HIJKLMNOPQRSTUVWXYZ ");
	}

	protected BufferedImage getNextCard() {
		if (nextCardCounter == cards.length) {
			nextCardCounter = 0;
		}
		BufferedImage img = cards[nextCardCounter++];
		Integer value = (Integer) cardValues.get(img);
		if (value == null) {
			System.out.println("Card has no value");
		}
		else {
			System.out.println("Card value = " + value.toString());
		}
		return img;
	}

	/**
	 * 
	 */
	protected void loadCardImages() {
		cards = getImages("resources/cards/svg-cards-2.0-1024.png", 13, 5, 0, 51);
		cardValues = new HashMap();
		int counter = 0;
		for (int i = 0; i < cards.length; i++) {
			if (i >= 0 && i <= 12) {
				if (i == 0) { // ACE
					cardValues.put(cards[i], new Integer(-1));
				}
				else if (i >= 9) { // TEN or more
					cardValues.put(cards[i], new Integer(10));
				}
				else {
					cardValues.put(cards[i], new Integer(i + 1));
				}
				leafs[counter] = cards[i];
			}
			else if (i >= 13 && i <= 25) {
				if (i == 13) { // ACE
					cardValues.put(cards[i], new Integer(-1));
				}
				else if (i >= 23) { // TEN or more
					cardValues.put(cards[i], new Integer(10));
				}
				else {
					cardValues.put(cards[i], new Integer((i % 13) + 1));
				}
				diamonds[counter] = cards[i];
			}
			else if (i >= 26 && i <= 38) {
				if (i == 26) { // ACE
					cardValues.put(cards[i], new Integer(-1));
				}
				else if (i >= 35) { // TEN or more
					cardValues.put(cards[i], new Integer(10));
				}
				else {
					cardValues.put(cards[i], new Integer((i % 13) + 1));
				}
				hearts[counter] = cards[i];
			}
			else if (i >= 39 && i <= 51) {
				if (i == 39) { // ACE
					cardValues.put(cards[i], new Integer(-1));
				}
				else if (i >= 48) { // TEN or more
					cardValues.put(cards[i], new Integer(10));
				}
				else {
					cardValues.put(cards[i], new Integer((i % 13) + 1));
				}
				spades[counter] = cards[i];
			}
			// else{
			// jokersAndBack[counter] = cards[i];
			// }
			counter++;
			if (counter == 13) {
				counter = 0;
			}
		}
	}

	protected void loadModernCardImages() {
		cards = getImages("resources/cards/modern-cards.jpg", 11, 5);
		int counter = 0;
		int secondCounter = 0;
		for (int i = 0; i < cards.length; i++) {
			if (i >= 0 && i <= 1) {
				jokersAndBack[secondCounter] = cards[i];
				secondCounter++;
			}
			else if (i >= 0 && i <= 14) {
				leafs[counter] = cards[i];
				counter++;
			}
			else if (i >= 15 && i <= 27) {
				diamonds[counter] = cards[i];
				counter++;
			}
			else if (i >= 28 && i <= 41) {
				hearts[counter] = cards[i];
				counter++;
			}
			else if (i >= 42 && i <= 53) {
				spades[counter] = cards[i];
				counter++;
			}
			if (counter == 13) {
				counter = 0;
			}
		}
	}

	public void update(long elapsedTime) {
		// no need to update the background and the group one by one
		// the playfield has taken this job!
		// background.update(elapsedTime);
		// PLAYER_GROUP.update(elapsedTime);
		// ENEMY_GROUP.update(elapsedTime);
		// PROJECTILE_GROUP.update(elapsedTime);
		// collision.checkCollision();
		// playfield update all things and check for collision
		playfield.update(elapsedTime);
		// enemy sprite movement timer
		// if (moveTimer.action(elapsedTime)) {
		// reverse all enemies' speed
		// System.out.println("Timer YEAH");
		// Sprite[] sprites = ENEMY_GROUP.getSprites();
		// int size = ENEMY_GROUP.getSize();
		// // iterate the sprites
		// for (int i = 0; i < size; i++) {
		// // reverse sprite velocity
		// sprites[i].setHorizontalSpeed(-sprites[i].getHorizontalSpeed());
		// }
		// }
		// control the sprite with arrow key
		// double speedX = 0;
		// if (keyDown(KeyEvent.VK_LEFT))
		// speedX = -0.1;
		// if (keyDown(KeyEvent.VK_RIGHT))
		// speedX = 0.1;
		// plane.setHorizontalSpeed(speedX);
		// firing!!
		if (keyPressed(KeyEvent.VK_CONTROL)) {
			// create projectile sprite
			// Sprite projectile = new
			// Sprite(getImage("resources/projectile.png"));
			// projectile.setLocation(plane.getX() + 16.5, plane.getY() - 16);
			// projectile.setVerticalSpeed(-0.2);
			// // add it to PROJECTILE_GROUP
			// PROJECTILE_GROUP.add(projectile);
			// // play fire sound
			// playSound("resources/sound1.wav");
		}
		// toggle ppc
		if (keyPressed(KeyEvent.VK_ENTER)) {
			// collision.pixelPerfectCollision =
			// !collision.pixelPerfectCollision;
		}
		if (keyPressed(KeyEvent.VK_H)) {
			// TEMP hack
			if (getPlayerTotalCardValue(PLAYER_CARDS) > 21) {
				newGame(false);
				dealOneCardToPlayer(getNextCard());
			}
			// HIT
			dealOneCardToPlayer(getNextCard());
		}
		if (keyPressed(KeyEvent.VK_S)) {
			// TEMP hack
			if (getPlayerTotalCardValue(DEALER_CARDS) > 21) {
				newGame(false);
				dealOneCardToDealer(getNextCard());
			}
			// HIT
			dealOneCardToDealer(getNextCard());
		}
		if (keyPressed(KeyEvent.VK_R)) {
			// HIT
			newGame(true);
			dealOneCardToPlayer(getNextCard());
			dealOneCardToPlayer(getNextCard());
		}
		if (keyPressed(KeyEvent.VK_R)) {
			// HIT
			newGame(true);
			dealOneCardToPlayer(getNextCard());
			dealOneCardToPlayer(getNextCard());
		}
		if (keyPressed(KeyEvent.VK_B)) {
			// HIT
			videoInBackground = !videoInBackground;
			if (videoInBackground) {
				videoSprite.setActive(false);
				playfield.setBackground(new ImageBackground(ps.getImage(), width, height));
			}
			else {
				playfield.setBackground(background);
				videoSprite.setActive(true);
				playfield.add(videoSprite);
			}
		}
		// background.setToCenter(plane);
	}
	
	protected int updatePlayerTotalCardValue(SpriteGroup cardGroup) {
		String playerName = cardGroup.getName();
		int total = 0;
		Sprite[] sprites = cardGroup.getSprites();
		int size = cardGroup.getSize();
		int numOfAces = 0;
		for (int i = 0; i < size; i++) {
			BufferedImage img = sprites[i].getImage();
			int val = ((Integer) cardValues.get(img)).intValue();
			if (val == -1) {
				++numOfAces;
			}
			else {
				total += val;
			}
		}
		if (numOfAces > 0) {
			for (int i = 0; i < numOfAces; i++) {
				if ((total + 11) > 21) {
					total += 1;
					System.out.println("Using ACE as 1");
				}
				else {
					total += 11;
					System.out.println("Using ACE as 11");
				}
			}
		}
		System.out.println("Counter the total ... to be " + total);

		playerTotals.put(playerName, new Integer(total));
	
		
		return total;
		
	}

	protected int getPlayerTotalCardValue(SpriteGroup cardGroup) {
		String playerName = cardGroup.getName();
		return ((Integer)playerTotals.get(playerName)).intValue();
	}

	protected void newGame(boolean shuffle) {
		if (shuffle) {
			System.out.println("Shuffling Cards");
			Collections.shuffle(Arrays.asList(cards));
			nextCardCounter = 0;
		}
		else {
			System.out.println("New Game");
		}
		PLAYER_CARDS.reset();
		DEALER_CARDS.reset();
	}

	protected void dealOneCardToPlayer(BufferedImage nextCard) {
		int size = PLAYER_CARDS.getSize();
		Sprite sNextCard = new Sprite(nextCard, 250 + (((size % 5)) * 80), 340);
		PLAYER_CARDS.add(sNextCard);
		updatePlayerTotalCardValue(PLAYER_CARDS);
	}

	protected void dealOneCardToDealer(BufferedImage nextCard) {
		int size = DEALER_CARDS.getSize();
		Sprite sNextCard = new Sprite(nextCard, 250 + (((size % 5)) * 80), 100);
		DEALER_CARDS.add(sNextCard);
		updatePlayerTotalCardValue(DEALER_CARDS);
	}

	public void render(Graphics2D g) {
		// (once again) no need to render the background and the group one by
		// one
		// the playfield has taken this job!
		// background.render(g);
		// PLAYER_GROUP.render(g);
		// ENEMY_GROUP.render(g);
		// PROJECTILE_GROUP.render(g);
		playfield.render(g);
		// draw info text
		int totalPlayer = getPlayerTotalCardValue(PLAYER_CARDS);
		font.drawString(g, "H KEY : HIT ME !!", 10, 10);
		font.drawString(g, "S KEY : STAND", 10, 30);
		font.drawString(g, "B KEY : VIDEO TOGGLE!", 10, 50);
		font.drawString(g, "TOTAL DEALER: " + getPlayerTotalCardValue(DEALER_CARDS) + "", 10, 70);
		font.drawString(g, "TOTAL : " + totalPlayer + "", 10, 90);
		if (totalPlayer > 21) {
			font.drawString(g, "BUSTED", 10, 110);
		}
		else if (totalPlayer == 21 && PLAYER_CARDS.getSize() == 2) {
			font.drawString(g, "BLACKJACK", 10, 70);
		}
		// font.drawString(g, "CONTROL : FIRE", 10, 30);
		// font.drawString(g, "ENTER : TOGGLE PPC", 10, 50);
		// if (collision.pixelPerfectCollision) {
		// font.drawString(g, " USE PIXEL PERFECT COLLISION ", GameFont.RIGHT,
		// 0, 460, getWidth());
		// }
	}

	/** ************************************************************************* */
	/** *************************** START-POINT ********************************* */
	/** ************************************************************************* */
	public static void main(String[] args) {
		GameLoader game = new GameLoader();
		game.setup(new BlackJack(), new Dimension(800, 600), false);
		game.setName("{iwrb} BlackJack v.01a");
		game.start();
	}

	public void newFrame() {
		ps.grabFrame();
		// playfield.setBackground(new ImageBackground(ps.getImage(), width,
		// height));
		videoSprite.setImage(ps.getImage());
	}
}

class ProjectileEnemyCollision2 extends BasicCollisionGroup {

	BlackJack owner;

	public ProjectileEnemyCollision2(BlackJack owner) {
		this.owner = owner; // set the game owner
		// we use this for getting image and
		// adding explosion to playfield
	}

	// when projectiles (in group a) collided with enemy (in group b)
	// what to do?
	public void collided(Sprite s1, Sprite s2) {
		// we kill/remove both sprite!
		// s1.setActive(false); // the projectile is set to non-active
		// s2.setActive(false); // the enemy is set to non-active
		// show explosion on the center of the exploded enemy
		// we use VolatileSprite -> sprite that animates once and vanishes
		// afterward
		// BufferedImage[] images = owner.getImages("resources/explosion.png",
		// 7, 1);
		// VolatileSprite explosion = new VolatileSprite(images, s2.getX(),
		// s2.getY());
		// directly add to playfield without using SpriteGroup
		// the sprite is added into a reserved extra sprite group in playfield
		// extra sprite group is used especially for animation effects in game
		// owner.playfield.add(explosion);
	}
}