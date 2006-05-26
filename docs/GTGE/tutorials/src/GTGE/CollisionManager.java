package com.golden.gamedev.object;

// JFC
import java.awt.image.BufferedImage;

// GTGE
import com.golden.gamedev.object.collision.CollisionRect;


/**
 * <code>CollisionManager</code> class is the base collision check abstract
 * class that check collision between two <code>SpriteGroups</code>.
 * The collision check is the subclass responsibility. <p>
 *
 * In Golden T Game Engine (GTGE) Frame Work, sprites are grouped into
 * {@link SpriteGroup} and collision is checked between two sprite groups.
 * This technique reduces code programming, increases readability,
 * simplify collision event, and improves collision check. <p>
 *
 * <code>CollisionManager</code> is added into <code>PlayField</code> using
 * {@link PlayField#addCollisionGroup(SpriteGroup, SpriteGroup, CollisionManager)},
 * and then the <code>PlayField</code> manage the collision check everytime the
 * PlayField is updated.
 *
 * @see PlayField#addCollisionGroup(SpriteGroup, SpriteGroup, CollisionManager)
 */
public abstract class CollisionManager {


 /*************************** COLLISION GROUP ********************************/

    private SpriteGroup group1, group2;

	private boolean 	active = true;


 /****************************************************************************/
 /******************************* CONSTRUCTOR ********************************/
 /****************************************************************************/

	/**
	 * Creates new <code>CollisionManager</code>.
	 */
	public CollisionManager() {
	}


 /****************************************************************************/
 /*************************** COLLISION GROUP ********************************/
 /****************************************************************************/

	/**
	 * Associates specified sprite groups to this manager.
	 * The groups will be checked its collision one against another.
	 *
	 * @see #checkCollision()
	 */
	public void setCollisionGroup(SpriteGroup group1, SpriteGroup group2) {
		this.group1 = group1;
		this.group2 = group2;
	}

	/**
	 * Returns the first group associated with this collision manager.
	 */
	public SpriteGroup getGroup1() {
		return group1;
	}

	/**
	 * Returns the second group associated with this collision manager.
	 */
	public SpriteGroup getGroup2() {
		return group2;
	}


 /****************************************************************************/
 /**************************** COLLISION CHECK *******************************/
 /****************************************************************************/

    /**
     * Checks for collision between all members in
     * {@linkplain #getGroup1() group 1} againts all members in
     * {@linkplain #getGroup1() group 2}.
     */
	public abstract void checkCollision();


 /****************************************************************************/
 /**************************** ACTIVE STATE **********************************/
 /****************************************************************************/

	/**
	 * Returns true, if this collision manager is active.
	 * Inactive collision manager won't perform any collision check.
	 *
	 * @see #setActive(boolean)
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets the active state of this collision manager.
	 * Inactive collision manager won't perform any collision check.
	 *
	 * @see #isActive()
	 */
	public void setActive(boolean b) {
		active = b;
	}


	//////////// optimization ///////////
	private final static CollisionRect iRect = new CollisionRect();

	/**
	 * Returns true whether <code>image1</code> at <code>x1</code>, <code>y1</code>
	 * collided with <code>image2</code> at <code>x2</code>, <code>y2</code>.
	 */
	public static boolean isPixelCollide(double x1, double y1, BufferedImage image1,
										 double x2, double y2, BufferedImage image2) {
		// initialization
		double width1 = x1 + image1.getWidth() -1,
        	   height1 = y1 + image1.getHeight() -1,
        	   width2 = x2 + image2.getWidth() -1,
        	   height2 = y2 + image2.getHeight() -1;

        int xstart = (int) Math.max(x1, x2),
        	ystart = (int) Math.max(y1, y2),
			xend   = (int) Math.min(width1, width2),
			yend   = (int) Math.min(height1, height2);

        // intersection rect
        int toty = Math.abs(yend - ystart);
        int totx = Math.abs(xend - xstart);

		for (int y=1;y < toty-1;y++){
            int ny = Math.abs(ystart - (int) y1) + y;
            int ny1 = Math.abs(ystart - (int) y2) + y;

            for (int x=1;x < totx-1;x++) {
                int nx = Math.abs(xstart - (int) x1) + x;
                int nx1 = Math.abs(xstart - (int) x2) + x;
                try {
					if (((image1.getRGB(nx,ny) & 0xFF000000) != 0x00) &&
						((image2.getRGB(nx1,ny1) & 0xFF000000) != 0x00)) {
						// collide!!
	                    return true;
					}
				} catch (Exception e) {
//					System.out.println("s1 = "+nx+","+ny+"  -  s2 = "+nx1+","+ny1);
				}
            }
        }

        return false;
	}

    /**
     * Returns the intersection rect of two rectangle.
     */
	public static CollisionRect getIntersectionRect(double x1, double y1, int width1, int height1,
													double x2, double y2, int width2, int height2) {
		double x12 = x1 + width1, y12 = y1 + height1,
			   x22 = x2 + width2, y22 = y2 + height2;

		if (x1 < x2) x1 = x2;
		if (y1 < y2) y1 = y2;
		if (x12 > x22) x12 = x22;
		if (y12 > y22) y12 = y22;
		x12 -= x1;
		y12 -= y1;
		// x12,y12 will never overflow (they will never be
		// larger than the smallest of the two source w,h)
		// they might underflow, though...
		iRect.setBounds(x1, y1, (int) x12, (int) y12);

		return iRect;
    }

}