package com.idega.games.blackjack;

// JFC
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import com.golden.gamedev.Game;
import com.golden.gamedev.GameLoader;
import com.golden.gamedev.object.AnimatedSprite;
import com.golden.gamedev.object.Background;
import com.golden.gamedev.object.GameFont;
import com.golden.gamedev.object.PlayField;
import com.golden.gamedev.object.Sprite;
import com.golden.gamedev.object.SpriteGroup;
import com.golden.gamedev.object.Timer;
import com.golden.gamedev.object.background.ImageBackground;
import com.golden.gamedev.object.collision.BasicCollisionGroup;
import com.golden.gamedev.object.sprite.VolatileSprite;

/**
 * It's time to play!
 * 
 * Objective: show how to use playfield to automate all things!
 */
public class BlackJack extends Game {

	PlayField playfield; // the game playfield
	Background background;
	SpriteGroup PLAYER_GROUP;
	SpriteGroup PROJECTILE_GROUP;
	SpriteGroup ENEMY_GROUP;
	AnimatedSprite plane;
	Timer moveTimer; // to set enemy behaviour
	// for moving left to right, right to left
	ProjectileEnemyCollision2 collision;
	GameFont font;
	

	BufferedImage[] cards = new BufferedImage[65];
	
	BufferedImage[] leafs = new BufferedImage[13];
	BufferedImage[] diamonds = new BufferedImage[13];
	BufferedImage[] hearts = new BufferedImage[13];
	BufferedImage[] spades = new BufferedImage[13];
	BufferedImage[] jokersAndBack = new BufferedImage[13];

	/** ************************************************************************* */
	/** ************************** GAME SKELETON ******************************** */
	/** ************************************************************************* */
	public void initResources() {
		// create the game playfield
		playfield = new PlayField();
		// associate the playfield with a background
		background = new ImageBackground(getImage("resources/cards/BarroomBJBackground.png"), 640, 480);
		playfield.setBackground(background);
		// create our plane sprite
		loadModernCardImages();
		
		plane = new AnimatedSprite(cards, getWidth()/2, getHeight()/2);
		plane.setSpeed(3,0);
		plane.setAnimate(true);
		plane.setLoopAnim(true);
		
		
		// ///// create the sprite group ///////
		PLAYER_GROUP = new SpriteGroup("Player");
		// no need to set the background for each group, we delegated it to
		// playfield
		// PLAYER_GROUP.setBackground(background);
		PROJECTILE_GROUP = new SpriteGroup("Projectile");
		// add all groups into our playfield
		playfield.addGroup(PLAYER_GROUP);
		playfield.addGroup(PROJECTILE_GROUP);
		// use shortcut, creating group and adding it to playfield in one step
		ENEMY_GROUP = playfield.addGroup(new SpriteGroup("Enemy"));
		// ///// insert sprites into the sprite group ///////
		PLAYER_GROUP.add(plane);
		// inserts sprites in rows to ENEMY_GROUP
		BufferedImage image = getImage("resources/plane1.png");
		int startX = 10, startY = 30; // starting coordinate
		for (int j = 0; j < 4; j++) { // 4 rows
			for (int i = 0; i < 7; i++) { // 7 sprites in a row
				Sprite enemy = new Sprite(image, startX + (i * 80), startY + (j * 70));
				enemy.setHorizontalSpeed(0.04);
				ENEMY_GROUP.add(enemy);
			}
		}
		// init the timer to control enemy sprite behaviour
		// (moving left-to-right, right-to-left)
		moveTimer = new Timer(2000); // every 2 secs the enemies reverse its
										// speed
		// ///// register collision ///////
		collision = new ProjectileEnemyCollision2(this);
		// register collision to playfield
		playfield.addCollisionGroup(PROJECTILE_GROUP, ENEMY_GROUP, collision);
		font = fontManager.getFont(getImages("resources/font.png", 20, 3), " !            .,0123"
				+ "456789:   -? ABCDEFG" + "HIJKLMNOPQRSTUVWXYZ ");
	}

