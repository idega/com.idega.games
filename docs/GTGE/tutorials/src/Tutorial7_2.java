// JFC
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.object.*;


/**
 * Sprite in action!!!
 *
 * Objective:
 * - show an animated sprite
 * - move the sprite based on key input
 */
public class Tutorial7_2 extends Game {


    AnimatedSprite    sprite;
    Sprite            projectile;

    GameFont          font;


 /****************************************************************************/
 /**************************** GAME SKELETON *********************************/
 /****************************************************************************/

    public void initResources() {
        // create animated sprite
        // animated sprite uses an array of images to make itself animated
        BufferedImage[] images = getImages("resources/plane2.png", 3, 1);
        sprite = new AnimatedSprite(images, 275, 250);
        sprite.setAnimate(true);    // animate the sprite
        sprite.setLoopAnim(true);   // animate it continously


        // only one projectile sprite is reserved
        // this projectile is used every time a fire event occurs
        projectile = new Sprite(getImage("resources/projectile.png"), -50, -50);


        font = fontManager.getFont(getImages("resources/font.png", 20, 3),
                                   " !            .,0123" +
                                   "456789:   -? ABCDEFG" +
                                   "HIJKLMNOPQRSTUVWXYZ ");
    }



    public void update(long elapsedTime) {
        // update the sprites
        sprite.update(elapsedTime);
        projectile.update(elapsedTime);


        // control the sprite with arrow key
        // moving the sprite 0.1 pixel every millisecond
        // note: always multiply the speed with elapsed time for frame rate
        // independent movement (speed * elapsedTime)
        if (keyDown(KeyEvent.VK_LEFT))   sprite.moveX(-0.1 * elapsedTime);
        if (keyDown(KeyEvent.VK_RIGHT))  sprite.moveX(0.1 * elapsedTime);
        if (keyDown(KeyEvent.VK_UP))     sprite.moveY(-0.1 * elapsedTime);
        if (keyDown(KeyEvent.VK_DOWN))   sprite.moveY(0.1 * elapsedTime);


        // check for control key, to fire the projectile
        if (keyPressed(KeyEvent.VK_CONTROL)) {
            // fire!!
            projectile.setLocation(sprite.getX()+16.5, sprite.getY()-16);

            // the projectile is moving to top with 0.5 velocity
            // (0.5 pixel every millisecond)
            projectile.setVerticalSpeed(-0.5);

            // play fire sound
            playSound("resources/sound1.wav");
        }


        // check for escape key, to quit game
        if (keyPressed(KeyEvent.VK_ESCAPE)) {
            finish();
        }
    }



    public void render(Graphics2D g) {
        // fill background
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, getWidth(), getHeight());


        // render the sprites
        sprite.render(g);
        projectile.render(g);


        // draw sprite coordinate
        // the text position is following sprite position
        int spriteX = (int) sprite.getX();
        int spriteY = (int) sprite.getY();
        font.drawString(g, "POS: "+spriteX+", "+spriteY,
                        spriteX-60, spriteY+70);


        // draw key info
        font.drawString(g, "ARROW KEY : MOVE", 10, 10);
        font.drawString(g, "CONTROL   : FIRE", 10, 30);
        font.drawString(g, "ESCAPE    : QUIT GAME", 10, 50);
    }


 /****************************************************************************/
 /***************************** START-POINT **********************************/
 /****************************************************************************/

    public static void main(String[] args) {
        GameLoader game = new GameLoader();
        game.setup(new Tutorial7_2(), new Dimension(640,480), false);
        game.start();
    }

}