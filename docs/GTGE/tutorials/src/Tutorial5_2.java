// JFC
import java.awt.*;
import java.awt.event.*;

// GTGE
import com.golden.gamedev.*;


/**
 * Game in Fullscreen Mode Environment.
 *
 * Objective: show how to set up a fullscreen-mode game
 */
public class Tutorial5_2 extends Game {


 /****************************************************************************/
 /**************************** GAME SKELETON *********************************/
 /****************************************************************************/

    public void initResources() {
    }



    public void update(long elapsedTime) {
        // in fullscreen mode, frame close button not visible
        // we need to set a key to quit the game, in here we set ESC key
        if (keyPressed(KeyEvent.VK_ESCAPE)) {
            finish();
        }
    }



    public void render(Graphics2D g) {
        // draw blue box as the background
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, getWidth(), getHeight());
    }


 /****************************************************************************/
 /***************************** START-POINT **********************************/
 /****************************************************************************/

    public static void main(String[] args) {
        GameLoader game = new GameLoader();
        game.setup(new Tutorial5_2(), new Dimension(640,480), true);
        game.start();
    }

}