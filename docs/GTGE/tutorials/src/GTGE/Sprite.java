package com.golden.gamedev.object;

// JFC
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

// GTGE
import com.golden.gamedev.util.Utility;


/**
 * <code>Sprite</code> is the object in game that has graphical look and has its
 * own behaviour. <p>
 *
 * Every sprite is lived in a background, by default sprite is attached to
 * {@linkplain Background#getDefaultBackground default background}, always
 * remember to set the sprite to the game background or use {@link SpriteGroup}
 * class in {@link PlayField} to take care the sprite background set
 * automatically. <p>
 *
 * Sprite is located somewhere in the background, to set sprite location simply
 * use {@linkplain #setLocation(double, double)}. <br>
 * And to move the sprite use either by moving the sprite directly by using
 * (@linkplain #move(double, double)} or give speed to the sprite by using
 * {@linkplain #setSpeed(double, double)}. <p>
 *
 * In conjunction with sprite group/playfield, every sprite has active state,
 * this active state that determine whether the sprite is alive or not. Thus to
 * remove a sprite from playfield, simply set the sprite active state to false
 * by using {@linkplain #setActive(boolean) setActive(false)}. <p>
 *
 * To create sprite behaviour, always use {@link Timer} class utility in order
 * to make the sprite behaviour independent of frame rate.
 *
 * @see com.golden.gamedev.object.SpriteGroup
 * @see com.golden.gamedev.object.PlayField
 * @see com.golden.gamedev.object.Timer
 */
public class Sprite implements java.io.Serializable {

	///////// optimization /////////
//    private final Rectangle collisionOffset = new Rectangle(0,0,0,0); // offset collision


 /**************************** SPRITE BACKGROUND *****************************/

	private Background 	background;


 /***************************** SPRITE POSITION ******************************/

    private double 	x, y;
    private double	horizontalSpeed,
					verticalSpeed;	// in pixels per millisecond
	private double 	oldX, oldY;		// old position before this sprite moves

	/////////// optimization ///////////
	private static double screenX, screenY;	// screen position = x-background.x


 /****************************** SPRITE IMAGES *******************************/

    private transient BufferedImage image;

    /**
     * The width of this sprite.
     */
	protected int 	width;

    /**
     * The height of this sprite.
     */
	protected int 	height;


 /****************************** SPRITE FLAGS ********************************/

    private int 	id;			// to differentiate a sprite with another
    private Object	dataID;

    private int 	layer;		// for layering purpose only

	private boolean active = true;
    private boolean immutable;	// immutable sprite won't be disposed/thrown
								// from its group


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

    /**
     * Creates new <code>Sprite</code> with specified image and location.
     */
	public Sprite(BufferedImage image, double x, double y) {
        // init variables
  		this.x = oldX = x;
  		this.y = oldY = y;

		// sprite image
  		if (image != null) {
	  		this.image = image;
  			width = image.getWidth();
  			height = image.getHeight();
		}

		background = Background.getDefaultBackground();
    }

    /**
     * Creates new <code>Sprite</code> with specified image and located at (0, 0). <p>
     *
     * @see #setLocation(double, double)
     */
    public Sprite(BufferedImage image) {
	    this(image,0,0);
	}

    /**
     * Creates new <code>Sprite</code> with specified position and null image. <p>
     *
     * <b>Note: the image must be set before rendering by using
     * {@link #setImage(BufferedImage)}.</b>
     *
     * @see #setImage(BufferedImage)
     */
    public Sprite(double x, double y) {
	    this(null,x,y);
	}

    /**
     * Creates new <code>Sprite</code> with null image and located at (0, 0). <p>
     *
     * <b>Note: the image must be set before rendering by using
     * {@link #setImage(BufferedImage)}.</b>
     *
     * @see #setImage(BufferedImage)
     * @see #setLocation(double, double)
     */
	public Sprite() {
		this(0,0);
	}


 /****************************************************************************/
 /************************* SPRITE BACKGROUND ********************************/
 /****************************************************************************/

	/**
	 * Associates specified background with this sprite.
	 */
	public void setBackground(Background backgr) {
		background = backgr;
		if (background == null) {
			background = Background.getDefaultBackground();
		}
	}

