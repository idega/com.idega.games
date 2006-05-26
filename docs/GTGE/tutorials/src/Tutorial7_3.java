// JFC
import java.awt.*;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.object.*;


/**
 * Controlling sprite behaviour with timer.
 *
 * Objective: Show how to use the Timer class to control sprite behaviour.
 */
public class Tutorial7_3 extends Game {


    Sprite       sprite;
    Sprite       projectile;
    Timer        fireTimer;      // timer for making the sprite firing in an interval

    Timer        countDownTimer; // Timer class is a simple class to count elapsed time
                                 // it can be used for *everything*
    int          time;           // the time we counting down using above timer

    GameFont     font;


 /****************************************************************************/
 /**************************** GAME SKELETON *********************************/
 /****************************************************************************/

    public void initResources() {
        // create the sprite
        // miss polymorphism?! ;)
        sprite = new AnimatedSprite(getImages("resources/plane2.png",3,1), 287.5, 330);
        // since sprite variable is Sprite class
        // and now we need to use AnimatedSprite specific functions
        // we have to cast the sprite to AnimatedSprite
        ((AnimatedSprite) sprite).setAnimate(true);
        ((AnimatedSprite) sprite).setLoopAnim(true);

        // create the projectile
        projectile = new Sprite(getImage("resources/projectile.png"), -50, -50);


        // set the sprite to fire every 1 sec (1000 ms)
        fireTimer = new Timer(1000);


        // count down timer
        countDownTimer = new Timer(1000);     // count down every second (1000ms)
        time = 20;                            // 20 seconds (20 * 1000ms) to go

      //At this point you may be wondering why we didn't just do :
      //countDownTimer = new Timer(20000);
      //Well, say we want to print a counter on the screen to be
      //updated every second. This makes it very easy to do
      //especially as an exercise for the reader! ;)


        font = fontManager.getFont(getImages("resources/font.png", 20, 3),
                                   " !            .,0123" +
                                   "456789:   -? ABCDEFG" +
                                   "HIJKLMNOPQRSTUVWXYZ ");
    }



    public void update(long elapsedTime) {
        // update the sprites
        sprite.update(elapsedTime);
        projectile.update(elapsedTime);


        // if the fireTimer.action(...) returns true
        // it means 1000 ms has been passed
        // time to fire !!
        if (fireTimer.action(elapsedTime)) {
            // play fire sound
            playSound("resources/sound1.wav");

            projectile.setLocation(sprite.getX()+16.5, sprite.getY()-16);
            projectile.setVerticalSpeed(-0.5);
        }


        // count down timer
        if (countDownTimer.action(elapsedTime)) {
            // decrease time
            time--;

            if (time <= 0) {
                // 20 seconds have passed
                // time to quit
                finish();
            }
        }
    }



    public void render(Graphics2D g) {
        // fill background
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, getWidth(), getHeight());


        // render the sprites
        sprite.render(g);
        projectile.render(g);


        // draw info text
        font.drawString(g, "TIME : " + time, 15, 15);
    }


 /****************************************************************************/
 /***************************** START-POINT **********************************/
 /****************************************************************************/

    public static void main(String[] args) {
        GameLoader game = new GameLoader();
        game.setup(new Tutorial7_3(), new Dimension(640,480), false);
        game.start();
    }

}