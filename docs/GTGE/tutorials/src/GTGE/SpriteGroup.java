package com.golden.gamedev.object;

// JFC
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;

// GTGE
import com.golden.gamedev.util.Utility;


/**
 * Group of sprites with common behaviour, for example PLAYER_GROUP, ENEMY_GROUP,
 * etc. This class maintain a growable sprite list (array of sprites). Each time
 * a sprite is added into this group, this group automatically adjust the size
 * of its sprites array. <p>
 *
 * <code>SpriteGroup</code> is used to store a list of sprites and also manage
 * the sprites updating, rendering, and collision check. <p>
 *
 * For example how to create and use sprite group :
 * <pre>
 *    SpriteGroup ENEMY_GROUP;
 *
 *    public void initResources() {
 *       // creates the enemy sprites
 *       Sprite asteroid = new Sprite();
 *       Sprite asteroid2 = new Sprite();
 *
 *       // creates and add the sprites into enemy group
 *       ENEMY_GROUP = new SpriteGroup("Enemy");
 *       ENEMY_GROUP.add(asteroid);
 *       ENEMY_GROUP.add(asteroid2);
 *    }
 *
 *    public void update(long elapsedTime) {
 *       // update all enemies at once
 *       ENEMY_GROUP.update(elapsedTime);
 *    }
 *
 *    public void render(Graphics2D g) {
 *       // render all enemies at once to the screen
 *       ENEMY_GROUP.render(g);
 *    }
 * </pre>
 *
 * @see com.golden.gamedev.object.PlayField
 * @see com.golden.gamedev.object.collision.CollisionGroup
 */
public class SpriteGroup {


 /************************* GROUP SPRITE FACTOR ******************************/

	// total 'empty' sprite (NULL sprite, allocation only)
	// reduce the cost of array enlargement operation
	private int expandFactor = 20;

	// removes inactive sprites every 15 seconds
	private Timer scanFrequence = new Timer(15000);


 /************************** GROUP PROPERTIES ********************************/

	private String      name;			// group name (for identifier only)
	private boolean		active = true;

	private Background 	background;

	private Comparator 	comparator;		// comparator for sorting sprite


 /******************** SPRITES THAT BELONG TO THIS GROUP *********************/

	private Sprite[]	sprites;		// member of this group
	private int			size;			// all non-null sprites (active + inactive)


 /****************************************************************************/
 /**************************** CONSTRUCTOR ***********************************/
 /****************************************************************************/

	/**
	 * Creates a new sprite group, with specified name. Name is used for group
	 * identifier only.
	 */
	public SpriteGroup(String name) {
		this.name = name;
	    background = Background.getDefaultBackground();

	    sprites = new Sprite[expandFactor];
    }


 /****************************************************************************/
 /*************************** INSERTION OPERATION ****************************/
 /****************************************************************************/

    /**
     * Inserts sprite at the bottom (last index) of this group. <p>
     *
     * Sprite at the last index (index = {@linkplain #getSize() size}-1)
     * is rendered on top of other sprites (last rendered).
     *
     * @see #add(int, Sprite)
     */
	public void add(Sprite member) {
	    sprites[size] = member;
		member.setBackground(background);

	    if (++size >= sprites.length) {
			// time to enlarge sprite storage
		    sprites = (Sprite[]) Utility.expand(sprites, expandFactor);
		}
	}

    /**
     * Inserts sprite at specified index, range from [0 -
     * {@linkplain #getSize() size}]. <p>
     *
     * Sprite at the first index (index = 0) is at the back of other sprites
     * (first rendered). <br>
     * Sprite at the last index (index = {@linkplain #getSize() size}-1)
     * is rendered on top of other sprites (last rendered).
     */
	public void add(int index, Sprite member) {
		if (index > size) index = size;

		if (index == size) {
			add(member);

		} else {
			// shift sprites by one at specified index
	    	System.arraycopy(sprites, index,
							 sprites, index+1,
							 size-index);
//			for (int i=size-1;i >= index;i--) {
//				sprites[i+1] = sprites[i];
//			}
			sprites[index] = member;
			member.setBackground(background);

		    if (++size >= sprites.length) {
				// time to enlarge sprite storage
			    sprites = (Sprite[]) Utility.expand(sprites, expandFactor);
			}
		}
	}