	/**
	 * Returns the background where this sprite lived.
	 */
	public Background getBackground() {
		return background;
	}


 /****************************************************************************/
 /************************** IMAGE OPERATION *********************************/
 /****************************************************************************/

	/**
	 * Returns the image of this sprite.
	 */
	public BufferedImage getImage() {
		return image;
	}

	/**
	 * Sets specified image as this sprite image.
	 */
	public void setImage(BufferedImage image) {
		this.image = image;

		width = height = 0;
		if (image != null) {
	  		width = image.getWidth();
	  		height = image.getHeight();
		}
	}

	/**
	 * Returns the width of this sprite.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the height of this sprite.
	 */
    public int getHeight() {
		return height;
	}


 /****************************************************************************/
 /************************ MOVEMENT OPERATION ********************************/
 /****************************************************************************/

	/**
	 * Attempts to move this sprite to specified <code>xs</code>, <code>ys</code>
	 * location, if the sprite is right on specified location, this method
	 * will return true. <p>
	 *
	 * For example :
	 * <pre>
	 *    Sprite s;
	 *
	 *    public void update(long elapsedTime) {
	 *       // move sprite to 100, 100 with speed 0.1 pixel in a millisecond
	 *       if (s.moveTo(elapsedTime, 100, 100, 0.1) {
	 *          // sprite has arrived to 100, 100
	 *       }
	 *    }
	 * </pre>
	 */
	public boolean moveTo(long elapsedTime, double xs, double ys, double speed) {
		if (x == xs && y == ys) {
			return true;
		}

		double angle = 90 + Math.toDegrees(Math.atan2(ys-y, xs-x));
		double radians = Math.toRadians(angle);

		double vx = Math.sin(radians) * speed * elapsedTime,
			   vy = -Math.cos(radians) * speed * elapsedTime;

		boolean arriveX = false,
				arriveY = false;

		// checking x coordinate
		if (vx != 0) {
			if (vx > 0) {
				// moving right
				if (x + vx >= xs) {
					vx = xs - x;	// snap
					arriveX = true;
				}

			} else {
				// moving left
				if (x + vx <= xs) {
					vx = xs - x;	// snap
					arriveX = true;
				}
			}

		} else if (x == xs) {
			arriveX = true;
		}

		// checking y coordinate
		if (vy != 0) {
			if (vy > 0) {
				// moving down
				if (y + vy >= ys) {
					vy = ys - y;	// snap
					arriveY = true;
				}

			} else {
				// moving up
				if (y + vy <= ys) {
					vy = ys - y;	// snap
					arriveY = true;
				}
			}

		} else if (y == ys) {
			arriveY = true;
		}

		move(vx, vy);

		return (arriveX && arriveY);
	}

    /**
     * Sets this sprite coordinate to specified location on the background.
     */
	public void setLocation(double xs, double ys) {
	    oldX = x = xs;
		oldY = y = ys;
	}

    /**
     * Moves this sprite as far as delta x (dx) and delta y (dy).
     */
    public void move(double dx, double dy) {
	    if (dx != 0) {
			oldX = x;
			x += dx;
		}

		if (dy != 0) {
			oldY = y;
			y += dy;
		}
	}


    /**
     * Moves sprite <code>x</code> coordinate as far as delta x (dx).
     */
	public void moveX(double dx) {
		if (dx != 0) {
			oldX = x;
			x += dx;
		}
	}

    /**
     * Moves sprite <code>y</code> coordinate as far as delta y (dy).
     */
    public void moveY(double dy) {
	    if (dy != 0) {
			oldY = y;
			y += dy;
		}
	}

	/**
	 * Sets sprite <code>x</code> coordinate.
	 */
	public void setX(double xs) {
		oldX = x = xs;
	}

	/**
	 * Sets sprite <code>y</code> coordinate.
	 */
    public void setY(double ys) {
		oldY = y = ys;
	}

	/**
	 * Forces sprite <code>x</code> position to specified coordinate. <p>
	 *
	 * The difference between {@link #setX(double)} with this method : <br>
	 * <code>setX(double)</code> changes the sprite old position (oldX = xs),
	 * while using <code>forceX(double)</code> <b>the old position is n
	 * ot changed</b>. <p>
	 *
	 * This method is used on collision check to move the sprite, but still keep
	 * the sprite old position value.
	 */
	public void forceX(double xs) {
		x = xs;
	}

