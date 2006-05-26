// JFC
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.net.*;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.object.*;


/**
 * Comprehensive use of GTGE game engines.
 *
 * Game class hold all game engines :
 * - Graphics Engine (bsGraphics)
 * - Input Engine    (bsInput)
 * - Timer Engine    (bsTimer)
 * - Image Engine    (bsLoader)
 * - I/O Engine      (bsIO)
 * - Sound Engine    (bsSound)
 * - Music Engine    (bsMusic)
 *
 * Objective: show the basic use of all engines inside the Game class.
 */
public class Tutorial6 extends Game {


    BufferedImage   image;
    BufferedImage[] images;
    URL             url;


    GameFont        font;    // bitmap game font


 /****************************************************************************/
 /**************************** GAME SKELETON *********************************/
 /****************************************************************************/

    /**
     * Init game variables.
     *
     * Here we show the basic use of :
     * - Image Engine (bsLoader)
     * - I/O Engine   (bsIO)
     * - Music Engine (bsMusic)
     * - Timer Engine (bsTimer)
     */
    public void initResources() {
        // Image Engine (bsLoader)
        // loading image
        image = getImage("resources/plane1.png");
        // loading images, 3 column, 1 row
        images = getImages("resources/plane2.png", 3, 1);


        // I/O Engine (bsIO)
        // get an external object, such as a file or, in this case, url
        url = bsIO.getURL("resources/textfile.txt");


        // Music Engine (bsMusic)
        // play midi audio
        playMusic("resources/music1.mid");


        // Timer Engine (bsTimer)
        // setting game frame-per-second (fps)
        setFPS(25);


        // Game Font Manager
        font = fontManager.getFont(getImages("resources/smallfont.png", 8, 12),
                                   " !\"#$%&'()*+,-./0123456789:;<=>?" +
                                   "@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_" +
                                   "`abcdefghijklmnopqrstuvwxyz{|}~~");
    }



    /**
     * Update game variables.
     *
     * Here we show the basic use of:
     * - Input Engine (bsInput)
     * - Sound Engine (bsSound)
     */
    public void update(long elapsedTime) {
        // Input Engine (bsInput)
        // poll keyboard and mouse events
        // keyboard event
        if (keyPressed(KeyEvent.VK_ENTER)) {
            System.out.println("ENTER is pressed");
        }
        if (keyDown(KeyEvent.VK_A)) {
            System.out.println("A is being pressed");
        }

        // mouse event
        if (click()) {
            System.out.println("Mouse LEFT BUTTON is pressed");
        }
        if (bsInput.isMouseDown(MouseEvent.BUTTON3)) {
            System.out.println("Mouse RIGHT BUTTON is being pressed");
        }


        // Sound Engine (bsSound)
        // play wave audio
        if (keyPressed(KeyEvent.VK_SPACE)) {
            playSound("resources/sound1.wav");
        }
    }



    /**
     * Render the game to screen.
     *
     * Here we show the basic use of:
     * - Graphics Engine (bsGraphics)
     */
    public void render(Graphics2D g) {
        // Graphics Engine (bsGraphics)
        // getting window size: getWidth(), getHeight()
        g.setColor(Color.RED.darker());
        g.fillRect(0, 0, getWidth(), getHeight());

        font.drawString(g, "====================", 10, 7);
        font.drawString(g, "Testing Game Engines", 10, 17);
        font.drawString(g, "====================", 10, 27);

        font.drawString(g, "Image Engine (bsLoader)", 10, 45);
        font.drawString(g, "- Loading \"resource/plane1.png\" and \"resource/plane2.png\" :", 20, 60);
        g.drawImage(image, 20, 65, null);
        g.drawImage(images[0], 140, 65, null);
        g.drawImage(images[1], 220, 65, null);
        g.drawImage(images[2], 300, 65, null);

        font.drawString(g, "I/O Engine (bsIO)", 10, 145);
        font.drawString(g, "- Loading file URL \"resources/textfile.txt\" :", 20, 160);
        font.drawString(g, "  "+url.toString(), 20, 175);

        font.drawString(g, "Music Engine (bsMusic)", 10, 205);
        font.drawString(g, "- Playing \"resources/music1.mid\"", 20, 220);

        font.drawString(g, "Timer Engine (bsTimer)", 10, 250);
        font.drawString(g, "- Set FPS to 25", 20, 265);
        font.drawString(g, "- Get current FPS : "+getCurrentFPS(), 20, 280);

        font.drawString(g, "Input Engine (bsInput)", 10, 310);
        font.drawString(g, "- Press \"ENTER\" : print something to console", 20, 325);
        font.drawString(g, "- Pressing \"A\"  : print something to console", 20, 340);
        font.drawString(g, "- Click mouse left button     : print something to console", 20, 355);
        font.drawString(g, "- Clicking mouse right button : print something to console", 20, 370);

        font.drawString(g, "Sound Engine (bsSound)", 10, 400);
        font.drawString(g, "- Press \"SPACE\" : play \"resources/sound1.wav\"", 20, 415);

        font.drawString(g, "Graphics Engine (bsGraphics) : Show this screen with all these text!", 10, 445);
    }


 /****************************************************************************/
 /***************************** START-POINT **********************************/
 /****************************************************************************/

    public static void main(String[] args) {
        GameLoader game = new GameLoader();
        game.setup(new Tutorial6(), new Dimension(640,480), false);
        game.start();
    }


 /****************************************************************************/
 /*************************** GAME CLASS INSIDES *****************************/
 /****************************************************************************/

// Basically Game class is a big wrapper for all GTGE game engines
// These are the ACTUAL/REAL functions in the Game class!
// The Game class DIRECTLY calls the engine's functions
// We use this kind of wrapper to simplify engines functions poll
// So rather than use    : bsLoader.getImage("image.png");
// You can directly call : getImage("image.png");
//
// Note: not all the engines functions are polled; only frequently used functions
// Please refer to the engines documentations for complete functions you can use


//    //-> Image Engine
//
//    public BufferedImage getImage(String imagefile) {
//        return bsLoader.getImage(imagefile);
//    }


//    //-> Music Engine
//    public int playMusic(String audiofile) {
//        return bsMusic.play(audiofile);
//    }


//    //-> Timer Engine
//    public void setFPS(int fps) {
//        bsTimer.setFPS(fps);
//    }


//    //-> Input Engine
//    public boolean keyPressed(int keyCode) {
//        return bsInput.isKeyPressed(keyCode);
//    }
//    public boolean keyDown(int keyCode) {
//        return bsInput.keyDown(keyCode);
//    }
//    public boolean click() {
//        return bsInput.isMousePressed(MouseEvent.BUTTON1);
//    }


//    //-> Sound Engine
//    public int playSound(String audiofile) {
//        return bsSound.play(audiofile);
//    }


//    //-> Graphics Engine
//    public int getWidth() {
//        return bsGraphics.getSize().width;
//    }
//    public int getHeight() {
//        return bsGraphics.getSize().height;
//    }

}