	/**
	 * Removes sprite at specified index from this group. <p>
	 *
     * This method has a big performance hit, <b>avoid</b> using this method
     * in tight-loop (main-loop). <br>
     * The standard way to remove a sprite from its group is by setting sprite
     * active state to false
     * {@link com.golden.gamedev.object.Sprite#setActive(boolean) Sprite.setActive(false)}. <p>
     *
     * SpriteGroup is designed to remove any inactive sprites automatically
     * after a period, use directly sprite removal method only for specific
     * purpose (if you really know what you are doing).
     *
     * @see com.golden.gamedev.object.Sprite#setActive(boolean)
     * @see #getScanFrequence()
	 */
	public Sprite remove(int index) {
		Sprite removedSprite = sprites[index];

		int numMoved = size - index - 1;
		if (numMoved > 0) {
	    	System.arraycopy(sprites, index+1,
							 sprites, index,
							 numMoved);
		}
		sprites[--size] = null;

		return removedSprite;
	}

	/**
	 * Removes specified sprite from this group. <p>
	 *
     * This method has a big performance hit, <b>avoid</b> using this method
     * in tight-loop (main-loop). <br>
     * The standard way to remove a sprite from its group is by setting sprite
     * active state to false
     * {@link com.golden.gamedev.object.Sprite#setActive(boolean) Sprite.setActive(false)}. <p>
     *
     * SpriteGroup is designed to remove any inactive sprites automatically
     * after a period, use directly sprite removal method only for specific
     * purpose (if you really know what you are doing).
     *
     * @return true, if specified sprite is successfuly removed from the group,
     *         or false if the sprite is not belong to this group.
     * @see com.golden.gamedev.object.Sprite#setActive(boolean)
     * @see #getScanFrequence()
	 */
	public boolean remove(Sprite s) {
		for (int i=0;i < size;i++) {
			if (sprites[i] == s) {
				remove(i);
				return true;
			}
		}

		return false;
	}

    /**
	 * Removes all members from this group, thus makes this group empty. <p>
	 *
	 * For example: <br>
	 * Destroying all enemies when player got a bomb.
	 * <pre>
	 *     ENEMY_GROUP.clear();
	 * </pre> <p>
	 *
	 * This method simply set group size to nil. The sprites reference is
	 * actually removed when {@link #removeInactiveSprites()} is scheduled. <p>
	 *
	 * To remove all sprites and also its reference immediately, use
	 * {@link #reset()} instead.
	 *
	 * @see #reset()
     */
	public void clear() {
		size = 0;
	}

	/**
	 * Removes all group members, same with {@link #clear()}, except this method
	 * also removes sprite memory reference immediately. <p>
	 *
	 * Use this method if only the size of the removed sprites is taking too big
	 * memory and you need to reclaim the used memory immediately.
	 *
	 * @see #clear()
	 */
	public void reset() {
		sprites = null;
		sprites = new Sprite[expandFactor];
		size = 0;
	}


 /****************************************************************************/
 /*************************** UPDATE THIS GROUP ******************************/
 /****************************************************************************/

    /**
     * Updates all active sprites in this group, and check the schedule for
     * removing inactive sprites.
     *
     * @see #getScanFrequence()
     */
	public void update(long elapsedTime) {
		for (int i=0;i < size;i++) {
            if (sprites[i].isActive()) {
				sprites[i].update(elapsedTime);
			}
		}

		if (scanFrequence.action(elapsedTime)) {
			// remove all inactive sprites
			removeInactiveSprites();
		}
    }

	/**
	 * Throws any inactive sprites from this group, this method won't remove
	 * immutable sprites, to remove all inactive sprites even though the
	 * inactive sprites are immutable use {@link #removeImmutableSprites()}. <p>
	 *
	 * This method is automatically called every time
	 * {@linkplain #getScanFrequence() timer for scanning inactive sprite} is
	 * scheduled.
	 *
	 * @see #getScanFrequence()
	 * @see #removeImmutableSprites()
	 * @see com.golden.gamedev.object.Sprite#setImmutable(boolean)
	 */
    public void removeInactiveSprites() {
	    removeSprites(false);
	}

