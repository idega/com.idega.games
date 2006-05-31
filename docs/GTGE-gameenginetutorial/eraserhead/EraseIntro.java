package eraserhead;

import java.awt.*;
import java.awt.event.*;

import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;

public class EraseIntro extends GameObject
{
    private Sprite hero;
    private Background backgr;

    public EraseIntro(GameEngine parent)
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
        hero.setLocation(300, 100);

        if (keyPressed(KeyEvent.VK_ESCAPE))
        {            
            finish();
        }

        if (keyDown(KeyEvent.VK_SPACE))
        {
            parent.nextGameID = EraseGameEngine.PLAY;
            finish();
        }

        if (keyDown(KeyEvent.VK_F1))
        {
            parent.nextGameID = EraseGameEngine.HELP;
            finish();
        }
    }

    @Override
    public void render(Graphics2D g)
    {
        backgr.render(g);
                
        g.setColor(Color.BLUE);
        g.drawString("Eraser Head - Copyright (c) Wicked Cool Games", 20, 20);
        g.drawString("Press F1 for Help", 20, 50);
        g.drawString("Press SPACE to Play", 20, 70);
        g.drawString("Last Score: " + ((EraseGameEngine) parent).score, 20, 400);
        hero.render(g);
    }
}
