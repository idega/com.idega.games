// JFC
import java.awt.*;

// GTGE
import com.golden.gamedev.*;


/**
 * Game in Windowed Mode Environment.
 *
 * Objective: show how to set up a windowed mode game
 */
public class Tutorial5_1 extends Game {


 /****************************************************************************/
 /**************************** GAME SKELETON *********************************/
 /****************************************************************************/

    public void initResources() {
    }


    public void update(long elapsedTime) {
    }


    public void render(Graphics2D g) {
    }


 /****************************************************************************/
 /***************************** START-POINT **********************************/
 /****************************************************************************/

    public static void main(String[] args) {
        GameLoader game = new GameLoader();
        game.setup(new Tutorial5_1(), new Dimension(640,480), false);
        game.start();
    }

}