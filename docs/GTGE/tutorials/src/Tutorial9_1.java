// JFC
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;


/**
 * Grouping sprites into sprite group.
 * See how easy we can organize many sprites now.
 *
 * Objective: show how to group sprites
 */
public class Tutorial9_1 extends Game {


    SpriteGroup       SPRITE_GROUP; // we use UPPERCASE var name for sprite group

    AnimatedSprite      sprite1;
    Sprite            sprite2;
    Sprite            sprite3;

    Background        background;


 /****************************************************************************/
 /**************************** GAME SKELETON *********************************/
 /****************************************************************************/

    public void initResources() {
        // create the background
        background = new ImageBackground(getImage("resources/background.jpg"));


        // create the sprites
        sprite1 = new AnimatedSprite(getImages("resources/plane2.png",3,1), 120, 50);
        sprite1.setAnimate(true);
        sprite1.setLoopAnim(true);
        // no need to associate the sprite with the background anymore
        // as the task is handled by the sprite group....
//      sprite1.setBackground(backgr);
        sprite2 = new Sprite(getImage("resources/plane1.png"), 220, 50);
        sprite3 = new Sprite(getImage("resources/plane1.png"), 320, 50);


        // create the sprite group
        String name = "Plane Group";  // the group name
        SPRITE_GROUP = new SpriteGroup(name);


        // now the task of associating sprite to background
        // is delegated to sprite group
        // ALL sprites in a group will use SAME background
        SPRITE_GROUP.setBackground(background);


        // insert sprites that belong to this group
        SPRITE_GROUP.add(sprite1);    // when inserting the sprite to the group
        SPRITE_GROUP.add(sprite2);    // the sprite is automatically associated
        SPRITE_GROUP.add(sprite3);    // with the background of the group!

        // now with sprite group, you don't need to hold the sprite reference anymore
        // perfect for add and forget kind of sprite
        BufferedImage image = getImage("resources/plane1.png");
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
    }



    public void update(long elapsedTime) {
        background.update(elapsedTime);

        // no need to update the sprites one by one;
        // updating the sprite group will update them all!
//        sprite1.update(elapsedTime);
//        sprite2.update(elapsedTime);
//        sprite3.update(elapsedTime);

        SPRITE_GROUP.update(elapsedTime);


        // control the sprite with arrow key
        double speedX = 0;
        double speedY = 0;
        if (keyDown(KeyEvent.VK_LEFT))     speedX = -0.1;
        if (keyDown(KeyEvent.VK_RIGHT)) speedX = 0.1;
        if (keyDown(KeyEvent.VK_UP))     speedY = -0.1;
        if (keyDown(KeyEvent.VK_DOWN))     speedY = 0.1;

        sprite1.setSpeed(speedX, speedY);


        // set sprite1 to the center of the background
        background.setToCenter(sprite1);
    }



    public void render(Graphics2D g) {
        background.render(g);

        // the sprite group also takes care of all sprites rendering
//        sprite1.render(g);
//        sprite2.render(g);
//        sprite3.render(g);
        SPRITE_GROUP.render(g);
    }


 /****************************************************************************/
 /***************************** START-POINT **********************************/
 /****************************************************************************/

    public static void main(String[] args) {
        GameLoader game = new GameLoader();
        game.setup(new Tutorial9_1(), new Dimension(640,480), false);
        game.start();
    }

}