	/**
	 * Throws all inactive sprites from this group even the sprite is
	 * {@link com.golden.gamedev.object.Sprite#setImmutable(boolean) immutable
	 * sprite}.
	 *
	 * @see #getInactiveSprite()
	 * @see #removeInactiveSprites()
	 * @see com.golden.gamedev.object.Sprite#setImmutable(boolean)
	 */
	public void removeImmutableSprites() {
		removeSprites(true);
	}

    private void removeSprites(boolean removeImmutable) {
		int i = 0;
		int removed = 0;
		while (i < size) {
			// check for inactive sprite in range
			if (removeImmutable == false) {
				// do not remove immutable sprites
				while (i + removed < size &&
					   (sprites[i+removed].isActive() == false &&
					    sprites[i+removed].isImmutable() == false)) {
					removed++;
				}

			} else {
				// remove all inactive sprites include immutable ones
				while (i + removed < size &&
					   sprites[i+removed].isActive() == false) {
					removed++;
				}
			}

			if (removed > 0) {
				removeRange(i, i+removed);
				removed = 0;
			}

			i++;
		}


	    if (sprites.length > size+(expandFactor*2)) {
			// shrink sprite array
		    Sprite[] dest = new Sprite[size + expandFactor];
		    System.arraycopy(sprites, 0, dest, 0, size);
		    sprites = dest;
		}
	}

    private void removeRange(int fromIndex, int toIndex) {
		int numMoved = size - toIndex;
        System.arraycopy(sprites, toIndex,
						 sprites, fromIndex,
	            		 numMoved);

		// let gc do its work
		int newSize = size - (toIndex-fromIndex);
		while (size != newSize) {
	    	sprites[--size] = null;
		}
    }


 /****************************************************************************/
 /********************** RENDER TO GRAPHICS CONTEXT **************************/
 /****************************************************************************/

    /**
     * Renders all active sprites in this group. If this group is associated
     * with a comparator, the group sprites is sort against the comparator
     * first before rendered.
     *
     * @see #setComparator(Comparator)
     */
	public void render(Graphics2D g) {
		if (comparator != null) {
			// sort sprite before render
			sort(comparator);
		}

        for (int i=0;i < size;i++) {
            if (sprites[i].isActive()) {
	            // renders only active sprite
				sprites[i].render(g);
			}
		}
    }

    /**
     * Sorts all sprites in this group with specified comparator. <p>
     *
     * This method only sort the sprites once called, use
     * {@link #setComparator(Comparator)} instead to sort the sprites on each
     * update.
     *
     * @see #setComparator(Comparator)
     */
	public void sort(Comparator c) {
	    Arrays.sort(sprites, 0, size, c);
	}


 /****************************************************************************/
 /**************************** GROUP PROPERTIES ******************************/
 /****************************************************************************/

	/**
	 * Returns the name of this group. Name is used for group identifier only.
	 *
	 * @see #setName(String)
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this group. Name is used for group identifier only.
	 *
	 * @see #getName()
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the background of this group.
	 *
	 * @see #setBackground(Background)
	 */
	public Background getBackground() {
		return background;
	}

	/**
	 * Associates specified background with this sprite group, the background
	 * will be used by all sprites in this group.
	 *
	 * @see #getBackground()
	 */
	public void setBackground(Background backgr) {
		background = backgr;
		if (background == null) {
			background = Background.getDefaultBackground();
		}

		// force all sprites to use a same background
		for (int i=0;i < size;i++) {
			sprites[i].setBackground(background);
		}
	}

	/**
	 * Returns active state of this group.
	 *
	 * @see #setActive(boolean)
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets active state of this group, inactive group won't be updated,
	 * rendered, and won't be checked for collision. <p>
	 *
	 * @see #isActive()
	 */
	public void setActive(boolean b) {
		active = b;
	}