	/**
	 * Forces sprite <code>y</code> position to specified coordinate. <p>
	 *
	 * The difference between {@link #setY(double)} with this method : <br>
	 * <code>setY(double)</code> changes the sprite old position (oldY = ys),
	 * while using <code>forceY(double)</code> <b>the old position is n
	 * ot changed</b>. <p>
	 *
	 * This method is used on collision check to move the sprite, but still keep
	 * the sprite old position value.
	 */
	public void forceY(double ys) {
		y = ys;
	}

    /**
     * Returns sprite <code>x</code> coordinate.
     */
	public double getX() {
		return x;
	}

    /**
     * Returns sprite <code>y</code> coordinate.
     */
    public double getY() {
		return y;
	}

    /**
     * Returns sprite <code>x</code> coordinate before the sprite moving to the
     * current position.
     */
    public double getOldX() {
		return oldX;
	}

    /**
     * Returns sprite <code>y</code> coordinate before the sprite moving to the
     * current position.
     */
    public double getOldY() {
		return oldY;
	}


 /****************************************************************************/
 /*************************** SPEED VARIABLES ********************************/
 /****************************************************************************/

	/**
	 * Sets the speed of this sprite, the speed is based on actual time in
	 * milliseconds, 1 means the sprite is moving as far as 1000 (1x1000ms)
	 * pixels in a second. Negative value (-1) means the sprite moving backward.
	 */
	public void setSpeed(double vx, double vy) {
		horizontalSpeed = vx;
		verticalSpeed = vy;
	}

	/**
	 * Sets horizontal speed of the sprite, the speed is based on actual time in
	 * milliseconds, 1 means the sprite is moving as far as 1000 (1x1000ms)
	 * pixels in a second to the right, while negative value (-1) means the
	 * sprite is moving to the left.
	 */
    public void setHorizontalSpeed(double vx) {
		horizontalSpeed = vx;
	}

	/**
	 * Sets vertical speed of the sprite, the speed is based on actual time in
	 * milliseconds, 1 means the sprite is moving as far as 1000 (1x1000ms)
	 * pixels in a second to the bottom, while negative value (-1) means the
	 * sprite is moving to the top.
	 */
    public void setVerticalSpeed(double vy) {
		verticalSpeed = vy;
	}

	/**
	 * Moves sprite with specified angle, and speed. <p>
	 *
	 * The angle is as followed:
	 * <pre>
	 *   0°   : moving to top (12 o'clock)
	 *   90°  : moving to right (3 o'clock)
	 *   180° : moving to bottom (6 o'clock)
	 *   270° : moving to left (9 o'clock)
	 * </pre>
	 */
	public void setMovement(double speed, double angleDir) {
		//convert degrees to radians
		double radians = Math.toRadians(angleDir);

		setSpeed(Math.sin(radians)*speed, -Math.cos(radians)*speed);
	}

	/**
	 * Accelerates sprite horizontal speed by <code>accel</code> and limit the
	 * speed to <code>maxSpeed</code>. <p>
	 *
	 * This is used to create momentum speed (slowly increase/decrease the
	 * sprite speed). <p>
	 *
	 * For example :
	 * <pre>
	 *    Sprite s;
	 *
	 *    public void update(long elapsedTime) {
	 *       // accelerate sprite speed by 0.002
	 *       // and limit the maximum speed to 4
	 *
	 *       // moving right
	 *       s.addHorizontalSpeed(elapsedTime, 0.002, 4);
	 *
	 *       // moving left
	 *       s.addHorizontalSpeed(elapsedTime, -0.002, -4);
	 *
	 *       // momentum stop
	 *       s.addHorizontalSpeed(elapsedTime,
	 *                            (s.getHorizontalSpeed() > 0) ? -0.002 : 0.002,
	 *                            0);
	 *    }
	 * </pre>
	 */
	public void addHorizontalSpeed(long elapsedTime, double accel, double maxSpeed) {
		if (accel == 0 || elapsedTime == 0) {
			return;
		}

		horizontalSpeed += accel * elapsedTime;

		if (accel < 0) {
			if (horizontalSpeed < maxSpeed) {
				horizontalSpeed = maxSpeed;
			}

		} else {
			if (horizontalSpeed > maxSpeed) {
				horizontalSpeed = maxSpeed;
			}
		}
	}

