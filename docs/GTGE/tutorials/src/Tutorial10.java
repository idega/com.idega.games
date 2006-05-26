// JFC
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.sprite.*;
import com.golden.gamedev.object.background.*;
import com.golden.gamedev.object.collision.*;


/**
 * Need a collision!?
 *
 * Objective: show the basic usage of GTGE collision check.
 */
public class Tutorial10 extends Game {


    Background           background;

    SpriteGroup          PLAYER_GROUP;        // hold plane sprite (the sprite we control)
    SpriteGroup          PROJECTILE_GROUP;    // hold plane's projectiles
    SpriteGroup          ENEMY_GROUP;        // the enemy sprite

    AnimatedSprite       plane;

    GameFont              font;

    CollisionManager    collision;             // manage collision between
                                             // projectiles and enemy


 /****************************************************************************/
 /**************************** GAME SKELETON *********************************/
 /****************************************************************************/

    public void initResources() {
        // the game background
        background = new ColorBackground(Color.BLUE, 640, 480);

        // create our plane sprite
        plane = new AnimatedSprite(getImages("resources/plane2.png",3,1), 287.5, 395);
        plane.setAnimate(true);
        plane.setLoopAnim(true);


        // create the sprite groups
        PLAYER_GROUP     = new SpriteGroup("Player");
        PROJECTILE_GROUP = new SpriteGroup("Projectile");
        ENEMY_GROUP      = new SpriteGroup("Enemy");

        PLAYER_GROUP.setBackground(background);
        PROJECTILE_GROUP.setBackground(background);
        ENEMY_GROUP.setBackground(background);


        // insert sprites into the sprite group
        PLAYER_GROUP.add(plane);

        // initialize ENEMY_GROUP sprites
        BufferedImage image = getImage("resources/plane1.png");
        for (int j=0;j < 4;j++) {
            for (int i=0;i < 7;i++) {
                ENEMY_GROUP.add(new Sprite(image, 45+(i*80), 30+(j*70)));
            }
        }

        // the PROJECTILE_GROUP is currently empty
        // we'll add every projectile fired by the plane sprite to
        // this PROJECTILE_GROUP later
//        PROJECTILE_GROUP.add(...);


        // register the collision between PROJECTILE_GROUP <-> ENEMY_GROUP
        // see the collision class at bottom of this class
        collision = new ProjectileEnemyCollision(PROJECTILE_GROUP, ENEMY_GROUP);


        font = fontManager.getFont(getImages("resources/font.png", 20, 3),
                                   " !            .,0123" +
                                   "456789:   -? ABCDEFG" +
                                   "HIJKLMNOPQRSTUVWXYZ ");
    }



    public void update(long elapsedTime) {
        // update all
        background.update(elapsedTime);
        PLAYER_GROUP.update(elapsedTime);
        PROJECTILE_GROUP.update(elapsedTime);
        ENEMY_GROUP.update(elapsedTime);


        // check for collision!
        collision.checkCollision();


        // control the sprite with arrow key
        double speedX = 0;
        if (keyDown(KeyEvent.VK_LEFT))   speedX = -0.1;
        if (keyDown(KeyEvent.VK_RIGHT))  speedX = 0.1;
        plane.setHorizontalSpeed(speedX);


        // firing!!
        if (keyPressed(KeyEvent.VK_CONTROL)) {
            // create projectile sprite
            Sprite projectile = new Sprite(getImage("resources/projectile.png"));
            projectile.setLocation(plane.getX()+16.5, plane.getY()-16);
            projectile.setVerticalSpeed(-0.2);

            // add it to PROJECTILE_GROUP
            PROJECTILE_GROUP.add(projectile);
        }


        background.setToCenter(plane);
    }



    public void render(Graphics2D g) {
        // render all
        background.render(g);
        PLAYER_GROUP.render(g);
        PROJECTILE_GROUP.render(g);
        ENEMY_GROUP.render(g);


        // draw info text
        font.drawString(g, "ARROW KEY : MOVE", 10, 10);
        font.drawString(g, "CONTROL   : FIRE", 10, 30);
    }


 /****************************************************************************/
 /***************************** START-POINT **********************************/
 /****************************************************************************/

    public static void main(String[] args) {
        GameLoader game = new GameLoader();
        game.setup(new Tutorial10(), new Dimension(640,480), false);
        game.start();
    }

}


class ProjectileEnemyCollision extends BasicCollisionGroup {


    public ProjectileEnemyCollision(SpriteGroup group1, SpriteGroup group2) {
        // register the collision between what group to what group
        setCollisionGroup(group1, group2);
    }

    // when projectiles (in group a) collided with enemy (in group b)
    // what to do?
    public void collided(Sprite s1, Sprite s2) {
        // we kill/remove both sprite!
        s1.setActive(false); // the projectile is set to non-active
        s2.setActive(false); // the enemy is set to non-active
    }

}