	/**
	 * Returns comparator used for sorting sprites in this group.
	 *
	 * @see #setComparator(Comparator)
	 */
	public Comparator getComparator() {
		return comparator;
	}

	/**
	 * Sets comparator used for sorting sprites in this group. Specify null
	 * comparator for unsort order (the first sprite in the array will be
	 * rendered at the back of other sprite). <p>
	 *
	 * The comparator is used by
	 * {@link java.util.Arrays#sort(java.lang.Object[], int, int, java.util.Comparator)}
	 * to sort the sprites before rendering. For more information about how to
	 * make comparator, please read java.util.Comparator and
	 * java.util.Arrays#sort().
	 *
	 * Example of sorting sprites based on y-axis :
	 * <pre>
	 *    SpriteGroup ENEMY_GROUP;
	 *
	 *    ENEMY_GROUP.setComparator(
	 *       new Comparator() {
	 *          public int compare(Object o1, Object o2) {
	 *             Sprite s1 = (Sprite) o1,
	 *                    s2 = (Sprite) o2;
	 *
	 *             return (s1.getY() - s2.getY());
	 *          }
	 *       }
	 *    };
	 * </pre>
	 *
	 * @param c	the sprite comparator, null for unsort order
	 * @see java.util.Comparator
	 * @see java.util.Arrays#sort(java.lang.Object[], int, int, java.util.Comparator)
	 */
	public void setComparator(Comparator c) {
		comparator = c;
	}


 /****************************************************************************/
 /***************************** SPRITES GETTER *******************************/
 /****************************************************************************/

	/**
	 * Returns the first active sprite found in this group, or null if there
	 * is no active sprite. <p>
	 *
	 * This method usually used to check whether this group still have alive
	 * member or not. <br>
	 * Note: alive group has different meaning from
	 * {@linkplain #setActive(boolean) active} group, inactive group is not
	 * updated and rendered even there are many active sprites in the group,
	 * but dead group means there are no active sprites in the group. <p>
	 *
	 * For example :
	 * <pre>
	 *    SpriteGroup ENEMY_GROUP;
	 *
	 *    if (ENEMY_GROUP.getActiveSprite() == null) {
	 *        // no active enemy, advance to next level
	 *        gameState = WIN;
	 *    }
	 * </pre>
	 *
	 * @return The first found active sprite, or null if there is no active
	 * 		   sprite in this group.
	 * @see com.golden.gamedev.object.Sprite#setActive(boolean)
	 */
	public Sprite getActiveSprite() {
        for (int i=0;i < size;i++) {
			if (sprites[i].isActive()) {
				return sprites[i];
			}
		}

		return null;
	}

	/**
	 * Returns the first inactive sprite found in this group (the returned
	 * sprite is automatically set to active), or null if there is no inactive
	 * sprite, please see {@link com.golden.gamedev.object.Sprite#setImmutable(boolean)}
	 * for tag method of this method. <p>
	 *
	 * This method is used for optimization, to reuse inactive sprite instead of
	 * making new sprite. <p>
	 *
	 * For example :
	 * <pre>
	 *    SpriteGroup PROJECTILE_GROUP;
	 *
	 *    Sprite projectile = PROJECTILE_GROUP.getInactiveSprite();
	 *    if (projectile == null) {
	 *       // create new projectile if there is no inactive projectile
	 *       projectile = new Sprite(...);
	 *       PROJECTILE_GROUP.add(projectile);
	 *    }
	 *
	 *    // set projectile location and other stuff
	 *    projectile.setLocation(....);
	 * </pre>
	 * <p>
	 *
	 * This method is only a convenient way to return the first found
	 * inactive sprite. To filter the inactive sprite, simply find and then
	 * filter the inactive sprite like this :
	 * <pre>
	 *    SpriteGroup A_GROUP;
	 *    Sprite inactiveSprite = null;
	 *    Sprite[] sprites = A_GROUP.getSprites();
	 *    int size = A_GROUP.getSize();
	 *
	 *    for (int i=0;i < size;i++) {
	 *       if (!sprites[i].isActive()) {
	 *          // do the filter
	 *          // for example, we want only reuse sprite that has ID = 100
	 *          if (sprites[i].getID() == 100) {
	 *             inactiveSprite = sprites[i];
	 *             // activate sprite
	 *             inactiveSprite.setActive(true);
	 *             break;
	 *          }
	 *       }
	 *    }
	 *
	 *    if (inactiveSprite == null) {
	 *       // no inactive sprite found like the criteria (ID = 100)
	 *       // create new sprite
	 *    }
	 *
	 *    // reuse the inactive sprite
	 *    inactiveSprite.setLocation(...);
	 * </pre>
	 *
	 *
	 * @return The first found inactive sprite, or null if there is no inactive
	 *         sprite in this group.
	 * @see com.golden.gamedev.object.Sprite#setImmutable(boolean)
	 */
	public Sprite getInactiveSprite() {
        for (int i=0;i < size;i++) {
			if (sprites[i].isActive() == false) {
				sprites[i].setActive(true);
				return sprites[i];
			}
		}

		return null;
	}


