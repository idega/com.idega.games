// JFC
import java.awt.*;
import java.awt.image.*;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.object.*;


/**
 * Sprite is a graphical object that has its own behaviour.
 *
 * Objective: show sprite basic usage.
 */
public class Tutorial7_1 extends Game {


    Sprite   sprite1;
    Sprite   sprite2;


 /****************************************************************************/
 /**************************** GAME SKELETON *********************************/
 /****************************************************************************/

    public void initResources() {
        // sprite image
        BufferedImage image = getImage("resources/plane1.png");
        double posx = 150;    // sprite x coordinate
        double posy = 100;    // sprite y coordinate

        // instantiate sprite1 using our predetermined data
        sprite1 = new Sprite(image, posx, posy);


        // create sprite2 using on-the-fly data
        sprite2 = new Sprite(getImage("resources/plane1.png"), 425, 100);
    }


    public void update(long elapsedTime) {
        // update the sprites
        sprite1.update(elapsedTime);
        sprite2.update(elapsedTime);
    }


    public void render(Graphics2D g) {
        // clear background
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // render the sprites
        sprite1.render(g);
        sprite2.render(g);
    }


 /****************************************************************************/
 /***************************** START-POINT **********************************/
 /****************************************************************************/

    public static void main(String[] args) {
        GameLoader game = new GameLoader();
        game.setup(new Tutorial7_1(), new Dimension(640,480), false);
        game.start();
    }

}