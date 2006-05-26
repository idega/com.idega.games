// JFC
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;


/**
 * Killing sprite!
 *
 * Objective: show how to remove a sprite from a group.
 */
public class Tutorial9_2 extends Game {


    SpriteGroup       SPRITE_GROUP;
    Background        background;

    GameFont          font;


 /****************************************************************************/
 /**************************** GAME SKELETON *********************************/
 /****************************************************************************/

    public void initResources() {
        // create the background
        background = new ImageBackground(getImage("resources/background.jpg"));


        // create the sprite group
        SPRITE_GROUP = new SpriteGroup("Anonymous Group");
        SPRITE_GROUP.setBackground(background);


        // insert sprites to the group
        BufferedImage image = getImage("resources/plane1.png");
        SPRITE_GROUP.add(new Sprite(image, 120, 50));
        SPRITE_GROUP.add(new Sprite(image, 220, 50));
        SPRITE_GROUP.add(new Sprite(image, 320, 50));
        SPRITE_GROUP.add(new Sprite(image, 420, 50));
        SPRITE_GROUP.add(new Sprite(image, 120, 150));
        SPRITE_GROUP.add(new Sprite(image, 220, 150));
        SPRITE_GROUP.add(new Sprite(image, 320, 150));
        SPRITE_GROUP.add(new Sprite(image, 420, 150));
        SPRITE_GROUP.add(new Sprite(image, 120, 250));
        SPRITE_GROUP.add(new Sprite(image, 220, 250));
        SPRITE_GROUP.add(new Sprite(image, 320, 250));
        SPRITE_GROUP.add(new Sprite(image, 420, 250));
        SPRITE_GROUP.add(new Sprite(image, 120, 350));
        SPRITE_GROUP.add(new Sprite(image, 220, 350));
        SPRITE_GROUP.add(new Sprite(image, 320, 350));
        SPRITE_GROUP.add(new Sprite(image, 420, 350));


        font = fontManager.getFont(getImages("resources/font.png", 20, 3),
                                   " !            .,0123" +
                                   "456789:   -? ABCDEFG" +
                                   "HIJKLMNOPQRSTUVWXYZ ");
    }



    public void update(long elapsedTime) {
        background.update(elapsedTime);
        SPRITE_GROUP.update(elapsedTime);


        // remove sprite from the group when ENTER is pressed
        if (keyPressed(KeyEvent.VK_ENTER)) {
            // get any active sprite from the group
            // or NULL if there is no active sprite in the group
            Sprite activeSprite = SPRITE_GROUP.getActiveSprite();

            if (activeSprite != null) {
                // we set the sprite to inactive
                activeSprite.setActive(false);

                // when the sprite is not active
                // sprite group automatically takes care the sprite removal
            }
        }
    }



    public void render(Graphics2D g) {
        background.render(g);
        SPRITE_GROUP.render(g);

        // draw info text
        font.drawString(g, "ENTER : REMOVE A SPRITE", 10, 10);
        font.drawString(g, "        FROM ITS GROUP", 10, 30);
    }


 /****************************************************************************/
 /***************************** START-POINT **********************************/
 /****************************************************************************/

    public static void main(String[] args) {
        GameLoader game = new GameLoader();
        game.setup(new Tutorial9_2(), new Dimension(640,480), false);
        game.start();
    }

}