	/**
	 * Accelerates sprite vertical speed by <code>accel</code> and limit the
	 * speed to <code>maxSpeed</code>. <p>
	 *
	 * This is used to create momentum speed (slowly increase/decrease the
	 * sprite speed). <p>
	 *
	 * For example :
	 * <pre>
	 *    Sprite s;
	 *
	 *    public void update(long elapsedTime) {
	 *       // accelerate sprite speed by 0.002
	 *       // and limit the maximum speed to 4
	 *
	 *       // moving down
	 *       s.addVerticalSpeed(elapsedTime, 0.002, 4);
	 *
	 *       // moving up
	 *       s.addVerticalSpeed(elapsedTime, -0.002, -4);
	 *
	 *       // momentum stop
	 *       s.addVerticalSpeed(elapsedTime,
	 *                          (s.getVerticalSpeed() > 0) ? -0.002 : 0.002,
	 *                          0);
	 *    }
	 * </pre>
	 */
	public void addVerticalSpeed(long elapsedTime, double accel, double maxSpeed) {
		if (accel == 0 || elapsedTime == 0) {
			return;
		}

		verticalSpeed += accel * elapsedTime;

		if (accel < 0) {
			if (verticalSpeed < maxSpeed) {
				verticalSpeed = maxSpeed;
			}

		} else {
			if (verticalSpeed > maxSpeed) {
				verticalSpeed = maxSpeed;
			}
		}
	}

	/**
	 * Returns horizontal speed of the sprite. <p>
	 *
	 * Positive means the sprite is moving to right, and negative means the
	 * sprite is moving to left.
	 */
    public double getHorizontalSpeed() {
		return horizontalSpeed;
	}

	/**
	 * Returns vertical speed of the sprite. <p>
	 *
	 * Positive means the sprite is moving to bottom, and negative means the
	 * sprite is moving to top.
	 */
    public double getVerticalSpeed() {
		return verticalSpeed;
	}


 /****************************************************************************/
 /********************* OTHER SPRITE POSITION FUNCTIONS **********************/
 /****************************************************************************/

    /**
     * Returns sprite <code>x</code> coordinate relative to screen area.
     */
    public double getScreenX() {
		return x - background.getX() + background.getClip().x;
	}

    /**
     * Returns sprite <code>y</code> coordinate relative to screen area.
     */
	public double getScreenY() {
		return y - background.getY() + background.getClip().y;
	}

	/**
	 * Returns whether the screen is still on background screen area in
	 * specified offset.
	 */
	public boolean isOnScreen(int leftOffset, int topOffset,
							  int rightOffset, int bottomOffset) {
		screenX = x - background.getX();
		screenY = y - background.getY();

		return (screenX + width > -leftOffset &&
				screenY + height > -topOffset &&
				screenX < background.getClip().width + rightOffset &&
				screenY < background.getClip().height + bottomOffset);
	}

	/**
	 * Returns whether the screen is still on background screen area.
	 */
	public boolean isOnScreen() {
		return isOnScreen(0,0,0,0);
	}


 /****************************************************************************/
 /*************************** UPDATE SPRITE **********************************/
 /****************************************************************************/

    /**
     * Updates this sprite.
     */
	public void update(long elapsedTime) {
		updateMovement(elapsedTime);
	}

	/**
	 * Updates sprite movement.
	 */
	protected void updateMovement(long elapsedTime) {
		move(horizontalSpeed * elapsedTime,
			 verticalSpeed * elapsedTime);
	}


 /****************************************************************************/
 /*************************** RENDER SPRITE **********************************/
 /****************************************************************************/

    /**
     * Renders this sprite to specified graphics context.
     *
     * @param g graphics context
     */
	public void render(Graphics2D g) {
		screenX = x-background.getX();
		screenY = y-background.getY();

		// check whether the sprite is still on screen rendering area
		if (screenX + width <= 0 || screenY + height <= 0 ||
			screenX > background.getClip().width ||
			screenY > background.getClip().height) return;

		screenX += background.getClip().x;
		screenY += background.getClip().y;

		render(g, (int) screenX, (int) screenY);
	}