	/**
	 * 
	 */
	protected void loadCardImages() {
		cards = getImages("resources/cards/svg-cards-2.0-1024.png",13,5);
		
		int counter = 0;
		for (int i = 0; i < cards.length; i++) {
			if(i>=0 && i<=12){
				leafs[counter] = cards[i];
			}
			else if(i>=13 && i<=26){
				diamonds[counter] = cards[i];
			}
			else if(i>=27 && i<=40){
				hearts[counter] = cards[i];
			}
			else if(i>=41 && i<=52){
				spades[counter] = cards[i];
			}
			else{
				jokersAndBack[counter] = cards[i];
			}
			
			counter++;
			if(counter==13){
				counter = 0;
			}
			
		}
	}

	protected void loadModernCardImages() {
		cards = getImages("resources/cards/modern-cards.jpg",11,5);
		
		int counter = 0;
		int secondCounter = 0;
		for (int i = 0; i < cards.length; i++) {
			
			if(i>=0 && i<=1){
				jokersAndBack[secondCounter] = cards[i];
				secondCounter++;
			}
			else if(i>=0 && i<=14){
				leafs[counter] = cards[i];
				counter++;
			}
			else if(i>=15 && i<=27){
				diamonds[counter] = cards[i];
				counter++;
			}
			else if(i>=28 && i<=41){
				hearts[counter] = cards[i];
				counter++;
			}
			else if(i>=42 && i<=53){
				spades[counter] = cards[i];
				counter++;
			}
			
			if(counter==13){
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
		if (moveTimer.action(elapsedTime)) {
			// reverse all enemies' speed
			Sprite[] sprites = ENEMY_GROUP.getSprites();
			int size = ENEMY_GROUP.getSize();
			// iterate the sprites
			for (int i = 0; i < size; i++) {
				// reverse sprite velocity
				sprites[i].setHorizontalSpeed(-sprites[i].getHorizontalSpeed());
			}
		}
		// control the sprite with arrow key
		double speedX = 0;
		if (keyDown(KeyEvent.VK_LEFT))
			speedX = -0.1;
		if (keyDown(KeyEvent.VK_RIGHT))
			speedX = 0.1;
		plane.setHorizontalSpeed(speedX);
		// firing!!
		if (keyPressed(KeyEvent.VK_CONTROL)) {
			// create projectile sprite
			Sprite projectile = new Sprite(getImage("resources/projectile.png"));
			projectile.setLocation(plane.getX() + 16.5, plane.getY() - 16);
			projectile.setVerticalSpeed(-0.2);
			// add it to PROJECTILE_GROUP
			PROJECTILE_GROUP.add(projectile);
			// play fire sound
			playSound("resources/sound1.wav");
		}
		// toggle ppc
		if (keyPressed(KeyEvent.VK_ENTER)) {
			collision.pixelPerfectCollision = !collision.pixelPerfectCollision;
		}
		background.setToCenter(plane);
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
		font.drawString(g, "ARROW KEY : MOVE", 10, 10);
		font.drawString(g, "CONTROL   : FIRE", 10, 30);
		font.drawString(g, "ENTER     : TOGGLE PPC", 10, 50);
		if (collision.pixelPerfectCollision) {
			font.drawString(g, " USE PIXEL PERFECT COLLISION ", GameFont.RIGHT, 0, 460, getWidth());
		}
	}

	/** ************************************************************************* */
	/** *************************** START-POINT ********************************* */
	/** ************************************************************************* */
	public static void main(String[] args) {
		GameLoader game = new GameLoader();
		game.setup(new BlackJack(), new Dimension(640, 480), false);
		game.start();
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
		s1.setActive(false); // the projectile is set to non-active
		s2.setActive(false); // the enemy is set to non-active
		// show explosion on the center of the exploded enemy
		// we use VolatileSprite -> sprite that animates once and vanishes
		// afterward
		BufferedImage[] images = owner.getImages("resources/explosion.png", 7, 1);
		VolatileSprite explosion = new VolatileSprite(images, s2.getX(), s2.getY());
		// directly add to playfield without using SpriteGroup
		// the sprite is added into a reserved extra sprite group in playfield
		// extra sprite group is used especially for animation effects in game
		owner.playfield.add(explosion);
	}
}