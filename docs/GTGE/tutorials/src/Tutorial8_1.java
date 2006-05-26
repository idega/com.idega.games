// JFC
import java.awt.*;
import java.awt.event.*;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;


/**
 * Background with sprites.
 *
 * Objective:
 * - show how to create game background
 * - how to associate sprites with the background
 */
public class Tutorial8_1 extends Game {


    Sprite            sprite1;
    Sprite            sprite2;
    Sprite            sprite3;

    Background        backgr;

    GameFont          font;


 /****************************************************************************/
 /**************************** GAME SKELETON *********************************/
 /****************************************************************************/

    public void initResources() {
        int w = 768;   // background width
        int h = 576;   // background height

        // create the background
        backgr = new ImageBackground(getImage("resources/background.jpg"), w, h);


        // create sprite
        sprite1 = new Sprite(getImage("resources/plane1.png"), 100, 100);
        // associate the sprite with above background
        sprite1.setBackground(backgr);


        // create another sprite
        sprite2 = new Sprite(getImage("resources/plane1.png"), 250, 250);
        sprite2.setBackground(backgr);



        // create another sprite
        sprite3 = new Sprite(getImage("resources/plane1.png"), 400, 400);
        // always remember to associate the sprite with the background
        // or the sprite will be use the default background!
        sprite3.setBackground(backgr);


        font = fontManager.getFont(getImages("resources/font.png", 20, 3),
                                   " !            .,0123" +
                                   "456789:   -? ABCDEFG" +
                                   "HIJKLMNOPQRSTUVWXYZ ");
    }



    public void update(long elapsedTime) {
        backgr.update(elapsedTime);
        sprite1.update(elapsedTime);
        sprite2.update(elapsedTime);
        sprite3.update(elapsedTime);


        // control the sprite with arrow key
        // sprite speed = 0.1 pixel every ms
        double speedX = 0, speedY = 0;

        if (keyDown(KeyEvent.VK_LEFT))   speedX = -0.1;
        if (keyDown(KeyEvent.VK_RIGHT))  speedX =  0.1;
        if (keyDown(KeyEvent.VK_UP))     speedY = -0.1;
        if (keyDown(KeyEvent.VK_DOWN))   speedY =  0.1;

        sprite1.setSpeed(speedX, speedY);


        // set sprite1 (the one we control) as the center of the background
        backgr.setToCenter(sprite1);
    }



    public void render(Graphics2D g) {
        backgr.render(g);
        sprite1.render(g);
        sprite2.render(g);
        sprite3.render(g);


        // draw our sprite coordinate
        int spriteX = (int) sprite1.getX(),
            spriteY = (int) sprite1.getY();
        int screenX = (int) sprite1.getScreenX(),
            screenY = (int) sprite1.getScreenY();
        // the text is following the sprite position
        font.drawString(g, "LOCATED AT " + spriteX + ", " + spriteY,
            GameFont.CENTER, screenX-155, screenY+70, 400);
        font.drawString(g, "ON THE BACKGROUND",
            GameFont.CENTER, screenX-155, screenY+90, 400);


        // draw background info
        font.drawString(g, "BG SIZE     : " +
                        backgr.getWidth() + " X " + backgr.getHeight(),
                        10, 10);
        font.drawString(g, "BG POSITION : " +
                        (int) backgr.getX() + ", " + (int) backgr.getY(),
                        10, 35);
    }


 /****************************************************************************/
 /***************************** START-POINT **********************************/
 /****************************************************************************/

    public static void main(String[] args) {
        GameLoader game = new GameLoader();
        game.setup(new Tutorial8_1(), new Dimension(640,480), false);
        game.start();
    }

}