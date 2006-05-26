// JFC
import java.awt.*;
import java.awt.event.*;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;


/**
 * Background view port/view area/clipping area.
 *
 * Objective: show how to set background view area.
 */
public class Tutorial8_3 extends Game {


    AnimatedSprite        sprite;
    ImageBackground        background;

    GameFont            font;


 /****************************************************************************/
 /**************************** GAME SKELETON *********************************/
 /****************************************************************************/

    public void initResources() {
        // create the background
        background = new ImageBackground(getImage("resources/background.jpg"));
        // by default the background viewport is as big as the game area
        // to change the background view area, use background clipping
        background.setClip(100, 100, 350, 300);


        // create the sprite
        sprite = new AnimatedSprite(getImages("resources/plane2.png", 3, 1));
        sprite.setBackground(background);

        sprite.setAnimate(true);
        sprite.setLoopAnim(true);


        font = fontManager.getFont(getImages("resources/font.png", 20, 3),
                                   " !            .,0123" +
                                   "456789:   -? ABCDEFG" +
                                   "HIJKLMNOPQRSTUVWXYZ ");
    }


    public void update(long elapsedTime) {
        background.update(elapsedTime);
        sprite.update(elapsedTime);


        double speedX = 0, speedY = 0;
        if (keyDown(KeyEvent.VK_LEFT))  speedX = -0.1;
        if (keyDown(KeyEvent.VK_RIGHT)) speedX =  0.1;
        if (keyDown(KeyEvent.VK_UP))    speedY = -0.1;
        if (keyDown(KeyEvent.VK_DOWN))  speedY =  0.1;

        sprite.setSpeed(speedX, speedY);


        background.setToCenter(sprite);
    }



    public void render(Graphics2D g) {
        // fill unoccupy background
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // draw the sprites and the background
        background.render(g);
        sprite.render(g);


        // draw sprite coordinate
        font.drawString(g, "THE SPRITE IS LOCATED AT " +
                        (int) sprite.getX() + ", " + (int) sprite.getY(),
                        100, 50);
        font.drawString(g, "ON THE BACKGROUND", 100, 70);

        font.drawString(g, "BACKGROUND VIEWPORT", 100, 415);
    }


 /****************************************************************************/
 /***************************** START-POINT **********************************/
 /****************************************************************************/

    public static void main(String[] args) {
        GameLoader game = new GameLoader();
        game.setup(new Tutorial8_3(), new Dimension(640,480), false);
        game.start();
    }

}