    /**
     * Returns all sprites (active, inactive, and also <b>null</b> sprite)
     * in this group. <p>
     *
     * How to iterate all sprites :
     * <pre>
     *    SpriteGroup GROUP;
     *
     *    Sprite[] sprites = GROUP.getSprites();
     *    int size = GROUP.getSize();
     *
     *    // iterate the sprite one by one
     *    for (int i=0;i < size;i++) {
	 *       // remember the sprite array consists inactive sprites too
	 *       // you need to check sprite active state before process the sprite
     *       if (sprites[i].isActive()) {
     *          // now, what do you want with this active sprite?
     *          // move it to (0, 0)
     *          sprites[i].setLocation(0, 0);
     *       }
     *    }
     * </pre>
     *
     * @see #getSize()
     */
	public Sprite[] getSprites() {
		return sprites;
	}

	/**
	 * Returns total active and inactive sprites (<b>non-null</b> sprites) in
	 * this group.
	 *
	 * @see #getSprites()
	 */
    public int getSize() {
		return size;
	}


 /****************************************************************************/
 /************************** GROUP FACTOR METHODS ****************************/
 /****************************************************************************/

	/**
	 * Returns allocation size for empty sprite (null sprite).
	 *
	 * @see #setExpandFactor(int)
	 */
	public int getExpandFactor() {
		return expandFactor;
	}

	/**
	 * Sets allocation size for empty sprite (null sprite). This factor is used
	 * only for optimization (reduce the cost of array enlargement operation). <p>
	 *
	 * The process : <br>
	 * If there is a new member insertion to the group and the group sprite
	 * array has been full, the array is expanded as large as this factor.
	 *
	 * For example: <br>
	 * Expand factor is 20 (the default). <br>
	 * {@linkplain #getSize() The group size} is 100. <br>
	 * {@linkplain #getSprites() The group member} is also 100. <p>
	 *
	 * If new member is added into this group, the group size is automatically
	 * grow to 120 (100+20). <br>
	 * The new sprite added is at index 101 and the rest is empty sprite
	 * (null sprite). <p>
	 *
	 * <b>Note: use large expand factor if there are many sprites inserted
	 * into this group in a period.</b>
	 *
	 * @see #getExpandFactor()
	 */
	public void setExpandFactor(int factor) {
		expandFactor = factor;
	}

	/**
	 * Schedule timer for {@linkplain #removeInactiveSprites() removing inactive
	 * sprites}. <p>
	 *
	 * For example to set this group to scan inactive sprite every 30 seconds
	 * (the default is 15 seconds) :
	 * <pre>
	 *    SpriteGroup PLAYER_GROUP;
	 *
	 *    PLAYER_GROUP.getScanFrequence().setDelay(30000); // 30 x 1000 ms
	 * </pre>
	 *
	 * @see #removeInactiveSprites()
	 */
	public Timer getScanFrequence() {
		return scanFrequence;
	}


	public String toString() {
		return super.toString() + " " +
			"[name=" + name +
			", active=" + getSize() +
			", total=" + sprites.length +
			", member=" + getActiveSprite() +
			", background=" + background + "]";
	}

}