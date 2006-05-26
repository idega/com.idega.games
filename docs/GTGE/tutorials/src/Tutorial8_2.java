// JFC
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;


/**
 * Various background types.
 *
 * Objective: show all available background types.
 */
public class Tutorial8_2 extends Game {


    Sprite            sprite;

    Background        backgr;  // the active background the game currently uses

    // available backgrounds
    ColorBackground   colorBG;
    ImageBackground   imageBG;
    TileBackground    tileBG;

    GameFont          font;


 /****************************************************************************/
 /**************************** GAME SKELETON *********************************/
 /****************************************************************************/

    public void initResources() {
        // init variables the backgrounds need
        // note: we set all the backgrounds to have the same size
        //       that way we can set the active background without resize anything
        int w = 768, h = 576;

        // color background need a color and the background size
        Color color = Color.BLUE;

        // image background need an image
        BufferedImage image = getImage("resources/background.jpg");

        // tile background needs array of integer and the tile images
        BufferedImage[] tileImages = getImages("resources/tiles.png", 6, 1); // tile size = 64 x 64
        // the tile size is 64 x 64, if we want the same background size
        // we set the tiles as large as: bgwidth/64, bgheight/64
        int[][] tiles = new int[w/64][h/64];
        // fill the tiles with random value
        for (int i=0;i < tiles.length;i++) {
            for (int j=0;j < tiles[0].length;j++) {
                tiles[i][j] = getRandom(0, tileImages.length-1);
            }
        }


        // create the background with above settings
        colorBG = new ColorBackground(color, w, h);
        imageBG = new ImageBackground(image);
        tileBG  = new TileBackground(tileImages, tiles);


        // we set the imageBG as the first active background
        backgr = imageBG;


        // create the sprite
        sprite = new Sprite(getImage("resources/plane1.png"), 100, 100);
        sprite.setBackground(backgr); // associate the sprite with active bg


        font = fontManager.getFont(getImages("resources/font.png", 20, 3),
                                   " !            .,0123" +
                                   "456789:   -? ABCDEFG" +
                                   "HIJKLMNOPQRSTUVWXYZ ");
    }


    public void update(long elapsedTime) {
        backgr.update(elapsedTime);
        sprite.update(elapsedTime);


        // check for ENTER key to change the active background
        if (keyPressed(KeyEvent.VK_ENTER)) {
            // rotate the active background
            if (backgr == colorBG) {
                backgr = imageBG;

            } else if (backgr == imageBG) {
                backgr = tileBG;

            } else if (backgr == tileBG) {
                backgr = colorBG;
            }

            // since the sprite still associated with the old background
            // we need to associate the sprite with the new one
            sprite.setBackground(backgr);
        }


        // control the sprite with arrow key
        // we use momentum movement here
        if (keyDown(KeyEvent.VK_LEFT))       sprite.addHorizontalSpeed(elapsedTime, -0.001, -0.4);
        else if (keyDown(KeyEvent.VK_RIGHT)) sprite.addHorizontalSpeed(elapsedTime,  0.001,  0.4);
        else if (sprite.getHorizontalSpeed() > 0) sprite.addHorizontalSpeed(elapsedTime, -0.005, 0);
        else if (sprite.getHorizontalSpeed() < 0) sprite.addHorizontalSpeed(elapsedTime,  0.005, 0);

        if (keyDown(KeyEvent.VK_UP))         sprite.addVerticalSpeed(elapsedTime, -0.001, -0.4);
        else if (keyDown(KeyEvent.VK_DOWN))  sprite.addVerticalSpeed(elapsedTime,  0.001,  0.4);
        else if (sprite.getVerticalSpeed() > 0) sprite.addVerticalSpeed(elapsedTime, -0.005, 0);
        else if (sprite.getVerticalSpeed() < 0) sprite.addVerticalSpeed(elapsedTime,  0.005, 0);


        // set the sprite as the center of the background
        backgr.setToCenter(sprite);
    }


    public void render(Graphics2D g) {
        backgr.render(g);
        sprite.render(g);


        // draw info text
        font.drawString(g, "ENTER: CHANGE ACTIVE BACKGROUND", 10, 10);
    }


 /****************************************************************************/
 /***************************** START-POINT **********************************/
 /****************************************************************************/

    public static void main(String[] args) {
        GameLoader game = new GameLoader();
        game.setup(new Tutorial8_2(), new Dimension(640,480), false);
        game.start();
    }

}