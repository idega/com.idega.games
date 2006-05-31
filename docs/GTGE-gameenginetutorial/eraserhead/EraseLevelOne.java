package eraserhead;

import java.awt.*;
import java.awt.event.*;

import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;

public class EraseLevelOne extends GameObject
{

    private Sprite hero;
    private Background backgr;

    public EraseLevelOne(GameEngine parent)
    {
        super(parent);
    }

    @Override
    public void initResources()
    {
        hero = new Sprite(getImage("resources/eraserguy.png"));
        backgr = new ColorBackground(Color.WHITE, 640, 480);
        hero.setBackground(backgr);
    }

    @Override
    public void update(long elapsedTime)
    {
        backgr.update(elapsedTime);

        hero.update(elapsedTime);

        if (keyDown(KeyEvent.VK_ESCAPE))
        {
            parent.nextGameID = EraseGameEngine.INTRO;
            finish();
        }

        if (keyDown(KeyEvent.VK_LEFT))
        {
            hero.setX(hero.getX() - 1);
        }

        if (keyDown(KeyEvent.VK_RIGHT))
        {
            hero.setX(hero.getX() + 1);
        }

        if (keyDown(KeyEvent.VK_UP))
        {
            hero.setY(hero.getY() - 1);
        }

        if (keyDown(KeyEvent.VK_DOWN))
        {
            hero.setY(hero.getY() + 1);
        }
        
        if(keyDown(KeyEvent.VK_CONTROL))
        {
            ((EraseGameEngine) parent).score++;
        }
    }

    @Override
    public void render(Graphics2D g)
    {
        backgr.render(g);
        hero.render(g);
        g.setColor(Color.BLUE);
        g.drawString("Score: " + ((EraseGameEngine) parent).score, 20, 400);
    }
}