	/**
	 * Renders sprite image to specified graphics context and specified location.
	 *
	 * @param g graphics context
	 * @param x screen x-coordinate
	 * @param y screen y-coordinate
	 */
	public void render(Graphics2D g, int x, int y) {
		g.drawImage(image, x, y, null);
	}


 /****************************************************************************/
 /**************************** SPRITE FLAGS **********************************/
 /****************************************************************************/

    /**
     * Returns sprite ID, ID is used to mark a sprite from other sprite.
     */
	public int getID() {
		return id;
	}

    /**
     * Sets sprite ID, ID is used to mark a sprite from other sprite.
     */
    public void setID(int id) {
		this.id = id;
	}


	/**
	 * Returns sprite data ID, ID is used to mark a sprite from other sprite.
	 */
	public Object getDataID() {
		return dataID;
	}

	/**
	 * Sets sprite data ID, ID is used to mark a sprite from other sprite.
	 */
	public void setDataID(Object dataID) {
		this.dataID = dataID;
	}


	/**
	 * Returns the layer of this sprite.
	 *
	 * @see #setLayer(int)
	 */
	public int getLayer() {
		return layer;
	}

	/**
	 * Sets the layer of this sprite. <p>
	 *
	 * Layer is used for z-order rendering. Use this along with
	 * {@link PlayField#setComparator(Comparator)} or
	 * {@link SpriteGroup#setComparator(Comparator)} for that purpose.
	 *
	 * @see #getLayer()
	 */
	public void setLayer(int i) {
		layer = i;
	}


    /**
     * Returns active state of this sprite.
     */
	public boolean isActive() {
		return active;
	}

    /**
     * Sets active state of this sprite, only active sprite will be updated and
     * rendered and check for collision. <p>
     *
     * Inactive sprite is same as dead sprite, it won't be updated nor rendered,
     * and only wait to be disposed (if the sprite is not immutable). <p>
     *
     * The proper way to remove a sprite from the game, is by setting sprite
     * active state to false (Sprite.setActive(false)).
     *
     * @see #setImmutable(boolean)
     */
	public void setActive(boolean b) {
		active = b;
	}


	/**
	 * Returns whether this sprite is immutable or not.
	 */
	public boolean isImmutable() {
		return immutable;
	}

	/**
	 * Sets immutable state of this sprite, immutable sprite means the sprite
	 * won't be removed from its group even though the sprite is not active. <p>
	 *
	 * This state is used for optimization by reusing inactive sprite rather
	 * than making new sprite each time. <p>
	 *
	 * Usually used for many, small, short live, and frequently used sprites
	 * such as projectile in shooter game. Thus rather than making a new sprite
	 * for every projectile that can cause performance degrade, the inactive
	 * projectiles can be reuse again and again. <p>
	 *
	 * <b>WARNING:</b> Immutable sprite won't be disposed by Java garbage
	 * collector until the sprite is manually removed from its group using
	 * {@link com.golden.gamedev.object.SpriteGroup#removeImmutableSprites()}.
	 *
	 * @see com.golden.gamedev.object.SpriteGroup#getInactiveSprite()
	 * @see com.golden.gamedev.object.SpriteGroup#removeImmutableSprites()
	 * @see #setActive(boolean)
	 */
	public void setImmutable(boolean b) {
		immutable = true;
	}


	/**
	 * Returns the distance of this sprite from the specified sprite. <p>
	 *
	 * Used this method to check whether the specified sprite is in this sprite
	 * range area or not. <p>
	 *
	 * This method can be used for :
	 * <ul>
	 *   <li>Determining sprite attack range.</li>
	 *   <li>Sprite aura that affecting surrounding unit.</li>
	 *   <li>Activating this sprite to chase player whenever the player come
	 * 		 closer to certain distance of this sprite.</li>
	 * </ul>
	 */
	public double getDistance(Sprite other) {
		return Math.sqrt(Math.pow(getX()-other.getX(),2) +
						 Math.pow(getY()-other.getY(),2));
	}


//    private static int garbagecount = 0;
//    protected void finalize() throws Throwable {
//        System.out.println("Total sprite garbaged = " + (++garbagecount) + " = " + this);
//        super.finalize();
//    }

}