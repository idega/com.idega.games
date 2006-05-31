package eraserhead;

import java.awt.*;
import java.awt.event.*;

import com.golden.gamedev.*;

public class EraseHelp extends GameObject
{
    public EraseHelp(GameEngine parent)
    {
        super(parent);
    }

    @Override
    public void initResources()
    {
    }

    @Override
    public void update(long elapsedTime)
    {

        if (keyPressed(KeyEvent.VK_ESCAPE))
        {
            parent.nextGameID = EraseGameEngine.INTRO;
            finish();
        }
    }

    @Override
    public void render(Graphics2D g)
    {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 640, 480);
        
        g.setColor(Color.BLUE);
        g.drawString("Use your arrow keys to move EraseHead around", 20, 20);
        g.drawString("Press your CTRL key for points!", 20, 50);
        g.drawString("Press ESC to return to main screen", 20, 80);
    }

}
